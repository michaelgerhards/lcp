package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

/**
 * lanes with later end times before lanes with earlier end times
 * @author Gerhards
 *
 */
public class LaneLaterExecutionEndTimeComparator implements Comparator<Lane>{

	@Override
	public int compare(Lane o1, Lane o2) {
		double executionTimeO1 = o1.getEndTime();
		double executionTimeO2 = o2.getEndTime();
		if (executionTimeO1 > executionTimeO2) {
			return -1;
		} else if (executionTimeO1 < executionTimeO2) {
			return 1;
		} else {
			return 0;
		}
		
		
	}

	
	
}
