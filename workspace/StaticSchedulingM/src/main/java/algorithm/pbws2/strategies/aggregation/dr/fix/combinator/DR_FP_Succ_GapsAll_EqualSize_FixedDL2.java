package algorithm.pbws2.strategies.aggregation.dr.fix.combinator;

import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Debug;
import statics.util.Util;

public class DR_FP_Succ_GapsAll_EqualSize_FixedDL2 implements Aggregator {

    /**
     *
     * @param thrower
     * @param catcher
     * @return thrower if thrower is applicable, else null
     */
    public AggregationResult checkEqualInstanceSize(Lane thrower, Lane catcher) {
        if (thrower.getInstanceSize() != catcher.getInstanceSize()) {
            throw new RuntimeException();
        }

        final double catcherEndtime = catcher.getEndTime();
        final double throwerStartTime = thrower.getStartTime();

        double throwerShiftTime = catcherEndtime - throwerStartTime;
        if (throwerShiftTime < Util.DOUBLE_THRESHOLD) {
            throwerShiftTime = 0;

        }

        BillingUtil bu = BillingUtil.getInstance();
        double combinedCosts = thrower.getCost() + catcher.getCost();
        double newCosts = bu.getCombinedCosts_EqualSize(catcher, thrower, throwerShiftTime);
        if (newCosts > combinedCosts) {
            return null;
        }

        if (throwerStartTime - catcherEndtime > -Util.DOUBLE_THRESHOLD) {
            // concatenation
            double catcherAtuEnd = bu.getBillingEndTime(catcher);
            double throwerAtuStart = bu.getBillingStartTime(thrower);
            if (throwerStartTime - catcherAtuEnd > Util.DOUBLE_THRESHOLD
                    && throwerAtuStart - catcherEndtime > Util.DOUBLE_THRESHOLD) {
                return null;
            }
        } else {
            // serialization
            final double availableThrowerShiftTime = thrower.getRSWfix();
            if (throwerShiftTime - availableThrowerShiftTime > Util.DOUBLE_THRESHOLD) {
//				boolean satisfyNewStartTimeFix = thrower.satisfyNewStartTimeFix(catcherEndtime);
//				if(!satisfyNewStartTimeFix) {
                return null;
//				}
            }
        }

        AggregationResult result = new AggregationResult(this);
        result.setCatcher(catcher);
        result.setThrower(thrower);
        result.setThrowerShiftTime(throwerShiftTime);
        result.setNewCosts(newCosts);
        result.setOldCosts(combinedCosts);
        return result;
    }

    /**
     * schedules all tasks of thrower on catcher if possible.
     *
     * @param catcher
     * @param thrower
     * @return
     */
    @Override
    public void performCombination(AggregationResult result) {
        Lane catcher = result.getCatcher();
        Lane thrower = result.getThrower();
        double throwerShift = result.getThrowerShiftTime();
        Debug.INSTANCE.println(4, "combine tc ", thrower, " to ", catcher, " tshift= ", throwerShift);
        thrower.shift(throwerShift);
        catcher.reassignToEndFrom(thrower);

    }

}
