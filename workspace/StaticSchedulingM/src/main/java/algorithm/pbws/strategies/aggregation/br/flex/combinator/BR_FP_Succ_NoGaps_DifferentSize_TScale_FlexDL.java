package algorithm.pbws.strategies.aggregation.br.flex.combinator;

import algorithm.misc.ScaledPseudoLane;
import java.util.Set;

import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import algorithm.pbws.PBWSInit;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Debug;
import statics.util.Util;

public class BR_FP_Succ_NoGaps_DifferentSize_TScale_FlexDL implements
		Aggregator {

	private final PBWSInit algorithm;

	public BR_FP_Succ_NoGaps_DifferentSize_TScale_FlexDL(PBWSInit algorithm) {
		this.algorithm = algorithm;
	}

	public AggregationResult tryMigrateFullPath_ThrowerAfterCatcher_ThrowerScale_DifferentInstanceSize(
			Lane catcher, Lane thrower, ScaledPseudoLane newThrower) {
		if (catcher.getInstanceSize() == thrower.getInstanceSize()
				|| catcher.getInstanceSize() != newThrower.getInstanceSize()) {
			throw new RuntimeException();
		}
		if (catcher.getUmodChildren().contains(thrower)
				|| catcher.getUmodParents().contains(thrower)) {
			throw new RuntimeException();
		}

		double throwShift = catcher.getEndTime() - newThrower.getStartTime();

		if (throwShift < Util.DOUBLE_THRESHOLD) {
			return null;
		}

		double origCombinedCosts = thrower.getCost() + catcher.getCost();
		BillingUtil bu = BillingUtil.getInstance();
		double newCombinedCosts =bu
				.getCombinedCosts_DifferentSize(catcher, newThrower, throwShift);

		if (newCombinedCosts > origCombinedCosts) {
			return null;
		}

		if (!newThrower.hasGap()) {
			final double availableThrowerShiftTime = newThrower.getRSWflex();
			if (throwShift > availableThrowerShiftTime) {
				return null;
			}
		} else {
			double time = catcher.getEndTime();
			boolean satisfyNewStartTimeFlex = newThrower.satisfyNewStartTimeFlex(time);
			if(!satisfyNewStartTimeFlex) {
				return null;
			}
		}

		AggregationResult result = new AggregationResult(this);
		result.setCatcher(catcher);
		result.setThrower(thrower);
		result.setThrowerLastPath(thrower.getUmodTasks()); // XXX remove?
		result.setNewThrower(newThrower);
		result.setThrowerShiftTime(throwShift);
		result.setNewCosts(newCombinedCosts);
		result.setOldCosts(origCombinedCosts);
		return result;
	}

	@Override
	public void performCombination(AggregationResult result) {
		Lane thrower = result.getThrower();
		Lane catcher = result.getCatcher();
		ScaledPseudoLane newThrower = result.getNewThrower();
		double throwShift = result.getThrowerShiftTime();

		Set<Lane> shifted = thrower.prepareSuccessorsForOwnVertScale(
				newThrower, throwShift);
		shifted.add(catcher);
		shifted.add(thrower);
		shifted.addAll(thrower.getUmodChildren());
		shifted.addAll(catcher.getUmodChildren());
		result.setShifted(shifted);

		thrower.scale(newThrower);
		algorithm.printer.printJoinShift(result, throwShift, 0);
		thrower.shift(throwShift);

		Debug.INSTANCE.println(Integer.MAX_VALUE, "shift join");
		catcher.reassignToEndFrom(thrower);
	}

}
