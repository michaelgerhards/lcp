package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

/**
 * lanes with earlier start times before lanes with later start times
 * @author Gerhards
 *
 */
public class LaneLowerStartTimeComparator implements Comparator<Lane>{
		
	@Override
	public int compare(Lane o1, Lane o2) {
		// TODO turn around?
		double startTimeO1 = o1.getStartTime();
		double startTimeO2 = o2.getStartTime();
		if (startTimeO1 > startTimeO2) {
			return 1;
		} else if (startTimeO1 < startTimeO2) {
			return -1;
		} else {
			return 0;
		}
		
		
	}

	
	
}
