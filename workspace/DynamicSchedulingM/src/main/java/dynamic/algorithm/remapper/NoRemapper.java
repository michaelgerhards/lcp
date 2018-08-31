package dynamic.algorithm.remapper;

import java.util.Set;

import dynamic.scheduling.engine.WorkflowEngineImpl;
import reality.QueueEvent;
import reality.RealResource;
import statics.initialization.SchedulingTask;

public class NoRemapper implements Remapper {

	@Override
	public void initialize(WorkflowEngineImpl workflowEngineImpl) {
		// nothing
	}

	@Override
	public void notifyIdlingResouceAtBillingRaster(QueueEvent event) {
		// nothing
	}

	@Override
	public void notifyThatDeadlineIsViolated(QueueEvent event) {
		// nothing
	}

	@Override
	public void notifyThatTaskEnded(QueueEvent event, Set<SchedulingTask> newRdyTasks) {
		// nothing
	}

	@Override
	public void notifyThatTaskWillBeScheduled(SchedulingTask nextTaskToSchedule, RealResource resource) {
		// nothing
	}

}
