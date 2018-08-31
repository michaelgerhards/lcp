package reality;

import statics.initialization.SchedulingTask;

public interface QueueEvent {

	RealJob getJob();

	double getTime();

	EventType getType();

	RealResource getResource();

	SchedulingTask getTask();

	EventHandler getEventHandler();

}