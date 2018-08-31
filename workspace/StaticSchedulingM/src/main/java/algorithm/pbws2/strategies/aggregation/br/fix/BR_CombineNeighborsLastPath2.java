package algorithm.pbws2.strategies.aggregation.br.fix;

import algorithm.AbstractAlgorithm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws.Comparators;
import algorithm.pbws2.PBWSInit2;
import algorithm.pbws2.strategies.aggregation.br.fix.combinator.BR_LP_Succ_NoGaps_DifferSize_TScale_FixDL2;
import algorithm.pbws2.strategies.aggregation.br.fix.combinator.BR_LP_Succ_NoGaps_EqualSize_FixDL2;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.initialization.impl.SchedulingTaskList;
import statics.util.Debug;

public class BR_CombineNeighborsLastPath2 {

    protected final PBWSInit2 algorithm;

    public final BR_LP_Succ_NoGaps_DifferSize_TScale_FixDL2 lP_Succ_NoGaps_DifferentSize_FixedDL;
    public final BR_LP_Succ_NoGaps_EqualSize_FixDL2 lP_Succ_NoGaps_EqualSize_FixedDL;

    public BR_CombineNeighborsLastPath2(PBWSInit2 algorithm) {
        this.algorithm = algorithm;
        lP_Succ_NoGaps_DifferentSize_FixedDL = new BR_LP_Succ_NoGaps_DifferSize_TScale_FixDL2();
        lP_Succ_NoGaps_EqualSize_FixedDL = new BR_LP_Succ_NoGaps_EqualSize_FixDL2();
    }

    public void distributeTasksToNeighborsConsideringRSWfix() {
        List<SchedulingTask> joinTasks = algorithm.getJoinTasks();
        Collections.sort(joinTasks, Comparators.taskHigherStartTimeComparator);
        for (SchedulingTask join : joinTasks) {
            Lane currentJoinLane = join.getLane();
            if (!algorithm.getWorkflow().existsLane(currentJoinLane)) {
                continue;
            }
            if (!algorithm.containsConsiderJoins(currentJoinLane)) {
                Debug.INSTANCE.println(4, "lane ignored for LP: ", currentJoinLane);
                continue;
            }

            combineParallelLanesAnyPathesUsingJoinWhatever(join, Comparators.lanePathCountComparator);
        }
    }

    private void combineParallelLanesAnyPathesUsingJoinWhatever(SchedulingTask join, Comparator<Lane> throwerComparator) {

        Set<Lane> predecessors = AbstractAlgorithm.getParentLanesOfTaskSetReadOnly(join);

        List<Lane> throwerList = new ArrayList<>(predecessors);
        List<Lane> catcherList = throwerList;
//        Collections.sort(throwerList, throwerComparator);

        // shortest thrower first
        for (Lane thrower : throwerList) {
            if (!algorithm.getWorkflow().existsLane(thrower)) {
                continue;
            }

            SchedulingTaskList throwerLastPath = thrower.getUmodLastPath();
            if (throwerLastPath.size() == thrower.getTasksCount()) {
                continue;
            }

            // for (Lane catcher : catcherList) {
            for (int i = catcherList.size() - 1; i >= 0; --i) {
                Lane catcher = catcherList.get(i); // longest catcher first
                if (!algorithm.getWorkflow().existsLane(catcher) || catcher == thrower) {
                    continue;
                }

                // thrower catcher combination
                tryMigrateWhatever(thrower, catcher);
                boolean throwerReleased = !algorithm.getWorkflow().existsLane(thrower);
                if (throwerReleased) {
                    // next thrower
                    break;
                }
            } // catcher for
        } // thrower for
    }

    /**
     * tries to migrate the last path of thrower after catcher
     *
     * @param thrower
     * @param catcher
     * @return true if thrower is released
     */
    private void tryMigrateWhatever(Lane thrower, Lane catcher) {
//        boolean continueOnSameLane;
//        do { // continue on same lane
        SchedulingTaskList throwerLastPath = thrower.getUmodLastPath();
        AggregationResult result;
        if (thrower.getInstanceSize() == catcher.getInstanceSize()) {
            result = lP_Succ_NoGaps_EqualSize_FixedDL.tryMigrateThrowerPathAfterCatcher_EqualInstanceSize_NoGaps(thrower, catcher, throwerLastPath);
        } else {
            result = lP_Succ_NoGaps_DifferentSize_FixedDL.tryMigrateThrowerPathAfterCatcher_DifferentInstanceSize_ThrowerScale_NoGaps(thrower, catcher, throwerLastPath);
        }
        if (result == null) {
//                continueOnSameLane = false;
        } else {
            result.reassign();
//                continueOnSameLane = algorithm.getWorkflow().existsLane(result.getThrower());
        }
//        } while (continueOnSameLane);
    }

}
