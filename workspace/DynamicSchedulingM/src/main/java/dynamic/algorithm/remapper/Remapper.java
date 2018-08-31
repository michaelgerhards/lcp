package dynamic.algorithm.remapper;

import java.util.Set;

import dynamic.scheduling.engine.WorkflowEngineImpl;
import reality.QueueEvent;
import reality.RealResource;
import statics.initialization.SchedulingTask;

public interface Remapper {

	void initialize(WorkflowEngineImpl workflowEngineImpl);

	void notifyIdlingResouceAtBillingRaster(QueueEvent event);

	void notifyThatDeadlineIsViolated(QueueEvent event);

	void notifyThatTaskEnded(QueueEvent event, Set<SchedulingTask> newRdyTasks);

	void notifyThatTaskWillBeScheduled(SchedulingTask nextTaskToSchedule, RealResource resource);

}