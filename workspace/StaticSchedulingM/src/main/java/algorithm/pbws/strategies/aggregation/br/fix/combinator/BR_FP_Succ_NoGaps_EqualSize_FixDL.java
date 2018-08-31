package algorithm.pbws.strategies.aggregation.br.fix.combinator;

import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import algorithm.pbws.PBWSInit;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Util;

public class BR_FP_Succ_NoGaps_EqualSize_FixDL implements Aggregator {

	private final PBWSInit algorithm;

	public BR_FP_Succ_NoGaps_EqualSize_FixDL(PBWSInit algorithm) {
		this.algorithm = algorithm;
	}

	public AggregationResult tryCombineThrowerAfterCatcherEqualSizeNoGaps(
			Lane thrower, Lane catcher) {
		if(thrower.getInstanceSize() != catcher.getInstanceSize()) {
			throw new RuntimeException();
		}
		if (catcher.getUmodChildren().contains(thrower)
				|| catcher.getUmodParents().contains(thrower)) {
			throw new RuntimeException();
		}
		
		final double catcherEndtime = catcher.getEndTime();
		final double throwerStartTime = thrower.getStartTime();

		double throwerShiftTime = catcherEndtime - throwerStartTime;
		if (throwerShiftTime < -Util.DOUBLE_THRESHOLD) {
			return null;
		}

		double combinedCosts = thrower.getCost() + catcher.getCost();
		BillingUtil bu = BillingUtil.getInstance();
		double newCosts = bu
				.getCombinedCosts_EqualSize(catcher, thrower, throwerShiftTime);
		if (newCosts > combinedCosts) {
			return null; // XXX can never happen?
		}

		// serialization
		final double availableThrowerShiftTime = thrower.getRSWfix();
		if (throwerShiftTime > availableThrowerShiftTime) {
			boolean satisfyNewStartTimeFix = thrower.satisfyNewStartTimeFix(catcherEndtime);
			if(!satisfyNewStartTimeFix) {
				return null;
			}
		}

		AggregationResult result = new AggregationResult(this);
		result.setCatcher(catcher);
		result.setThrower(thrower);
		result.setThrowerShiftTime(throwerShiftTime);
		result.setNewCosts(newCosts);
		result.setOldCosts(combinedCosts);
		return result;
		
	}

	@Override
	public void performCombination(AggregationResult result) {
		Lane thrower = result.getThrower();
		Lane catcher = result.getCatcher();
		double throwerShiftTime = result.getThrowerShiftTime();

		thrower.shift(throwerShiftTime);
		catcher.reassignToEndFrom(thrower);
	}

}
