package algorithm.pbws.strategies.aggregation.dr.fix.manager;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws.strategies.aggregation.dr.fix.combinator.DR_FP_Succ_GapsAll_DifferSize_CScaleDown_FixedDL;
import algorithm.pbws.strategies.aggregation.dr.fix.combinator.DR_FP_Succ_GapsAll_DifferSize_TScale_FixedDL;
import statics.initialization.impl.Lane;
import statics.util.Util;

public class DR_Manager_FP_Succ_GapsAll_DifferSize_FixedDL implements DR_Manager_DifferSize {

	public final DR_FP_Succ_GapsAll_DifferSize_CScaleDown_FixedDL dR_FP_Succ_GapsAllowed_DifferentSize_CScale_FixedDL;
	public final DR_FP_Succ_GapsAll_DifferSize_TScale_FixedDL dR_FP_Succ_GapsAllowed_DifferentSize_TScale_FixedDL;

	public DR_Manager_FP_Succ_GapsAll_DifferSize_FixedDL() {
		dR_FP_Succ_GapsAllowed_DifferentSize_CScale_FixedDL = new DR_FP_Succ_GapsAll_DifferSize_CScaleDown_FixedDL(
				);
		dR_FP_Succ_GapsAllowed_DifferentSize_TScale_FixedDL = new DR_FP_Succ_GapsAll_DifferSize_TScale_FixedDL(
				);
	}

	/**
	 * 
	 * @param lane1
	 * @param lane2
	 * @return the deleted lane. Null if no lane was deleted
	 */
	public AggregationResult checkDifferentInstanceSize(Lane lane1, Lane lane2) {
		AggregationResult result1 = scaleUp(lane1, lane2);
		AggregationResult result2 = scaleDown(lane1, lane2);

		if (result1 == null) {
			return result2;
		}
		if (result2 == null) {
			return result1;
		}

		double result1Saved = result1.getSavedCosts();
		double result2Saved = result2.getSavedCosts();

		if (result1Saved > result2Saved) {
			return result1;
		} else if (result1Saved < result2Saved) {
			return result2;
		}
		return result1;
	}

	private AggregationResult scaleUp(Lane lane1, Lane lane2) {
		Lane scaled;
		ScaledPseudoLane newScaled;
		Lane constant;

		if (lane2.getInstanceSize().isFaster(lane1.getInstanceSize())) {
			// scale up lane1
			ScaledPseudoLane newLane1 = lane1.tryScalingIdlFix(lane2
					.getInstanceSize());
			scaled = lane1;
			constant = lane2;
			newScaled = newLane1;
		} else {
			// scale up lane2
			ScaledPseudoLane newLane2 = lane2.tryScalingIdlFix(lane1
					.getInstanceSize());
			scaled = lane2;
			constant = lane1;
			newScaled = newLane2;
		}

		AggregationResult result1 = dR_FP_Succ_GapsAllowed_DifferentSize_TScale_FixedDL
				.tryCombineThrowerAfterCatcher_ThrowerScaleUpGapsAllowed(
						constant, scaled, newScaled);
		return result1;
	}

	private AggregationResult scaleDown(Lane lane1, Lane lane2) {
		Lane scaled;
		ScaledPseudoLane newScaled;
		Lane constant;

		if (lane1.getInstanceSize().isFaster(lane2.getInstanceSize())) {
			// scale down lane1
			ScaledPseudoLane newLane1 = lane1.tryScalingIdlFix(lane2
					.getInstanceSize());
			scaled = lane1;
			constant = lane2;
			newScaled = newLane1;
		} else {
			// scale down lane2
			ScaledPseudoLane newLane2 = lane2.tryScalingIdlFix(lane1
					.getInstanceSize());

			scaled = lane2;
			constant = lane1;
			newScaled = newLane2;

		}

		if (newScaled == null) {
			return null;
		}

		AggregationResult result1 = dR_FP_Succ_GapsAllowed_DifferentSize_TScale_FixedDL
				.tryCombineThrowerAfterCatcher_ThrowerScaleUpGapsAllowed(
						constant, scaled, newScaled);
		AggregationResult result2 = dR_FP_Succ_GapsAllowed_DifferentSize_CScale_FixedDL
				.tryCombineThrowerAfterCatcher_CatcherScaleDown_GapsAllowed(
						scaled, constant, newScaled);

		if (result1 == null) {
			return result2;
		}
		if (result2 == null) {
			return result1;
		}

		double result1Saved = result1.getSavedCosts();
		double result2Saved = result2.getSavedCosts();

		if (result1Saved > result2Saved) {
			return result1;
		} else if (result1Saved < result2Saved) {
			return result2;
		}

		double result1Gap = Util.getGap(result1.getCatcher(),
				result1.getThrower(), result1.getThrowerShiftTime());
		double result2Gap = Util.getGap(result2.getCatcher(),
				result2.getThrower(), result2.getThrowerShiftTime());

		if (result1Gap < result2Gap) {
			return result1;
		} else {
			return result2;
		}
	}

}
