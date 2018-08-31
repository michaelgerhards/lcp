package algorithm.pbws2.strategies.aggregation.dr.fix.manager;

import algorithm.misc.DR_Manager_EqualSize;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws2.strategies.aggregation.dr.fix.combinator.DR_FP_Succ_GapsAll_EqualSize_FixedDL2;
import statics.initialization.impl.Lane;
import statics.util.Util;

public class DR_Manager_FP_Succ_GapsAll_EqualSize_FixedDL2 implements DR_Manager_EqualSize {

    public final DR_FP_Succ_GapsAll_EqualSize_FixedDL2 dR_FP_Succ_GapsAllowed_EqualSize_FixedDL;

    public DR_Manager_FP_Succ_GapsAll_EqualSize_FixedDL2() {
        dR_FP_Succ_GapsAllowed_EqualSize_FixedDL = new DR_FP_Succ_GapsAll_EqualSize_FixedDL2();
    }

    /**
     *
     * @param lane1
     * @param lane2
     * @return the deleted lane. Null if no lane was deleted
     */
    public AggregationResult checkEqualInstanceSize(Lane lane1, Lane lane2) {

        AggregationResult result1 = dR_FP_Succ_GapsAllowed_EqualSize_FixedDL.checkEqualInstanceSize(lane1, lane2);
        AggregationResult result2 = dR_FP_Succ_GapsAllowed_EqualSize_FixedDL.checkEqualInstanceSize(lane2, lane1);

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

        double result1Gap = Util.getGap(result1.getCatcher(), result1.getThrower(), result1.getThrowerShiftTime());
        double result2Gap = Util.getGap(result2.getCatcher(), result2.getThrower(), result2.getThrowerShiftTime());

        if (result1Gap < result2Gap) {
            return result1;
        } else {
            return result2;
        }

    }

}
