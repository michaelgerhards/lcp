package reality.executiontimes;

import cloud.InstanceSize;
import statics.initialization.SchedulingTask;

public interface ExecutionTimes {

    double getExecutionTime(SchedulingTask task, InstanceSize size);

}
