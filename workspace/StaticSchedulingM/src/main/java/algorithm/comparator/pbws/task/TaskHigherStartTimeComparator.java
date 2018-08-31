package algorithm.comparator.pbws.task;

import java.util.Comparator;

import statics.initialization.SchedulingTask;

/**
 * Tasks with later start times before tasks with earlier start times
 * 
 * @author Gerhards
 *
 */
public class TaskHigherStartTimeComparator implements
		Comparator<SchedulingTask> {

	@Override
	public int compare(SchedulingTask o1, SchedulingTask o2) {
		double startTimeO1 = o1.getStartTime();
		double startTimeO2 = o2.getStartTime();
		if (startTimeO1 > startTimeO2) {
			return -1;
		} else if (startTimeO1 < startTimeO2) {
			return 1;
		} else {
			return 0;
		}

	}

}
