package algorithm.pbws2.strategies.aggregation.br.flex;

import algorithm.AbstractAlgorithm;
import static statics.util.Util.bestOfList;
import static statics.util.Util.swapList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws.Comparators;

import algorithm.pbws2.PBWSInit2;
import algorithm.pbws2.strategies.aggregation.br.flex.combinator.BR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL2;
import algorithm.pbws2.strategies.aggregation.br.flex.combinator.BR_LongestLP_Succ_NoGaps_EqualSize_FlexDL2;
import java.util.Collections;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.util.Debug;

public class BR_ShiftJoinLastPath2 {

    private final PBWSInit2 algorithm;

    private final BR_LongestLP_Succ_NoGaps_EqualSize_FlexDL2 bR_LongestLP_Succ_NoGaps_EqualSize_FlexDL;

    private final BR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL2 bR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL;

    public BR_ShiftJoinLastPath2(PBWSInit2 algorithm) {
        this.algorithm = algorithm;
        bR_LongestLP_Succ_NoGaps_EqualSize_FlexDL = new BR_LongestLP_Succ_NoGaps_EqualSize_FlexDL2();
        bR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL = new BR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL2();
    }

    public boolean distributeTasksToNeighborsConsideringRSWflex() {
        List<SchedulingTask> joinTasks = algorithm.getJoinTasks();
        boolean result = shiftJoinAnyPathUsingJoinWhatever_EqualInstanceSizes(joinTasks, Comparators.joinTaskComparatorLargestFirst);
        return result;
    }

    private boolean shiftJoinAnyPathUsingJoinWhatever_EqualInstanceSizes(List<SchedulingTask> joinTasks, Comparator<SchedulingTask> comp) {
        Collections.sort(joinTasks, comp);
        for (int j = 0; j < joinTasks.size(); ++j) {
//            int bestJoinIndex = bestOfList(joinTasks, comp, j);
//            SchedulingTask join = joinTasks.get(bestJoinIndex);
//            swapList(joinTasks, j, bestJoinIndex);
            SchedulingTask join = joinTasks.get(j);

            Set<Lane> predecessors = AbstractAlgorithm.getParentLanesOfTaskSetReadOnly(join);
            boolean result = tryShiftJoin(predecessors);
            if (result) {
                return true;
            }
        } // for over all joins
        Debug.INSTANCE.println(4, "nothing found equal instance size:(");
        return false;
    }

    private boolean tryShiftJoin(Set<Lane> predecessors) {
        List<Lane> catcherList = new ArrayList<>(predecessors);
//        List<Lane> throwerList = new ArrayList<>(predecessors);

        // modify
//		ResultSelector selector = new BR_ShiftJoinFPResultSelector();
        for (int c = 0; c < catcherList.size(); ++c) {
//			int bestCatcherIndex = bestOfList(catcherList, Comparators.laneLaterExecutionEndTimeComparator,
//					c);
//			Lane catcher = catcherList.get(bestCatcherIndex);
//			swapList(catcherList, c, bestCatcherIndex);
            Lane catcher = catcherList.get(c);

            // algorithm.comparators.laneLastPathBestFitIntoWastedFreeTimeComparator
            // .setReference(catcher);
            // Collections
            // .sort(throwerList,
            // algorithm.comparators.laneLastPathBestFitIntoWastedFreeTimeComparator);
//            Collections.sort(throwerList, Comparators.laneHigherExecutionTimeComparator);
//            AggregationResult bestResult = null;
            boolean returnResult = false;
//            for (Lane thrower : throwerList) {
            for (int j = c + 1; j < catcherList.size(); ++j) {
                Lane thrower = catcherList.get(j);
                if (catcher == thrower) {
                    continue;
                }
//                boolean performed = false;
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
                    result = bR_LongestLP_Succ_NoGaps_EqualSize_FlexDL.tryMigratePath_ThrowerAfterCatcher_EqualInstanceSizes(catcher, thrower);

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

                    result = bR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL.tryMigrateLastPath_ThrowerAfterCatcher_ThrowerScale_DifferentInstanceSize(catcher, thrower);

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
//                    performed = true;
                } else {
//                    performed = false;
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
