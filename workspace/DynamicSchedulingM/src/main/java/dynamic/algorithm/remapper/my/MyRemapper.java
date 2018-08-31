package dynamic.algorithm.remapper.my;

import java.util.Set;

import dynamic.algorithm.remapper.Remapper;
import dynamic.scheduling.engine.WorkflowEngineImpl;
import reality.EventHandler;
import reality.EventType;
import reality.QueueEvent;
import dynamic.reality.QueueEventImpl;
import reality.RealResource;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.util.Debug;
import statics.util.Util;
import static dynamic.scheduling.engine.WorkflowEngineImpl.*;

public class MyRemapper implements Remapper {

    private WorkflowEngineImpl workflowEngineImpl;
    private ThresholdExceeded thresholdExceeded;
    private ResourceAdapterIdling resourceAdapterIdling;
    private DeadlineViolationPredicted deadlineViolationPredicted;
    private TerminateIfIdling terminateIfIdling;
    private DeadlineIsViolatedAdapter deadlineIsViolatedAdapter;
    EventHandler lastStartTimeExceededHandler;

    @Override
    public void initialize(WorkflowEngineImpl workflowEngineImpl) {
        this.workflowEngineImpl = workflowEngineImpl;
        resourceAdapterIdling = new ResourceAdapterIdling(workflowEngineImpl);
        deadlineViolationPredicted = new DeadlineViolationPredicted(workflowEngineImpl);
        terminateIfIdling = new TerminateIfIdling(workflowEngineImpl);
        deadlineIsViolatedAdapter = new DeadlineIsViolatedAdapter(workflowEngineImpl);
        thresholdExceeded = new ThresholdExceeded(workflowEngineImpl);
        lastStartTimeExceededHandler = new LastStartTimeExceededHandler(workflowEngineImpl, this);
        SchedulingTask entryTask = workflowEngineImpl.getPlan().getEntry();
        double entryDelayBuffer = entryTask.updateDelayBuffer();
        Debug.INSTANCE.println(DEBUG_COMPLETE, "entryDelayBuffer= " + entryDelayBuffer);
    }

    void notifyThatLatestStartingTimeExceeded(QueueEvent event) {
        // callback of LastStartTimeExceededHandler
        thresholdExceeded.adaptPlan_ThresholdExceeded(event);
    }

    @Override
    public void notifyIdlingResouceAtBillingRaster(QueueEvent event) {
        terminateIfIdling.terminateIfIdling(event);
    }

    @Override
    public void notifyThatDeadlineIsViolated(QueueEvent event) {
        deadlineIsViolatedAdapter.notifyThatDeadlineIsViolated();
    }

    @Override
    public void notifyThatTaskEnded(QueueEvent event, Set<SchedulingTask> newRdyTasks) {
        deadlineViolationPredicted.adaptateIfDeadlineViolationIsPredicted(event);
        resourceAdapterIdling.instanceIdlingAdoptTask(event.getTime());

        double eventTime = event.getTime();
        SchedulingTask task = event.getJob().getTask();
        // TODO not so good, maybe task start time is set to front
        for (SchedulingTask rdyTask : newRdyTasks) {
            if (rdyTask.getStartTime() - eventTime > Util.DOUBLE_THRESHOLD) {
                // planned start time is in future
                double st = rdyTask.getStartTime();
                double buf = task.getDelayBuffer();
                if (buf > 0) {
                    double t = st + buf;
                    QueueEventImpl rdyEvent = new QueueEventImpl(t, EventType.TASK_LAST_START_TIME_EXCEEDED, rdyTask, lastStartTimeExceededHandler);
                    Debug.INSTANCE.println(DEBUG_COMPLETE, "created " , rdyEvent);
                    Time.getInstance().addEvent(rdyEvent);
                }
            }
        }
    }

    @Override
    public void notifyThatTaskWillBeScheduled(SchedulingTask nextTaskToSchedule, RealResource resource) {
        double originalStartTime = nextTaskToSchedule.getStartTime();
        double diff = Time.getInstance().getActualTime() - originalStartTime;

        double buffer = nextTaskToSchedule.getDelayBuffer();
        buffer -= diff;
        nextTaskToSchedule.setDelayBuffer(buffer);

        double startTime = Time.getInstance().getActualTime();
        double sExTime = nextTaskToSchedule.getExecutionTime(resource.getInstanceSize());
        double predictedEndtime = startTime + sExTime;
        String s = String.format("schedule %10s on %5s at %10.3f pet=%10.3f buffer=%10.3f bufferinc=%10.3f", nextTaskToSchedule.getId(), resource.getName(), startTime, predictedEndtime, buffer, -diff);
        Debug.INSTANCE.println(DEBUG_SCHEDULE, s);

    }

}
