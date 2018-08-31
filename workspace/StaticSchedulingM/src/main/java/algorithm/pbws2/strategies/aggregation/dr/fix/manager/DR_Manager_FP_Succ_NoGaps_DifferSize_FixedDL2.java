package algorithm.pbws2.strategies.aggregation.dr.fix.manager;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws2.PBWSInit2;
import algorithm.pbws2.strategies.aggregation.dr.fix.combinator.DR_FP_Succ_NoGaps_DifferSize_CScaleDown_FixedDL2;
import algorithm.pbws2.strategies.aggregation.dr.fix.combinator.DR_FP_Succ_NoGaps_DifferSize_TScale_FixedDL2;
import statics.initialization.impl.Lane;

public class DR_Manager_FP_Succ_NoGaps_DifferSize_FixedDL2 implements DR_Manager_DifferSize {

    private final DR_FP_Succ_NoGaps_DifferSize_TScale_FixedDL2 dR_FP_Succ_NoGaps_DifferentSize_TScale_FixedDL;
    private final DR_FP_Succ_NoGaps_DifferSize_CScaleDown_FixedDL2 dR_FP_Succ_NoGaps_DifferentSize_CScale_FixedDL;

    public DR_Manager_FP_Succ_NoGaps_DifferSize_FixedDL2(PBWSInit2 algorithm) {
        dR_FP_Succ_NoGaps_DifferentSize_TScale_FixedDL = new DR_FP_Succ_NoGaps_DifferSize_TScale_FixedDL2(
                algorithm);
        dR_FP_Succ_NoGaps_DifferentSize_CScale_FixedDL = new DR_FP_Succ_NoGaps_DifferSize_CScaleDown_FixedDL2(
                algorithm);
    }

    /**
     *
     * @param lane1
     * @param lane2
     * @return the deleted lane. Null if no lane was deleted
     */
    @Override
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

        AggregationResult result1 = dR_FP_Succ_NoGaps_DifferentSize_TScale_FixedDL
                .tryCombineThrowerAfterCatcher_ThrowerScaleNoGaps(constant,
                        scaled, newScaled);
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

        AggregationResult result1 = dR_FP_Succ_NoGaps_DifferentSize_TScale_FixedDL
                .tryCombineThrowerAfterCatcher_ThrowerScaleNoGaps(constant,
                        scaled, newScaled);
        AggregationResult result2 = dR_FP_Succ_NoGaps_DifferentSize_CScale_FixedDL
                .tryCombineThrowerAfterCatcher_CatcherScaleNoGaps(scaled,
                        constant, newScaled);

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
