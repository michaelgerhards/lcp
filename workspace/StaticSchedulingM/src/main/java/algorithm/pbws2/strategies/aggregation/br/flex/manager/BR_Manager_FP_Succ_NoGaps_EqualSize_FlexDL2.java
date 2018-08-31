package algorithm.pbws2.strategies.aggregation.br.flex.manager;

import algorithm.misc.ResultSelector;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws2.PBWSInit2;
import algorithm.pbws2.strategies.aggregation.br.flex.combinator.BR_FP_Succ_NoGaps_EqualSize_FlexDL2;
import statics.initialization.impl.Lane;

public class BR_Manager_FP_Succ_NoGaps_EqualSize_FlexDL2 {

    private final BR_FP_Succ_NoGaps_EqualSize_FlexDL2 br_FP_Succ_NoGaps_EqualSize_FlexDL;
    private final ResultSelector selector;

    public BR_Manager_FP_Succ_NoGaps_EqualSize_FlexDL2(PBWSInit2 algorithm, ResultSelector selector) {
        this.selector = selector;
        br_FP_Succ_NoGaps_EqualSize_FlexDL = new BR_FP_Succ_NoGaps_EqualSize_FlexDL2(algorithm);
    }

    /**
     *
     * @param lane1
     * @param lane2
     * @return the deleted lane. Null if no lane was deleted
     */
    public AggregationResult checkEqualInstanceSize(Lane lane1, Lane lane2) {
        AggregationResult result1 = br_FP_Succ_NoGaps_EqualSize_FlexDL.tryMigratePath_ThrowerAfterCatcher_EqualInstanceSizes(lane1, lane2);
        AggregationResult result2 = br_FP_Succ_NoGaps_EqualSize_FlexDL.tryMigratePath_ThrowerAfterCatcher_EqualInstanceSizes(lane2, lane1);
        return selector.selectResult(result1, result2);
    }
}
