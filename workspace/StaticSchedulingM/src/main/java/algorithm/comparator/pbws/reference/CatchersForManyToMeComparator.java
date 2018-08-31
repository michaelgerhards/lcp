package algorithm.comparator.pbws.reference;
//package algorithm.comparator.lanescheduling.reference;
//
//import static algorithm.AlgorithmHelper.THRESHOLD;
//import algorithm.lane.Lane;
//import algorithm.lane.LaneSchedulingInit;
//
//public class CatchersForManyToMeComparator implements
//		ReferenceComparator<Lane, Lane> {
//
//	private final LaneSchedulingInit algorithm;
//
//	private Lane thrower;
//
//	public CatchersForManyToMeComparator(LaneSchedulingInit algorithm) {
//		this.algorithm = algorithm;
//	}
//
////	public CatchersForManyToMeComparator(LaneScheduling algorithm, Lane catcher) {
////		this.algorithm = algorithm;
////		this.thrower = catcher;
////	}
//
//	@Override
//	public int compare(final Lane finalCatcher, final Lane catcher) {
//		if (finalCatcher == null) {
//			return 1;
//		}
//		int throwerATUs = algorithm.getUsedATUs(thrower.getExecutionTime());
//		double catcherShift = thrower.getStarttime()
//				- catcher.getExecutionEndTime();
//		double throwerShift = 0;
//		if (catcherShift < -THRESHOLD) {
//			throwerShift = -catcherShift;
//			catcherShift = 0;
//		}
//
//		double finalCatcherShift = thrower.getStarttime()
//				- finalCatcher.getExecutionEndTime();
//		double finalThrowerShift = 0;
//		if (finalCatcherShift < -THRESHOLD) {
//			finalThrowerShift = -finalCatcherShift;
//			finalCatcherShift = 0;
//		}
//
//		// compare new thrower with finalThrower
//		// check ATU reductions
//
//		double finalDuration = thrower.getExecutionEndTime()
//				+ finalThrowerShift
//				- (finalCatcher.getStarttime() + finalCatcherShift);
//		int finalATUs = algorithm.getUsedATUs(finalDuration);
//		int finalCatcherATUs = algorithm.getUsedATUs(finalCatcher
//				.getExecutionTime());
//		int finalSavedATUs = throwerATUs + finalCatcherATUs - finalATUs;
//
//		double duration = thrower.getExecutionEndTime() + throwerShift
//				- (catcher.getStarttime() + catcherShift);
//		int ATUs = algorithm.getUsedATUs(duration);
//		int catcherATUs = algorithm.getUsedATUs(catcher.getExecutionTime());
//		int savedATUs = throwerATUs + catcherATUs - ATUs;
//
//		if (savedATUs > finalSavedATUs) {
//			return 1;
//		} else if (savedATUs == finalSavedATUs) {
//			if (catcher.getExecutionEndTime() > finalCatcher
//					.getExecutionEndTime()) {
//				return 1;
//			} else if (catcher.getExecutionEndTime() == finalCatcher
//					.getExecutionEndTime()) {
//				// check wasted free time!
//
//				double finalWastedTime = algorithm
//						.getFreeRemainingTime(finalDuration);
//
//				double wastedTime = algorithm.getFreeRemainingTime(duration);
//
//				if (wastedTime < finalWastedTime) {
//					return 1;
//				} else if (wastedTime == finalWastedTime) {
//					// will never happen
//				}
//			}
//		}
//		return -1;
//	}
//
//	@Override
//	public void setReference(Lane t) {
//		this.thrower = t;
//	}
//
//	@Override
//	public Lane getReference() {
//		return thrower;
//	}
//
//}
