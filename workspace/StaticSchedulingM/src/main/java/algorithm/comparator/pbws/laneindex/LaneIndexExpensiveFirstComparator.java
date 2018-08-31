package algorithm.comparator.pbws.laneindex;

import java.util.Comparator;

import algorithm.comparator.pbws.lane.LaneExpensiveFirstComparator;
import statics.initialization.impl.LaneIndex;

/**
 * more expensive lane before less expensive one
 * 
 * @author Gerhards
 * 
 */
public class LaneIndexExpensiveFirstComparator implements Comparator<LaneIndex> {

	private static final LaneExpensiveFirstComparator laneExpensiveFirstComparator = new LaneExpensiveFirstComparator();

	@Override
	public int compare(LaneIndex o1, LaneIndex o2) {
		return laneExpensiveFirstComparator.compare(o1.getLane(), o2.getLane());
	}

}
