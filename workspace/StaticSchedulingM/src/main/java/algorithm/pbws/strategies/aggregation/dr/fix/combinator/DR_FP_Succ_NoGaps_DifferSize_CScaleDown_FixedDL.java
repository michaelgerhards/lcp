package algorithm.pbws.strategies.aggregation.dr.fix.combinator;

import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Util;

public class DR_FP_Succ_NoGaps_DifferSize_CScaleDown_FixedDL implements
		Aggregator {

	

	public AggregationResult tryCombineThrowerAfterCatcher_CatcherScaleNoGaps(
			Lane catcher, Lane thrower, ScaledPseudoLane newCatcher) {
		if (thrower.getInstanceSize().isFaster(catcher.getInstanceSize())
				|| thrower.getInstanceSize() == catcher.getInstanceSize()
				|| newCatcher.getInstanceSize() != thrower.getInstanceSize()) {
			throw new RuntimeException();
		}

		final double catcherEndtime = newCatcher.getEndTime();
		final double throwerStartTime = thrower.getStartTime();

		double throwerShiftTime = catcherEndtime - throwerStartTime;
		if (throwerShiftTime < -Util.DOUBLE_THRESHOLD) {
			return null;
		}

		double combinedCosts = thrower.getCost() + catcher.getCost();
		BillingUtil bu = BillingUtil.getInstance();
		double newCosts = bu
				.getCombinedCosts_DifferentSize(newCatcher, 0, thrower,
						throwerShiftTime);
		if (newCosts > combinedCosts) {
			return null;
		}

		final double availableThrowerShiftTime = newCatcher
				.getRSWfixOfParameter(thrower);
		if (throwerShiftTime > availableThrowerShiftTime) {
			return null;
		}

		AggregationResult result = new AggregationResult(this);
		result.setCatcher(catcher);
		result.setThrower(thrower);
		result.setThrowerShiftTime(throwerShiftTime);

		result.setNewCatcher(newCatcher);
		result.setNewCosts(newCosts);
		result.setOldCosts(combinedCosts);
		return result;
	}

	@Override
	public void performCombination(AggregationResult result) {

		Lane catcher = result.getCatcher();
		Lane thrower = result.getThrower();

		double throwerShiftTime = result.getThrowerShiftTime();

		ScaledPseudoLane newCatcher = result.getNewCatcher();

		thrower.shift(throwerShiftTime);
		catcher.scale(newCatcher);

		catcher.reassignToEndFrom(thrower);
	}
}
