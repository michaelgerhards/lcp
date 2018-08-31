package algorithm.pbws.strategies.aggregation.dr.fix.combinator;

import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Debug;
import statics.util.Util;

public class DR_FP_Succ_GapsAll_DifferSize_TScale_FixedDL implements Aggregator {

    public AggregationResult tryCombineThrowerAfterCatcher_ThrowerScaleUpGapsAllowed(
            Lane catcher, Lane thrower, ScaledPseudoLane newThrower) {
        if (catcher.getInstanceSize() == thrower.getInstanceSize()
                || newThrower.getInstanceSize() != catcher.getInstanceSize()) {
            throw new RuntimeException();
        }

        final double catcherEndtime = catcher.getEndTime();
        final double throwerStartTime = newThrower.getStartTime();

        double throwerShiftTime = catcherEndtime - throwerStartTime;
        if (throwerShiftTime < Util.DOUBLE_THRESHOLD) {
            throwerShiftTime = 0;
        }

        double combinedCosts = thrower.getCost() + catcher.getCost();
        BillingUtil bu = BillingUtil.getInstance();
        double newCosts = bu
                .getCombinedCosts_DifferentSize(catcher, newThrower, throwerShiftTime);
        if (newCosts > combinedCosts) {
            return null;
        }

        if (throwerStartTime - catcherEndtime > -Util.DOUBLE_THRESHOLD) {
            // concatenation
            double catcherAtuEnd = bu
                    .getBillingEndTime(catcher);
            double throwerAtuStart = bu
                    .getBillingStartTime(newThrower);
            if (throwerStartTime - catcherAtuEnd > Util.DOUBLE_THRESHOLD
                    && throwerAtuStart - catcherEndtime > Util.DOUBLE_THRESHOLD) {
                return null;
            }
        } else // serialization
        {
            if (!newThrower.hasGap()) {
                final double availableThrowerShiftTime = newThrower.getRSWfix();
                if (throwerShiftTime > availableThrowerShiftTime) {
                    return null;
                }
            } else {
                double time = catcher.getEndTime();

                boolean satisfyNewStartTime = newThrower.satisfyNewStartTimeFix(time);
                if (!satisfyNewStartTime) {
                    return null;
                }

            }
        }

        AggregationResult result = new AggregationResult(this);
        result.setCatcher(catcher);
        result.setThrowerShiftTime(throwerShiftTime);
        result.setThrower(thrower);
        result.setNewThrower(newThrower);
        result.setNewCosts(newCosts);
        result.setOldCosts(combinedCosts);
        return result;
    }

    @Override
    public void performCombination(AggregationResult result) {
        Lane catcher = result.getCatcher();
        Lane thrower = result.getThrower();
        ScaledPseudoLane newThrower = result.getNewThrower();

        double throwerShiftTime = result.getThrowerShiftTime();

        thrower.scale(newThrower);

        Debug.INSTANCE.println(2, "tryCombineThrowerAfterCatcher_ThrowerScaleUpGapsAllowed: c= ", catcher, " t= ", thrower);

        thrower.shift(throwerShiftTime);
        catcher.reassignToEndFrom(thrower);

        boolean throwerReleased = !catcher.getWorkflow().existsLane(thrower);

        if (!throwerReleased) {
            throw new RuntimeException("thrower not released! t=" + thrower + " c=" + catcher);
        }
        Lane.inverseShiftRecursiveForSuccessorsOf(catcher);
    }

}
