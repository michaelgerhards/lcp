package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

/**
 * Lanes with less paths before Lanes with more paths.
 * 
 * @author Gerhards
 * 
 */
public class LanePathCountComparator implements Comparator<Lane> {

	@Override
	public int compare(Lane o1, Lane o2) {
		return o2.getPathCount() - o1.getPathCount();
	}
}