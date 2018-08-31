package algorithm.misc;

import algorithm.misc.aggregation.AggregationResult;
import statics.initialization.impl.Lane;


public class BR_ShiftJoinFPResultSelector implements ResultSelector {


	
	@Override
	public AggregationResult selectResult(AggregationResult finalResult,
			AggregationResult result) {
		if (result == null) {
			return finalResult;
		}
		if (finalResult == null) {
			return result;
		}

		// normal

		double throwerExecutionEndTime;
		double throwerStarttime;

		if (result.getNewThrower() != null) {
			ScaledPseudoLane newThrower = result.getNewThrower();
			throwerExecutionEndTime = newThrower.getEndTime();
			throwerStarttime = newThrower.getStartTime();
		} else {
			Lane thrower = result.getThrower();
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
			finalCatcherExecutionEndTime = finalCatcher.getEndTime();
			finalCatcherStarttime = finalCatcher.getStartTime();

		}

		double finalCatcherShift = 0;
		double finalThrowerShift = finalResult.getThrowerShiftTime();
		double throwerShift = result.getThrowerShiftTime();
		double catcherShift = 0;

		return compareIndependentSize(finalResult, result,
				finalCatcherStarttime, finalCatcherExecutionEndTime,
				finalCatcherShift, catcherStarttime, catcherExecutionEndTime,
				catcherShift, finalThrowerStarttime,
				finalThrowerExecutionEndTime, finalThrowerShift,
				throwerStarttime, throwerExecutionEndTime, throwerShift);

	}

	private AggregationResult compareIndependentSize(
			AggregationResult finalResult, AggregationResult result,
			double finalCatcherStart, double finalCatcherEnd,
			double finalCatcherShift, double catcherStart, double catcherEnd,
			double catcherShift, double finalThrowerStart,
			double finalThrowerEnd, double finalThrowerShift,
			double throwerStart, double throwerEnd, double throwerShift) {

		double finalNewEnd = finalThrowerEnd + finalThrowerShift;
		double newEnd = throwerEnd + throwerShift;

		if (finalNewEnd > newEnd) {
			return finalResult;
		} else {
			return result;
		}

	}
}
