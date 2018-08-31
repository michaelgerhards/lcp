package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

/**
 * forks with more branches before forks with less branches.
 * 
 * 
 * @author Gerhards
 * 
 */
public class ForkLaneComparatorLargestFirst implements Comparator<Lane> {

	@Override
	public int compare(Lane arg0, Lane arg1) {
		return arg1.getUmodChildren().size()
				- arg0.getUmodChildren().size();

	}

}
