package reality;

import statics.initialization.SchedulingTask;
import statics.util.Duration;

public interface RealJob extends Duration {

	double getExecutionTime();

	RealJobStatus getStatus();

	int getId();

	int getType();

	double getStartTime();

	double getEndTime();

	RealResource getResource();

	SchedulingTask getTask();

}