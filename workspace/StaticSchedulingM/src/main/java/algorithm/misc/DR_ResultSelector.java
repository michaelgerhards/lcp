package algorithm.misc;

import algorithm.misc.aggregation.AggregationResult;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;

public class DR_ResultSelector {

    private DR_ResultSelector() {
        // static class
    }

    public static AggregationResult selectResult(AggregationResult finalResult,
            AggregationResult result) {
        if (result == null) {
            return finalResult;
        }
        if (finalResult == null) {
            return result;
        }

        double finalSavedCosts = finalResult.getSavedCosts();
        double savedCosts = result.getSavedCosts();
        if (finalSavedCosts > savedCosts) {
            return finalResult;
        } else if (finalSavedCosts < savedCosts) {
            return result;
        }

		// normal
        double throwerExecutionEndTime;
        double throwerStarttime;

        BillingUtil bu = null;

        if (result.getNewThrower() != null) {
            ScaledPseudoLane newThrower = result.getNewThrower();
            throwerExecutionEndTime = newThrower.getEndTime();
            throwerStarttime = newThrower.getStartTime();
        } else {
            Lane thrower = result.getThrower();
            bu = BillingUtil.getInstance();
            throwerExecutionEndTime = thrower.getEndTime();
            throwerStarttime = thrower.getStartTime();
        }

        double catcherStarttime;
        double catcherExecutionEndTime;

        if (result.getNewCatcher() != null) {
            ScaledPseudoLane newCatcher = result.getNewCatcher();
            catcherExecutionEndTime = newCatcher.getEndTime();
            catcherStarttime = newCatcher.getStartTime();
        } else {
            Lane catcher = result.getCatcher();
            bu = BillingUtil.getInstance();
            catcherStarttime = catcher.getStartTime();
            catcherExecutionEndTime = catcher.getEndTime();
        }

		// final
        double finalThrowerExecutionEndTime;
        double finalThrowerStarttime;

        if (finalResult.getNewThrower() != null) {
            ScaledPseudoLane newThrower = finalResult.getNewThrower();
            finalThrowerExecutionEndTime = newThrower.getEndTime();
            finalThrowerStarttime = newThrower.getStartTime();
        } else {
            Lane finalThrower = finalResult.getThrower();
            bu = BillingUtil.getInstance();
            finalThrowerExecutionEndTime = finalThrower.getEndTime();
            finalThrowerStarttime = finalThrower.getStartTime();
        }

        double finalCatcherExecutionEndTime;
        double finalCatcherStarttime;
        if (finalResult.getNewCatcher() != null) {
            ScaledPseudoLane newCatcher = finalResult.getNewCatcher();
            finalCatcherExecutionEndTime = newCatcher.getEndTime();
            finalCatcherStarttime = newCatcher.getStartTime();
        } else {
            Lane finalCatcher = finalResult.getCatcher();
            bu = BillingUtil.getInstance();
            finalCatcherExecutionEndTime = finalCatcher.getEndTime();
            finalCatcherStarttime = finalCatcher.getStartTime();

        }

        double finalThrowerShift = finalResult.getThrowerShiftTime();
        double throwerShift = result.getThrowerShiftTime();

        return compareIndependentSize(bu, finalResult, result,
                finalCatcherStarttime, finalCatcherExecutionEndTime,
                catcherStarttime, catcherExecutionEndTime,
                finalThrowerStarttime, finalThrowerExecutionEndTime,
                finalThrowerShift, throwerStarttime, throwerExecutionEndTime,
                throwerShift);
    }

    private static AggregationResult compareIndependentSize(BillingUtil bu,
            AggregationResult finalResult, AggregationResult result,
            double finalCatcherStart, double finalCatcherEnd,
            double catcherStart, double catcherEnd, double finalThrowerStart,
            double finalThrowerEnd, double finalThrowerShift,
            double throwerStart, double throwerEnd, double throwerShift) {

        double finalWastedTime = bu.calcWastedTime(
                finalCatcherStart, catcherEnd, finalThrowerShift,
                finalThrowerStart, finalThrowerEnd);

        double wastedTime = bu.calcWastedTime(
                catcherStart, catcherEnd, throwerShift, throwerStart,
                throwerEnd);

        if (wastedTime < finalWastedTime) {
            return result;
        } else if (wastedTime > finalWastedTime) {
            return finalResult;
        }

        if (catcherEnd > finalCatcherEnd) {
            return result;
        } else {
            return finalResult;
        }
    }
}
