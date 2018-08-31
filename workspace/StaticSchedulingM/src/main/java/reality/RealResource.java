package reality;

import java.util.List;

import cloud.BasicInstance;
import cloud.Instance;
import cloud.InstanceSize;
import statics.initialization.SchedulingTask;

public interface RealResource extends BasicInstance<RealJob> {

	String getInfo();

	double getStartTime();

	double getEndTime();

	InstanceSize getInstanceSize();

	String getName();

	List<RealJob> getUmodTasks();

	RealResourceStatus getStatus();

	void terminate(double time);

	Instance getInstance();

	double getTerminatedTime();

	RealJob scheduleTask(SchedulingTask task, double taskStartTime, EventHandler jobCompletionHandler);

}