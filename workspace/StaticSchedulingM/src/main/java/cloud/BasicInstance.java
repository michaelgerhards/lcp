package cloud;


import java.util.List;

import statics.initialization.WorkflowInstance;
import statics.util.Duration;
import statics.util.Util;

public interface BasicInstance<T> extends Duration, Comparable<BasicInstance<T>> {
	
	InstanceSize getInstanceSize();

	String getName();
	
	default double getExecutionTime() {
		return Util.round2Digits(getEndTime() - getStartTime());
	}
	
	List<T> getUmodTasks();
	
	Object getStatus();
	
	@Override
	default int compareTo(BasicInstance<T> o) {
		return getName().compareTo(o.getName());
	}
	
	double getTerminatedTime();
	
	public WorkflowInstance getWorkflow();
	
}
