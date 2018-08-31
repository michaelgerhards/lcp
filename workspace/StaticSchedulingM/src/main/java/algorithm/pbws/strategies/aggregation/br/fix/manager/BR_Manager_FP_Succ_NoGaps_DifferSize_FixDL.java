package algorithm.pbws.strategies.aggregation.br.fix.manager;

import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws.PBWSInit;
import algorithm.pbws.strategies.aggregation.br.fix.combinator.BR_FP_Succ_NoGaps_DifferSize_CScale_FixDL;
import algorithm.pbws.strategies.aggregation.br.fix.combinator.BR_FP_Succ_NoGaps_DifferSize_TScale_FixDL;
import statics.initialization.impl.Lane;

public class BR_Manager_FP_Succ_NoGaps_DifferSize_FixDL {

    private final BR_FP_Succ_NoGaps_DifferSize_TScale_FixDL br_FP_Succ_NoGaps_DifferentSize_TScale_FixedDL;

    private final BR_FP_Succ_NoGaps_DifferSize_CScale_FixDL br_FP_Succ_NoGaps_DifferentSize_CScale_FixedDL;

    public BR_Manager_FP_Succ_NoGaps_DifferSize_FixDL(
            PBWSInit algorithm) {
        br_FP_Succ_NoGaps_DifferentSize_TScale_FixedDL = new BR_FP_Succ_NoGaps_DifferSize_TScale_FixDL();
        br_FP_Succ_NoGaps_DifferentSize_CScale_FixedDL = new BR_FP_Succ_NoGaps_DifferSize_CScale_FixDL();
    }

    /**
     *
     * @param lane1
     * @param lane2
     * @return
     */
    public AggregationResult checkDifferentInstanceSize(Lane lane1, Lane lane2) {
        AggregationResult result1 = scaleDown(lane1, lane2);
        AggregationResult result2 = scaleUp(lane1, lane2);
        return compare(result1, result2);
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
            newScaled = newLane2;
            constant = lane1;
        }

        return perform(scaled, newScaled, constant);
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
            newScaled = newLane2;
            constant = lane1;
        }
        if (newScaled == null) {
            return null;
        }
        return perform(scaled, newScaled, constant);
    }

    private AggregationResult perform(Lane scaled, ScaledPseudoLane newScaled,
            Lane constant) {
        AggregationResult result1 = br_FP_Succ_NoGaps_DifferentSize_TScale_FixedDL
                .tryCombineThrowerAfterCatcher_ThrowerScaleNoGaps(constant,
                        scaled, newScaled);
        AggregationResult result2 = br_FP_Succ_NoGaps_DifferentSize_CScale_FixedDL
                .tryCombineThrowerAfterCatcher_CatcherScaleNoGaps(scaled,
                        constant, newScaled);

        return compare(result1, result2);
    }

    private AggregationResult compare(AggregationResult result1,
            AggregationResult result2) {
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

}
