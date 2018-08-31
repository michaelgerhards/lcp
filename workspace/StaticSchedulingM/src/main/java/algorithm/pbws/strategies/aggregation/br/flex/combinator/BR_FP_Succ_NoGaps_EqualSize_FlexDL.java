package algorithm.pbws.strategies.aggregation.br.flex.combinator;

import java.util.Set;

import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import algorithm.pbws.PBWSInit;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Debug;
import statics.util.Util;

public class BR_FP_Succ_NoGaps_EqualSize_FlexDL implements Aggregator {

	private final PBWSInit algorithm;

	public BR_FP_Succ_NoGaps_EqualSize_FlexDL(PBWSInit algorithm) {
		this.algorithm = algorithm;
	}

	public AggregationResult tryMigratePath_ThrowerAfterCatcher_EqualInstanceSizes(
			Lane catcher, Lane thrower) {
		if (thrower.getInstanceSize() != catcher.getInstanceSize()) {
			throw new RuntimeException();
		}
		if (catcher.getUmodChildren().contains(thrower)
				|| catcher.getUmodParents().contains(thrower)) {
			throw new RuntimeException();
		}

		double throwShift = catcher.getEndTime() - thrower.getStartTime();

		if (throwShift < Util.DOUBLE_THRESHOLD) {
			return null;
		}
		
		double oldCosts = thrower.getCost() + catcher.getCost();
		BillingUtil bu = BillingUtil.getInstance();
		double newCosts = bu
				.getCombinedCosts_EqualSize(catcher, thrower, throwShift);
		if (newCosts > oldCosts) {
			return null; // XXX can never happen?
		}

		double throwerGlobalShiftSpace = thrower.getRSWflex();
		if (throwerGlobalShiftSpace - throwShift < -Util.DOUBLE_THRESHOLD) {
			boolean satisfyNewStartTimeFix = thrower.satisfyNewStartTimeFix(catcher.getEndTime());
			if(!satisfyNewStartTimeFix) {
				return null;
			}
		}
		
		AggregationResult result = new AggregationResult(this);
		result.setCatcher(catcher);
		result.setThrower(thrower);
		result.setThrowerLastPath(thrower.getUmodTasks());
		result.setThrowerShiftTime(throwShift);
		result.setOldCosts(oldCosts);
		result.setNewCosts(newCosts);
		return result;
	}

	@Override
	public void performCombination(AggregationResult result) {
		Lane thrower = result.getThrower();
		Lane catcher = result.getCatcher();
		double throwShift = result.getThrowerShiftTime();

		algorithm.printer.printJoinShift(result, throwShift, 0);
		Debug.INSTANCE.println(Integer.MAX_VALUE, "shift join");

		Set<Lane> shifted = thrower
				.shiftEquallyRecursiveWithAllSuccessors(throwShift);
		shifted.add(catcher);
		shifted.add(thrower);
		shifted.addAll(thrower.getUmodChildren());
		shifted.addAll(catcher.getUmodChildren());
		result.setShifted(shifted);
		catcher.reassignToEndFrom(thrower);
	}

}
