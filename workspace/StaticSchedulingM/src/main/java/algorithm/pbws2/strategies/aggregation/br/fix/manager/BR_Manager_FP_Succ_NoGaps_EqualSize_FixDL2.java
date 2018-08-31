package algorithm.pbws2.strategies.aggregation.br.fix.manager;

import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws2.PBWSInit2;
import algorithm.pbws2.strategies.aggregation.br.fix.combinator.BR_FP_Succ_NoGaps_EqualSize_FixDL2;
import statics.initialization.impl.Lane;

public class BR_Manager_FP_Succ_NoGaps_EqualSize_FixDL2 {

    public final BR_FP_Succ_NoGaps_EqualSize_FixDL2 br_FP_Succ_NoGaps_EqualSize_FixedDL;

    public BR_Manager_FP_Succ_NoGaps_EqualSize_FixDL2(PBWSInit2 algorithm) {
        br_FP_Succ_NoGaps_EqualSize_FixedDL = new BR_FP_Succ_NoGaps_EqualSize_FixDL2();
    }

    /**
     *
     * @param lane1
     * @param lane2
     * @return the deleted lane. Null if no lane was deleted
     */
    public AggregationResult checkEqualInstanceSize(Lane lane1, Lane lane2) {
        AggregationResult result1 = br_FP_Succ_NoGaps_EqualSize_FixedDL.tryCombineThrowerAfterCatcherEqualSizeNoGaps(lane1, lane2);
        AggregationResult result2 = br_FP_Succ_NoGaps_EqualSize_FixedDL.tryCombineThrowerAfterCatcherEqualSizeNoGaps(lane2, lane1);

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
