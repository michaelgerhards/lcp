package statics.initialization;

import java.util.List;
import java.util.Map;

import cloud.InstanceSize;

public interface SchedulingInformation {

	abstract Map<InstanceSize, Double> getExecutionTimes(DependencyTask task);

	abstract List<InstanceSize> getSizes();

	abstract double getAtuLength();

	abstract Map<InstanceSize, Double> getVariances(DependencyTask task);

}