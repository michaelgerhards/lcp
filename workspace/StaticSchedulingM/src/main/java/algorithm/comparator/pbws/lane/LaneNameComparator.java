package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

public class LaneNameComparator implements Comparator<Lane>{

	@Override
	public int compare(Lane arg0, Lane arg1) {
		return arg0.getId().compareTo(arg1.getId());
	}

}
