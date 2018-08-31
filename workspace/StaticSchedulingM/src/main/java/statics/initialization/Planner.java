package statics.initialization;

import java.io.Serializable;

import cloud.InstanceSize;

public interface Planner extends Serializable {

    double getPlannedExecutionTime(SchedulingTask task, InstanceSize size);

}
