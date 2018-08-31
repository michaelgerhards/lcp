package algorithm.pbws2.strategies.aggregation.dr.fix.combinator;

import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Debug;
import statics.util.Util;

public class DR_FP_Succ_GapsAll_DifferSize_CScaleDown_FixedDL2 implements Aggregator {

    public AggregationResult tryCombineThrowerAfterCatcher_CatcherScaleDown_GapsAllowed(Lane catcher, Lane thrower,
            ScaledPseudoLane newCatcher) {
        if (thrower.getInstanceSize().isFaster(catcher.getInstanceSize())
                || thrower.getInstanceSize() == catcher.getInstanceSize()
                || newCatcher.getInstanceSize() != thrower.getInstanceSize()) {
            throw new RuntimeException();
        }

        final double catcherEndtime = newCatcher.getEndTime();
        final double throwerStartTime = thrower.getStartTime();

        double throwerShiftTime = catcherEndtime - throwerStartTime;
        if (throwerShiftTime < Util.DOUBLE_THRESHOLD) {
            throwerShiftTime = 0;
        }

        BillingUtil bu = BillingUtil.getInstance();

        double combinedCosts = thrower.getCost() + catcher.getCost();
        double newCosts = bu.getCombinedCosts_DifferentSize(newCatcher, 0, thrower, throwerShiftTime);
        if (newCosts > combinedCosts) {
            return null;
        }

        if (throwerStartTime - catcherEndtime > -Util.DOUBLE_THRESHOLD) {
            // concatenation
            double catcherAtuEnd = bu.getBillingEndTime(newCatcher);
            double throwerAtuStart = bu.getBillingStartTime(thrower);
            if (throwerStartTime - catcherAtuEnd > Util.DOUBLE_THRESHOLD
                    && throwerAtuStart - catcherEndtime > Util.DOUBLE_THRESHOLD) {
                return null;
            }
        } else {
            // serialization, gaps are not relevant
            final double availableThrowerShiftTime = newCatcher.getRSWfixOfParameter(thrower);
            if (throwerShiftTime - availableThrowerShiftTime > Util.DOUBLE_THRESHOLD) {
                return null;
            }

        }

        AggregationResult result = new AggregationResult(this);
        result.setCatcher(catcher);
        result.setThrower(thrower);
        result.setNewCatcher(newCatcher);
        result.setNewCosts(newCosts);
        result.setOldCosts(combinedCosts);
        result.setThrowerShiftTime(throwerShiftTime);
        return result;

    }

    @Override
    public void performCombination(AggregationResult result) {
        Lane catcher = result.getCatcher();
        Lane thrower = result.getThrower();
        double throwerShiftTime = result.getThrowerShiftTime();
        ScaledPseudoLane compressedCatcher = result.getNewCatcher();
        thrower.shift(throwerShiftTime);
        catcher.scale(compressedCatcher);
        catcher.reassignToEndFrom(thrower);
//		boolean throwerReleased = !algorithm.getWorkflow().existsLane(thrower);

        Debug.INSTANCE.println(2, "tryCombineThrowerAfterCatcher_CatcherScaleUpGapsAllowed: c= ", catcher, " t= ", thrower);

//		if (!throwerReleased) {
//			throw new RuntimeException("thrower not released!");
//		}
    }
}
