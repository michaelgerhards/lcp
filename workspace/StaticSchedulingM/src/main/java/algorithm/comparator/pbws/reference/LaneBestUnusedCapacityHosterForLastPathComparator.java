package algorithm.comparator.pbws.reference;

import java.util.Comparator;

import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Util;

public class LaneBestUnusedCapacityHosterForLastPathComparator
		implements Comparator<Lane>, ReferenceComparator<Lane, Lane> {

	private Lane lane;

	@Override
	public void setReference(Lane t) {
		this.lane = t;

	}

	@Override
	public Lane getReference() {
		return lane;
	}

	@Override
	public int compare(Lane arg0, Lane arg1) {

		double lpExTimeOnArg0 = Util.getExecutionTimeOfLastPathUsingSdlFix(lane, arg0.getInstanceSize());

		BillingUtil bu = BillingUtil.getInstance();

		double unusedCapacityArg0 = bu.getUnusedCapacity(arg0.getExecutionTime() + lpExTimeOnArg0);

		double lpExTimeOnArg1 = Util.getExecutionTimeOfLastPathUsingSdlFix(lane, arg1.getInstanceSize());
		double unusedCapacityArg1 = bu.getUnusedCapacity(arg1.getExecutionTime() + lpExTimeOnArg1);

		if (unusedCapacityArg0 < unusedCapacityArg1) {
			return -1;
		} else if (unusedCapacityArg0 > unusedCapacityArg1) {
			return 1;
		} else {
			return 0;
		}
	}

}
