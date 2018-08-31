package algorithm.pbws2.strategies.aggregation.br.fix.combinator;

import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Util;

public class BR_FP_Succ_NoGaps_DifferSize_TScale_FixDL2 implements Aggregator {

    public AggregationResult tryCombineThrowerAfterCatcher_ThrowerScaleNoGaps(
            Lane catcher, Lane thrower, ScaledPseudoLane newThrower) {
        if (catcher.getInstanceSize() == thrower.getInstanceSize() || catcher.getInstanceSize() != newThrower.getInstanceSize()) {
            throw new RuntimeException();
        }
        if (catcher.getUmodChildren().contains(thrower) || catcher.getUmodParents().contains(thrower)) {
            throw new RuntimeException();
        }

        final double catcherEndtime = catcher.getEndTime();
        final double throwerStartTime = newThrower.getStartTime();

        double throwerShiftTime = catcherEndtime - throwerStartTime;
        if (throwerShiftTime < -Util.DOUBLE_THRESHOLD) {
            return null;
        }

        double combinedCosts = thrower.getCost() + catcher.getCost();
        BillingUtil bu = BillingUtil.getInstance();
        double newCosts = bu.getCombinedCosts_DifferentSize(catcher, newThrower, throwerShiftTime);
        if (newCosts > combinedCosts) {
            return null;
        }

//        if (!newThrower.hasGap()) {
        final double availableThrowerShiftTime = newThrower.getRSWfix();
        if (throwerShiftTime - availableThrowerShiftTime > Util.DOUBLE_THRESHOLD) {
            return null;
        }
//        } else {
//            boolean satisfyNewStartTimeFix = newThrower.satisfyNewStartTimeFix(catcherEndtime);
//            if (!satisfyNewStartTimeFix) {
//                return null;
//            }
//        }

        AggregationResult result = new AggregationResult(this);
        result.setCatcher(catcher);
        result.setThrower(thrower);
        result.setThrowerShiftTime(throwerShiftTime);
        result.setNewThrower(newThrower);
        result.setNewCosts(newCosts);
        result.setOldCosts(combinedCosts);
        return result;
    }

    @Override
    public void performCombination(AggregationResult result) {
        Lane catcher = result.getCatcher();
        Lane thrower = result.getThrower();
        double throwerShiftTime = result.getThrowerShiftTime();
        ScaledPseudoLane newThrower = result.getNewThrower();

        thrower.scale(newThrower);
        thrower.shift(throwerShiftTime);
        catcher.reassignToEndFrom(thrower);
    }

}
