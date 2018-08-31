package dynamic.scheduling.engine.eventhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cloud.Instance;
import dynamic.algorithm.remapper.Remapper;
import dynamic.scheduling.engine.WorkflowEngineImpl;
import reality.EventHandler;
import reality.EventType;
import reality.QueueEvent;
import dynamic.reality.QueueEventImpl;
import reality.RealJob;
import reality.RealResource;
import dynamic.reality.RealResourceManagerImpl;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.Debug;
import statics.util.Util;
import static dynamic.scheduling.engine.WorkflowEngineImpl.*;

public class JobCompletedHandler implements EventHandler {

	private final WorkflowEngineImpl engine;
	private final Remapper remapper;

	public JobCompletedHandler(WorkflowEngineImpl engine, Remapper remapper) {
		this.engine = engine;
		this.remapper = remapper;

	}

	@Override
	public void handleEvent(QueueEvent event) {
		print(event);
		double eventTime = event.getTime();
		SchedulingTask task = event.getJob().getTask();
		Set<SchedulingTask> rdyChildren = processTaskEnd(event);

		boolean violated = iskDeadlineViolated();

		boolean completed = checkWorkflowCompleted(task);
		if (completed) {
			Time time = Time.getInstance();
			EventHandler endEventHandler = new WorkflowCompletedHandler();
			QueueEventImpl endevent = new QueueEventImpl(eventTime, EventType.WORKFLOW_COMPLETED, task,
					endEventHandler);
			time.addEvent(endevent);
			return;
		}
		if (violated) {
			remapper.notifyThatDeadlineIsViolated(event);
		}
		remapper.notifyThatTaskEnded(event, rdyChildren);
		engine.getCloudManager().instanceReceiveScheduledTask();
	}

	private void print(QueueEvent event) {
		double eventTime = event.getTime();
		SchedulingTask task = event.getJob().getTask();
		double diff = eventTime - task.getPredictedEndtime();
		diff = Util.round2Digits(diff);
		double ex = eventTime - task.getStartTime();
		RealResource resource = event.getResource();
		String out = String.format(
				"EVENT\tTask completed: %10s %25s on %5s at %10.3f predicted at: %10.3f ex=%10.3f  %5.2f%% \t%s",
				task.getId(), task.getType(), resource.toString(), eventTime, task.getPredictedEndtime(), ex,
				ex / (task.getPredictedEndtime() - task.getStartTime()) * 100.,
				(eventTime > task.getPredictedEndtime() ? " predicted time exceeded! " + diff : ""));
		Debug.INSTANCE.println(DEBUG_COMPLETE, out);
	}

	private Set<SchedulingTask> processTaskEnd(QueueEvent event) {
		double eventTime = event.getTime();
		SchedulingTask task = event.getJob().getTask();
		task.setEndTime(eventTime);
		Set<SchedulingTask> rdyChildren = task.goCompleted();
		return rdyChildren;
	}

	private boolean iskDeadlineViolated() {
		if (engine.isAdapt()) {
			double time = Time.getInstance().getActualTime();
			double dl = engine.getPlan().getDeadline();
			double diff = time - dl;
			if (diff > Util.DOUBLE_THRESHOLD) {
				Debug.INSTANCE.println(DEBUG_ALERT, "stop adaptation: deadline is violated: dl= ", dl, " ms= ", time);
				engine.setAdapt(false);
				return true;
			}
		}
		return false;
	}

	private boolean checkWorkflowCompleted(SchedulingTask task) {
		WorkflowInstance plan = engine.getPlan();

		if (task == plan.getExit()) {
			engine.getCloudManager().terminate(plan.getDummyInstance());
			RealResourceManagerImpl resourceManager = engine.getCloudManager().getResourceManager();
			List<RealResource> idlingResources = new ArrayList<RealResource>(resourceManager.getInstanceIdling());
			for (RealResource idlingResource : idlingResources) {
				Instance instance = idlingResource.getInstance();
				engine.getCloudManager().terminate(instance);
			}

			boolean b1 = resourceManager.getInstanceReady().size() > 0;
			boolean b2 = resourceManager.getInstanceReadyToBoot().size() > 0;
			boolean b3 = resourceManager.getInstanceRunning().size() > 0;
			if (b1 || b2 || b3) {
				throw new RuntimeException(
						"rexit terminated but resources are active: rdy, rdy2b, ru " + b1 + " " + b2 + " " + b3);
			}
			return true;
		} else {
			return false;
		}
	}

}
