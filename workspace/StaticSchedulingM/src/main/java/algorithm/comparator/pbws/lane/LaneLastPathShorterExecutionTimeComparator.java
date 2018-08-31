package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

/**
 * lanes with shorter execution time on last path before lanes with shorter
 * execution time
 * 
 * @author Gerhards
 * 
 */
public class LaneLastPathShorterExecutionTimeComparator implements
		Comparator<Lane> {

	private LaneLastPathHigherExecutionTimeComparator laneLastPathHigherExecutionTimeComparator;

	public LaneLastPathShorterExecutionTimeComparator() {
		laneLastPathHigherExecutionTimeComparator = new LaneLastPathHigherExecutionTimeComparator();
	}

	@Override
	public int compare(Lane o1, Lane o2) {
		return -laneLastPathHigherExecutionTimeComparator.compare(o1, o2);
	}

}
