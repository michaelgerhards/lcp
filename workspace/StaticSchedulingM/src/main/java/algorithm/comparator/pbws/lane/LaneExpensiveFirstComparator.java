package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;
import statics.util.BillingUtil;

/**
 * more expensive lane before less expensive one
 * 
 * @author Gerhards
 * 
 */
public class LaneExpensiveFirstComparator implements Comparator<Lane> {

	private static final LaneHigherExecutionTimeComparator laneHigherExecutionTimeComparator = new LaneHigherExecutionTimeComparator();


	@Override
	public int compare(Lane o1, Lane o2) {
		double o1Costs = o1.getCost();
		double o2Costs = o2.getCost();

		if (o1Costs > o2Costs) {
			return -1;
		} else if (o1Costs < o2Costs) {
			return 1;
		}

		int result = laneHigherExecutionTimeComparator.compare(o1, o2);

		if (result != 0) {
			return result;
		}

		BillingUtil bu = BillingUtil.getInstance();
		double uc1 = bu.getUnusedCapacity(o1Costs);
		double uc2 = bu.getUnusedCapacity(o2Costs);

		if (uc1 > uc2) {
			return -1;
		} else if (uc1 < uc2) {
			return 1;
		} else {
			return 0;
		}

	}

}
