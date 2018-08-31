package algorithm.comparator.pbws.laneindex;

import java.util.Comparator;

import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;

/**
 * lanes with earlier start times before lanes with higher start times
 * @author Gerhards
 *
 */
public class LaneIndexLowerStartTimeComparator implements Comparator<LaneIndex>{

	@Override
	public int compare(LaneIndex o1i, LaneIndex o2i) {
		Lane o1 = o1i.getLane();
		Lane o2 = o2i.getLane();
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
