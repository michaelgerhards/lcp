package dynamic.algorithm.remapper.my;

import dynamic.scheduling.engine.WorkflowEngineImpl;
import reality.EventHandler;
import reality.EventType;
import reality.QueueEvent;
import dynamic.reality.QueueEventImpl;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.util.Debug;
import statics.util.Util;

import static dynamic.scheduling.engine.WorkflowEngineImpl.*;

class LastStartTimeExceededHandler implements EventHandler {

	private final WorkflowEngineImpl engine;
	private final MyRemapper remapper;

	public LastStartTimeExceededHandler(WorkflowEngineImpl engine, MyRemapper remapper) {
		this.engine = engine;
		this.remapper = remapper;

	}

	@Override
	public void handleEvent(QueueEvent event) {
		double eventTime = event.getTime();
		SchedulingTask task = event.getTask();
		TaskStatus status = task.getStatus();
		if (status == TaskStatus.READY) {
			double st = task.getStartTime();
			double buf = task.getDelayBuffer();
			double latestST = st + buf;

			if (st - eventTime > Util.DOUBLE_THRESHOLD) {
				// XXX find other way to see if event is still active
				// start was set into future by adaptation, set new
				// timer
				// TODO put this whole stuff into the remapper
				QueueEventImpl rdyEvent = new QueueEventImpl(latestST, EventType.TASK_LAST_START_TIME_EXCEEDED, task,
						this);
				Debug.INSTANCE.println(DEBUG_COMPLETE, "created " + rdyEvent); // XXX
																				// remove
				Time time = Time.getInstance();
				time.addEvent(rdyEvent);
			} else {
				// scale out to avoid delay
				Debug.INSTANCE.println(DEBUG_COMPLETE,
						"TODO adaptate: scale-out at " + eventTime + " for task: " + task);
				remapper.notifyThatLatestStartingTimeExceeded(event);
			}
		}

	}

}
