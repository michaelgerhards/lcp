package algorithm.pbws.strategies.aggregation.br.flex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import algorithm.AbstractAlgorithm;
import algorithm.misc.BR_ShiftJoinFPResultSelector;
import algorithm.misc.ResultSelector;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws.Comparators;
import algorithm.pbws.PBWSInit;
import algorithm.pbws.strategies.aggregation.br.flex.manager.BR_Manager_FP_Succ_NoGaps_DifferSize_FlexDL;
import algorithm.pbws.strategies.aggregation.br.flex.manager.BR_Manager_FP_Succ_NoGaps_EqualSize_FlexDL;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.util.Debug;
import statics.util.Util;

public class BR_ShiftJoinFullPath {

	private final PBWSInit algorithm;

	private final BR_Manager_FP_Succ_NoGaps_DifferSize_FlexDL br_Manager_FP_Succ_NoGaps_DifferSize_FlexDL;
	private final BR_Manager_FP_Succ_NoGaps_EqualSize_FlexDL br_Manager_FP_Succ_NoGaps_EqualSize_FlexDL;
	private final ResultSelector selector = new BR_ShiftJoinFPResultSelector();

	public BR_ShiftJoinFullPath(PBWSInit algorithm) {
		this.algorithm = algorithm;
		br_Manager_FP_Succ_NoGaps_DifferSize_FlexDL = new BR_Manager_FP_Succ_NoGaps_DifferSize_FlexDL(
				algorithm, selector);
		br_Manager_FP_Succ_NoGaps_EqualSize_FlexDL = new BR_Manager_FP_Succ_NoGaps_EqualSize_FlexDL(
				algorithm, selector);
	}

	public boolean aggregateNeighborsConsideringRSWflex() {
		Comparator<SchedulingTask> comp = Comparators.joinTaskComparatorLargestFirst;
		// XXX needs much time?
		List<SchedulingTask> joinTasks = algorithm.getJoinTasks();
		for (int j = 0; j < joinTasks.size(); ++j) {
			int bestJoinIndex = Util.bestOfList(joinTasks, comp, j);
			SchedulingTask join = joinTasks.get(bestJoinIndex);
			Util.swapList(joinTasks, j, bestJoinIndex);

			Set<Lane> predecessors = AbstractAlgorithm
					.getParentLanesOfTaskSetReadOnly(join);

			boolean result = tryShiftJoinUsingLists_IndependentInstanceSize(predecessors);

			if (result) {
				return true;
			}
		} // for over all joins
		Debug.INSTANCE.println(4, "nothing found equal instance size:(");
		return false;
	}

	private boolean tryShiftJoinUsingLists_IndependentInstanceSize(
			Set<Lane> predecessors) {
		List<Lane> catcherList = new ArrayList<Lane>(predecessors);
		List<Lane> throwerList = catcherList;

		for (int c = 0; c < catcherList.size(); ++c) {
			int bestCatcherIndex = Util.bestOfList(catcherList,
					Comparators.laneLaterExecutionEndTimeComparator,
					c);
			Lane catcher = catcherList.get(bestCatcherIndex);
			Util.swapList(catcherList, c, bestCatcherIndex);

			AggregationResult bestResult = null;
			for (Lane thrower : throwerList) {
				if (catcher == thrower) {
					continue;
				}

				AggregationResult result = tryCombineLanes_Successively_NoGaps(
						thrower, catcher);
				bestResult = selector.selectResult(bestResult, result);

			} // for thrower

			if (bestResult != null) {
				bestResult.reassign();
				Set<Lane> shifted = bestResult.getShifted();
				algorithm.updateConsiderJoins(shifted);
				return true;
			}
		} // for catcher
		return false;
	}

	private AggregationResult tryCombineLanes_Successively_NoGaps(Lane lane1,
			Lane lane2) {
		AggregationResult result;
		if (lane1.getInstanceSize() == lane2.getInstanceSize()) {
			result = br_Manager_FP_Succ_NoGaps_EqualSize_FlexDL
					.checkEqualInstanceSize(lane1, lane2);
		} else {
			result = br_Manager_FP_Succ_NoGaps_DifferSize_FlexDL
					.checkDifferentInstanceSize(lane1, lane2);
		}
		return result;
	}

}
