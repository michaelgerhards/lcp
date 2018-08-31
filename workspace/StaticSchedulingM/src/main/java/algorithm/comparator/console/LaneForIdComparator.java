package algorithm.comparator.console;

import java.util.Comparator;

import statics.initialization.impl.Lane;

public class LaneForIdComparator implements Comparator<Lane> {

	@Override
	public int compare(Lane o1, Lane o2) {
		int result = o1.getId().compareTo(o2.getId());
		return result;
	}

}
