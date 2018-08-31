package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

/**
 * joins with less branches before joins with more branches
 * 
 * @author Gerhards
 * 
 */
public class JoinLaneComparatorSmallestFirst extends JoinLaneComparatorLargestFirst implements Comparator<Lane> {

	@Override
	public int compare(Lane arg0, Lane arg1) {
		return -super.compare(arg0, arg1);
	}

}
