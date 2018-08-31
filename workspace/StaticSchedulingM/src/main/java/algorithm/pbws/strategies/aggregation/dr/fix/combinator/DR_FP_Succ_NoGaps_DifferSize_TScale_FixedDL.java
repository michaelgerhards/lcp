package algorithm.pbws.strategies.aggregation.dr.fix.combinator;

import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Util;

public class DR_FP_Succ_NoGaps_DifferSize_TScale_FixedDL implements Aggregator {

	public AggregationResult tryCombineThrowerAfterCatcher_ThrowerScaleNoGaps(
			Lane catcher, Lane thrower, ScaledPseudoLane newThrower) {
		if (catcher.getInstanceSize() == thrower.getInstanceSize()
				|| newThrower.getInstanceSize() != catcher.getInstanceSize()) {
			throw new RuntimeException();
		}
		final double catcherEndtime = catcher.getEndTime();
		final double throwerStartTime = newThrower.getStartTime();

		double throwerShiftTime = catcherEndtime - throwerStartTime;
		if (throwerShiftTime < -Util.DOUBLE_THRESHOLD) {
			return null;
		}

		double combinedCosts = thrower.getCost() + catcher.getCost();
		BillingUtil bu = BillingUtil.getInstance();
		double newCosts = bu
				.getCombinedCosts_DifferentSize(catcher, newThrower, throwerShiftTime);
		if (newCosts > combinedCosts) {
			return null;
		}

		// serialization
		if (!newThrower.hasGap()) {
			final double availableThrowerShiftTime = newThrower.getRSWfix();
			if (throwerShiftTime > availableThrowerShiftTime) {
				return null;
			}
		} else {
			
			double time = catcher.getEndTime();
			boolean satisfyNewStartTime = newThrower.satisfyNewStartTimeFix(time);
			if(!satisfyNewStartTime) {
				return null;
			}

		}

		AggregationResult result = new AggregationResult(this);
		result.setCatcher(catcher);
		result.setThrower(thrower);
		result.setThrowerShiftTime(throwerShiftTime);
		result.setNewThrower(newThrower);
		result.setNewCosts(newCosts);
		result.setOldCosts(combinedCosts);
		return result;
	}

	@Override
	public void performCombination(AggregationResult result) {
		Lane catcher = result.getCatcher();
		Lane thrower = result.getThrower();
		double throwerShiftTime = result.getThrowerShiftTime();
		ScaledPseudoLane newThrower = result.getNewThrower();
		thrower.scale(newThrower);
		thrower.shift(throwerShiftTime);
		catcher.reassignToEndFrom(thrower);
		Lane.inverseShiftRecursiveForSuccessorsOf(catcher);
	}

}
