package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;
import statics.initialization.impl.SchedulingTaskList;

/**
 * lanes with longer execution time on last path before lanes with shorter
 * execution time
 * 
 * @author Gerhards
 * 
 */
public class LaneLastPathHigherExecutionTimeComparator implements
		Comparator<Lane> {

	@Override
	public int compare(Lane o1, Lane o2) {
		SchedulingTaskList o1LastPath = o1.getUmodLastPath();
		double o1ExecutionTime = o1LastPath.getExecutionTime();
		SchedulingTaskList o2LastPath = o2.getUmodLastPath();
		double o2ExecutionTime = o2LastPath.getExecutionTime();

		double executionTimeO1 = o1ExecutionTime;
		double executionTimeO2 = o2ExecutionTime;

		if (executionTimeO1 > executionTimeO2) {
			return -1;
		} else if (executionTimeO1 < executionTimeO2) {
			return 1;
		} else {
			return 0;
		}
	}

}
