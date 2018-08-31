package algorithm.comparator.path;

import java.util.Comparator;
import java.util.Map;

import statics.initialization.SchedulingTask;

public class StarttimeComparator implements Comparator<SchedulingTask> {

	private Map<SchedulingTask, Double> localTaskStarttimes;

	public StarttimeComparator(Map<SchedulingTask, Double> localTaskStarttimes) {
		this.localTaskStarttimes = localTaskStarttimes;
	}

	@Override
	public int compare(SchedulingTask o1, SchedulingTask o2) {
		double diff = localTaskStarttimes.get(o1) - localTaskStarttimes.get(o2);
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		} else {
			return 0;
		}
	}

}
