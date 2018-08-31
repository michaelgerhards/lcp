package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;

/**
 * joins with more branches before joins with less branches.
 * Ties are broken by lower est before later est
 * 
 * @author Gerhards
 * 
 */
public class JoinLaneComparatorLargestFirst implements Comparator<Lane> {


	@Override
	public int compare(Lane arg0, Lane arg1) {
		// TODO consider join of tasks and not only join of lanes!
		int diff = arg1.getUmodParents().size()
				- arg0.getUmodParents().size();
		if (diff != 0) {
			return diff;
		} else {
			double diff2 = arg0.getStartTime() - arg1.getStartTime();
			if (diff2 == 0) {
				return 0;
			} else if (diff2 > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}

}
