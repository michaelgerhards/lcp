//package algorithm.comparator.pbws.reference;
//
//import java.util.Comparator;
//
//import statics.initialization.impl.Lane;
//import algorithm.pbws.PBWSInit;
//
///**
// * Combines catchers execution time with full path execution times of o1,o2.
// * combinations with most ATU reduction. The ATU reduction is 1 or 0. Ties are
// * broken by less free Remaining time before combinations with more free
// * remaining time
// * 
// * @author Gerhards
// * 
// */
//public class LaneFullPathMostATUReductionComparator implements
//		Comparator<Lane>, ReferenceComparator<Lane, Lane> {
//
//	private final PBWSInit algorithm;
//	private Lane lane;
//
//	public LaneFullPathMostATUReductionComparator(PBWSInit algorithm) {
//		this.algorithm = algorithm;
//	}
//
//
//
//	@Override
//	public int compare(Lane o1, Lane o2) {
//		int o1AtuReduction = algorithm.getBillingUtil().getSavedAtus_NoGaps_EqualSize(lane, o1, o1.getUmodTasks());
//		int o2AtuReduction = algorithm.getBillingUtil().getSavedAtus_NoGaps_EqualSize(lane, o2, o2.getUmodTasks());
//		
//		if (o1AtuReduction > o2AtuReduction) {
//			return -1;
//		} else if (o1AtuReduction < o2AtuReduction) {
//			return 1;
//		} else {
//			algorithm.comparators.laneFullPathBestFitIntoWastedFreeTimeComparator
//					.setReference(lane);
//			return algorithm.comparators.laneFullPathBestFitIntoWastedFreeTimeComparator
//					.compare(o1, o2);
//		}
//
//	}
//
//	@Override
//	public void setReference(Lane t) {
//		this.lane = t;
//	}
//
//	@Override
//	public Lane getReference() {
//		return lane;
//	}
//
//}
