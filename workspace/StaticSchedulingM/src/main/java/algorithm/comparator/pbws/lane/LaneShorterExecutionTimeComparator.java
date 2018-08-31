package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

/**
 * lanes with shorter execution time before lanes with shorter execution time
 * 
 * @author Gerhards
 * 
 */
public class LaneShorterExecutionTimeComparator implements Comparator<Lane> {

	private LaneHigherExecutionTimeComparator laneHigherExecutionTimeComparator;

	public LaneShorterExecutionTimeComparator() {
		laneHigherExecutionTimeComparator = new LaneHigherExecutionTimeComparator();
	}

	@Override
	public int compare(Lane o1, Lane o2) {
		return -laneHigherExecutionTimeComparator.compare(o1, o2);
	}

}
