package algorithm.pbws.strategies.aggregation.br.flex.manager;

import algorithm.misc.ResultSelector;
import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws.PBWSInit;
import algorithm.pbws.strategies.aggregation.br.flex.combinator.BR_FP_Succ_NoGaps_DifferentSize_TScale_FlexDL;
import statics.initialization.impl.Lane;

public class BR_Manager_FP_Succ_NoGaps_DifferSize_FlexDL {

	private final BR_FP_Succ_NoGaps_DifferentSize_TScale_FlexDL br_FP_Succ_NoGaps_DifferentSize_TScale_FlexDL;
	private final ResultSelector selector;

	public BR_Manager_FP_Succ_NoGaps_DifferSize_FlexDL(PBWSInit algorithm,
			ResultSelector selector) {
		this.selector = selector;
		br_FP_Succ_NoGaps_DifferentSize_TScale_FlexDL = new BR_FP_Succ_NoGaps_DifferentSize_TScale_FlexDL(
				algorithm);
	}

	/**
	 * 
	 * @param lane1
	 * @param lane2
	 * @return 
	 */
	public AggregationResult checkDifferentInstanceSize(Lane lane1, Lane lane2) {

		AggregationResult bestResult = null;

		ScaledPseudoLane newLane1 = lane1.tryScalingIdlFlex(lane2
				.getInstanceSize());
		if (newLane1 != null) {
			AggregationResult result = br_FP_Succ_NoGaps_DifferentSize_TScale_FlexDL
					.tryMigrateFullPath_ThrowerAfterCatcher_ThrowerScale_DifferentInstanceSize(
							lane2, lane1, newLane1);
			bestResult = selector.selectResult(bestResult, result);
		}

		ScaledPseudoLane newLane2 = lane2.tryScalingIdlFlex(lane1
				.getInstanceSize());
		if (newLane2 != null) {
			AggregationResult result = br_FP_Succ_NoGaps_DifferentSize_TScale_FlexDL
					.tryMigrateFullPath_ThrowerAfterCatcher_ThrowerScale_DifferentInstanceSize(
							lane1, lane2, newLane2);
			bestResult = selector.selectResult(bestResult, result);
		}

		return bestResult;
	}

}
