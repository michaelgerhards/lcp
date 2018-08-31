package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

/**
 * forks with less branches before forks with more branches
 * 
 * @author Gerhards
 * 
 */
public class ForkLaneComparatorSmallestFirst extends ForkLaneComparatorLargestFirst implements Comparator<Lane> {

	@Override
	public int compare(Lane arg0, Lane arg1) {
		return -super.compare(arg0, arg1);
	}

}
