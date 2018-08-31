package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

/**
 * lanes with longer execution time before lanes with shorter execution time
 * 
 * @author Gerhards
 * 
 */
public class LaneHigherExecutionTimeComparator implements Comparator<Lane> {

	@Override
	public int compare(Lane o1, Lane o2) {
		double executionTimeO1 = o1.getExecutionTime();
		double executionTimeO2 = o2.getExecutionTime();
		if (executionTimeO1 > executionTimeO2) {
			return -1;
		} else if (executionTimeO1 < executionTimeO2) {
			return 1;
		} else {
			return 0;
		}
	}

}
