package algorithm.comparator.pbws.reference;
//package algorithm.comparator.lanescheduling.reference;
//
//import static algorithm.AlgorithmHelper.THRESHOLD;
//import algorithm.lane.Lane;
//import algorithm.lane.LaneSchedulingInit;
//
//public class ThrowerForMeToManyComparator implements
//		ReferenceComparator<Lane, Lane> {
//
//	private final LaneSchedulingInit algorithm;
//
//	private Lane catcher;
//
//	public ThrowerForMeToManyComparator(LaneSchedulingInit algorithm) {
//		this.algorithm = algorithm;
//	}
//	
////	public ThrowerForMeToManyComparator(LaneScheduling algorithm, Lane catcher) {
////		this.algorithm = algorithm;
////		this.catcher = catcher;
////	}
//
//	@Override
//	public int compare(final Lane finalThrower, final Lane thrower) {
//		// TODO consider instance sizes
//		if (finalThrower == null) {
//			return 1;
//		}
//		if(thrower == null) {
//			return -1;
//		}
//		if(thrower == finalThrower) {
//			return 0;
//		}
//
//		double catcherShift = thrower.getStarttime()
//				- catcher.getExecutionEndTime();
//		double throwerShift = 0;
//		if (catcherShift < -THRESHOLD) {
//			throwerShift = -catcherShift;
//			catcherShift = 0;
//		}
//
//		double finalCatcherShift = finalThrower.getStarttime()
//				- catcher.getExecutionEndTime();
//		double finalThrowerShift = 0;
//		if (finalCatcherShift < -THRESHOLD) {
//			finalThrowerShift = -finalCatcherShift;
//			finalCatcherShift = 0;
//		}
//
//		int catcherATUs = algorithm.getUsedATUs(catcher.getExecutionTime());
//
//		double finalDuration = finalThrower.getExecutionEndTime()
//				+ finalThrowerShift
//				- (catcher.getStarttime() + finalCatcherShift);
//		int finalATUs = algorithm.getUsedATUs(finalDuration);
//		int finalThrowerATUs = algorithm.getUsedATUs(finalThrower
//				.getExecutionTime());
//		int finalSavedATUs = catcherATUs + finalThrowerATUs - finalATUs;
//
//		double duration = thrower.getExecutionEndTime() + throwerShift
//				- (catcher.getStarttime() + catcherShift);
//		int ATUs = algorithm.getUsedATUs(duration);
//		int throwerATUs = algorithm.getUsedATUs(thrower.getExecutionTime());
//		int savedATUs = catcherATUs + throwerATUs - ATUs;
//		
//		if (savedATUs > finalSavedATUs) {
//			return 1;
//		} else if (savedATUs == finalSavedATUs) {
//			// check wasted free time!
//			double finalWastedTime = algorithm
//					.getFreeRemainingTime(finalDuration);
//			double wastedTime = algorithm.getFreeRemainingTime(duration);
//			if (wastedTime < finalWastedTime) {
//				return 1;
//			} else if (wastedTime == finalWastedTime) {
//				// will never happen
//			}
//		}
//		return -1;
//	}
//
//	@Override
//	public void setReference(Lane t) {
//		this.catcher = t;
//	}
//
//	@Override
//	public Lane getReference() {
//		return catcher;
//	}
//
//}
