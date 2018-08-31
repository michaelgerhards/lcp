package algorithm.pbws2.strategies.aggregation.br.fix.combinator;

import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Util;

public class BR_FP_Succ_NoGaps_DifferSize_CScale_FixDL2 implements Aggregator {

    public AggregationResult tryCombineThrowerAfterCatcher_CatcherScaleNoGaps(
            Lane catcher, Lane thrower, ScaledPseudoLane newCatcher) {
        if (catcher.getInstanceSize() == thrower.getInstanceSize() || newCatcher.getInstanceSize() != thrower.getInstanceSize()) {
            throw new RuntimeException();
        }

        if (catcher.getUmodChildren().contains(thrower) || catcher.getUmodParents().contains(thrower)) {
            throw new RuntimeException();
        }

        final double catcherEndtime = newCatcher.getEndTime();
        final double throwerStartTime = thrower.getStartTime();

        double throwerShiftTime = catcherEndtime - throwerStartTime;
        if (throwerShiftTime < -Util.DOUBLE_THRESHOLD) {
            return null;
        }

        double combinedCosts = thrower.getCost() + catcher.getCost();
        BillingUtil bu = BillingUtil.getInstance();
        double newCosts = bu.getCombinedCosts_DifferentSize(newCatcher, 0, thrower, throwerShiftTime);
        if (newCosts > combinedCosts) {
            return null;
        }

        final double availableThrowerShiftTime = newCatcher.getRSWfixOfParameter(thrower);

        if (Math.abs(thrower.getRSWfix() - availableThrowerShiftTime) > Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException("thrower and catcher should be independent");
        }

        if (throwerShiftTime - availableThrowerShiftTime > Util.DOUBLE_THRESHOLD) {
//            boolean satisfyNewStartTimeFix = thrower.satisfyNewStartTimeFix(catcherEndtime);
//            if (!satisfyNewStartTimeFix) {
                return null;
//            }
        }

        AggregationResult result = new AggregationResult(this);
        result.setCatcher(catcher);
        result.setThrower(thrower);
        result.setThrowerShiftTime(throwerShiftTime);

        result.setNewCatcher(newCatcher);
        result.setNewCosts(newCosts);
        result.setOldCosts(combinedCosts);
        return result;
    }

    @Override
    public void performCombination(AggregationResult result) {
        Lane catcher = result.getCatcher();
        Lane thrower = result.getThrower();
        double throwerShiftTime = result.getThrowerShiftTime();
        ScaledPseudoLane newCatcher = result.getNewCatcher();

        catcher.scale(newCatcher);
        thrower.shift(throwerShiftTime);
        catcher.reassignToEndFrom(thrower);
    }
}
