package reality.executiontimes;

import cloud.InstanceSize;
import statics.initialization.SchedulingTask;

public class AllNoVarianceExecutionTimes implements ExecutionTimes {

    @Override
    public double getExecutionTime(SchedulingTask task, InstanceSize size) {
        return task.getExecutionTime(size);
    }

}
