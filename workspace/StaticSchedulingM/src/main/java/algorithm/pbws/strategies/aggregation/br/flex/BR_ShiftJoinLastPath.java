package algorithm.pbws.strategies.aggregation.br.flex;

import static statics.util.Util.bestOfList;
import static statics.util.Util.swapList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import algorithm.AbstractAlgorithm;
import algorithm.misc.BR_ShiftJoinFPResultSelector;
import algorithm.misc.ResultSelector;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws.Comparators;
import algorithm.pbws.PBWSInit;
import algorithm.pbws.strategies.aggregation.br.flex.combinator.BR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL;
import algorithm.pbws.strategies.aggregation.br.flex.combinator.BR_LongestLP_Succ_NoGaps_EqualSize_FlexDL;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.util.Debug;

public class BR_ShiftJoinLastPath {

	private final PBWSInit algorithm;

	private BR_LongestLP_Succ_NoGaps_EqualSize_FlexDL bR_LongestLP_Succ_NoGaps_EqualSize_FlexDL;

	private BR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL bR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL;

	public BR_ShiftJoinLastPath(PBWSInit algorithm) {
		this.algorithm = algorithm;
		bR_LongestLP_Succ_NoGaps_EqualSize_FlexDL = new BR_LongestLP_Succ_NoGaps_EqualSize_FlexDL(
				);

		bR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL = new BR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL(
				algorithm);
	}

	public boolean distributeTasksToNeighborsConsideringRSWflex() {
		List<SchedulingTask> joinTasks = algorithm.getJoinTasks();

		boolean result = shiftJoinAnyPathUsingJoinWhatever_EqualInstanceSizes(
				joinTasks, Comparators.joinTaskComparatorLargestFirst);
		return result;
	}

	private boolean shiftJoinAnyPathUsingJoinWhatever_EqualInstanceSizes(
			List<SchedulingTask> joinTasks, Comparator<SchedulingTask> comp) {
		for (int j = 0; j < joinTasks.size(); ++j) {
			int bestJoinIndex = bestOfList(joinTasks, comp, j);
			SchedulingTask join = joinTasks.get(bestJoinIndex);
			swapList(joinTasks, j, bestJoinIndex);

			Set<Lane> predecessors = AbstractAlgorithm
					.getParentLanesOfTaskSetReadOnly(join);
			boolean result = tryShiftJoin(predecessors);
			if (result) {
				return true;
			}
		} // for over all joins
		Debug.INSTANCE.println(4, "nothing found equal instance size:(");
		return false;
	}

	private boolean tryShiftJoin(Set<Lane> predecessors) {
		List<Lane> catcherList = new ArrayList<Lane>(predecessors);
		List<Lane> throwerList = new ArrayList<Lane>(predecessors);

		// modify
		ResultSelector selector = new BR_ShiftJoinFPResultSelector(); 
																					
		for (int c = 0; c < catcherList.size(); ++c) {
			int bestCatcherIndex = bestOfList(catcherList,
					Comparators.laneLaterExecutionEndTimeComparator,
					c);
			Lane catcher = catcherList.get(bestCatcherIndex);
			swapList(catcherList, c, bestCatcherIndex);

			// algorithm.comparators.laneLastPathBestFitIntoWastedFreeTimeComparator
			// .setReference(catcher);
			// Collections
			// .sort(throwerList,
			// algorithm.comparators.laneLastPathBestFitIntoWastedFreeTimeComparator);

			Collections.sort(throwerList,
					Comparators.laneHigherExecutionTimeComparator);

			AggregationResult bestResult = null;
			boolean returnResult = false;
			for (Lane thrower : throwerList) {
				if (catcher == thrower) {
					continue;
				}
				boolean performed = false;
				// do {
				if (!algorithm.getWorkflow().existsLane(thrower)) {
					break;
				}

				// SchedulingTaskList throwerLastPath = thrower
				// .getUmodLastPath();

				AggregationResult result;
				if (thrower.getInstanceSize() == catcher.getInstanceSize()) {

					// result = br_LP_Succ_NoGaps_EqualSize_FlexDL
					// .tryMigratePath_ThrowerAfterCatcher_EqualInstanceSizes(
					// catcher, thrower, throwerLastPath);
					

					result = bR_LongestLP_Succ_NoGaps_EqualSize_FlexDL
							.tryMigratePath_ThrowerAfterCatcher_EqualInstanceSizes(
									catcher, thrower);

				} else {
					// ScaledPseudoLane newThrowerLastPath = thrower
					// .tryScalingIdlFlex(catcher.getInstanceSize(),
					// throwerLastPath);
					// if (newThrowerLastPath != null) {
					// newThrowerLastPath.catchUpForNewSize();
					// result = br_LP_Succ_NoGaps_DifferentSize_TScale_FlexDL
					// .tryMigrateLastPath_ThrowerAfterCatcher_ThrowerScale_DifferentInstanceSize(
					// catcher, thrower,
					// newThrowerLastPath, throwerLastPath);

					result = bR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL
							.tryMigrateLastPath_ThrowerAfterCatcher_ThrowerScale_DifferentInstanceSize(
									catcher, thrower);

					// } else {
					// result = null;
					// }
				}

				// bestResult = selector.selectResult(bestResult, result);

				// if (result != null && performed) {
				// // TODO remove
				// System.out.println();
				// System.out.println(thrower);
				// System.out.println(catcher);
				// System.out.println("Done");
				// System.exit(0);
				//
				// }

				if (result != null) {
					applyResult(result);
					// return true;
					returnResult = true;
					performed = true;
				} else {
					performed = false;
				}

				// } while (performed);

				if (returnResult) {
					return true;
				}
			} // for thrower
				// if (bestResult != null) {
				// applyResult(join, bestResult);
				// return true;
				// }

		} // for catcher
		return false;
	}

	private void applyResult(AggregationResult bestResult) {
		bestResult.reassign();
		Set<Lane> shifted = bestResult.getShifted();
		algorithm.updateConsiderJoins(shifted);
	}

}
