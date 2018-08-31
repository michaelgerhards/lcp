package algorithm.pbws2.strategies.aggregation.dr.fix.manager;

import algorithm.misc.DR_Manager_EqualSize;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws2.PBWSInit2;
import algorithm.pbws2.strategies.aggregation.dr.fix.combinator.DR_FP_Succ_NoGaps_EqualSize_FixedDL2;
import statics.initialization.impl.Lane;

public class DR_Manager_FP_Succ_NoGaps_EqualSize_FixedDL2 implements DR_Manager_EqualSize {

    public final DR_FP_Succ_NoGaps_EqualSize_FixedDL2 dR_FP_Succ_NoGaps_EqualSize_FixedDL_CatchUp;

    public DR_Manager_FP_Succ_NoGaps_EqualSize_FixedDL2(PBWSInit2 algorithm) {
        dR_FP_Succ_NoGaps_EqualSize_FixedDL_CatchUp = new DR_FP_Succ_NoGaps_EqualSize_FixedDL2(
                algorithm);
    }

    /**
     *
     * @param lane1
     * @param lane2
     * @return the deleted lane. Null if no lane was deleted
     */
    public AggregationResult checkEqualInstanceSize(Lane lane1, Lane lane2) {

        AggregationResult result1 = dR_FP_Succ_NoGaps_EqualSize_FixedDL_CatchUp
                .checkEqualInstanceSize(lane1, lane2);
        AggregationResult result2 = dR_FP_Succ_NoGaps_EqualSize_FixedDL_CatchUp
                .checkEqualInstanceSize(lane2, lane1);

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

        double result1ShiftTime = result1.getThrowerShiftTime();
        double result2ShiftTime = result2.getThrowerShiftTime();

        if (result1ShiftTime < result2ShiftTime) {
            return result1;
        } else {
            return result2;
        }

    }

}
