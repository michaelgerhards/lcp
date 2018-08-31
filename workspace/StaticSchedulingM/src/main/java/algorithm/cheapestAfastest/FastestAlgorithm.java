package algorithm.cheapestAfastest;

import algorithm.AbstractAlgorithm;
import cloud.InstanceSize;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;

public class FastestAlgorithm extends AbstractAlgorithm {

	@Override
	public void scheduleIntern() {
            getWorkflow().getTasks().stream().filter((task) -> (task != getWorkflow().getEntry()					&& task != getWorkflow().getExit())).forEach((task) -> {
                schedule(task);
            });
	}

	private void schedule(SchedulingTask task) {
		InstanceSize size = getCloudUtil().getFastestSize(task);
		Lane l = getWorkflow().instantiate(size);
		l.addTaskAtEnd(task);
	}

	@Override
	public String getAlgorithmName() {
		return "Fastest Algorithm";
	}

	@Override
	public String getAlgorithmNameAbbreviation() {
		return "FASTEST";
	}

}
