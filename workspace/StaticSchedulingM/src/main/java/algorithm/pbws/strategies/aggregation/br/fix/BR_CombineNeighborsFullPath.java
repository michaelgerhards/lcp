package algorithm.pbws.strategies.aggregation.br.fix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import algorithm.comparator.pbws.lane.LaneExpensiveFirstComparator;
import algorithm.misc.DR_ResultSelector;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws.Comparators;
import algorithm.pbws.PBWSInit;
import algorithm.pbws.strategies.aggregation.br.fix.manager.BR_Manager_FP_Succ_NoGaps_DifferSize_FixDL;
import algorithm.pbws.strategies.aggregation.br.fix.manager.BR_Manager_FP_Succ_NoGaps_EqualSize_FixDL;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.util.CacheTwoToOne;
import statics.util.Debug;

public class BR_CombineNeighborsFullPath {

    private final PBWSInit algorithm;

    private CacheTwoToOne<Lane, AggregationResult> resultCache = new CacheTwoToOne<Lane, AggregationResult>();
    public final BR_Manager_FP_Succ_NoGaps_EqualSize_FixDL manager_FP_Succ_NoGaps_EqualSize_FixedDL;
    public final BR_Manager_FP_Succ_NoGaps_DifferSize_FixDL manager_FP_Succ_NoGaps_DifferentSize_Scale_FixedDL;
    private final Comparator<Lane> candidateComparator;

    public BR_CombineNeighborsFullPath(PBWSInit algorithm) {
        this.algorithm = algorithm;
        candidateComparator = new LaneExpensiveFirstComparator();
        manager_FP_Succ_NoGaps_DifferentSize_Scale_FixedDL = new BR_Manager_FP_Succ_NoGaps_DifferSize_FixDL(algorithm);
        manager_FP_Succ_NoGaps_EqualSize_FixedDL = new BR_Manager_FP_Succ_NoGaps_EqualSize_FixDL(algorithm);
    }

    /**
     * combines lanes that are parallel and overlap in computational time
     */
    public void aggregateNeighborsConsideringRSWfix() {
        List<SchedulingTask> joinTasks = algorithm.getJoinTasks();
        Collections.sort(joinTasks, Comparators.taskHigherStartTimeComparator);

        for (SchedulingTask join : joinTasks) {
            Lane currentJoinLane = join.getLane();
            if (!algorithm.getWorkflow().existsLane(currentJoinLane)) {
                continue;
            }

            if (!algorithm.containsConsiderJoins(currentJoinLane)) {
                Debug.INSTANCE.println(4, "lane ignored: ", currentJoinLane);
                continue;
            }

            Set<Lane> candidates = PBWSInit.getParentLanesOfTaskSetReadOnly(join);
            combineParallelLanesAnyPathesUsingJoinWhatever(candidates);

        } // join for
        resultCache = new CacheTwoToOne<>();
    }

    private void combineParallelLanesAnyPathesUsingJoinWhatever(Set<Lane> candidates) {

        // TODO consider multiple joins having same parent lanes
        // idea: two different lists: each of l1 tested with each of l2
        // if a1 is subset of a2 but a1 tested: l1 = a2, l2 = a2-a1
        List<Lane> candidateList = new ArrayList<Lane>(candidates);
        Collections.sort(candidateList, candidateComparator);

        int i = 0;
        for (Lane outer : candidateList) {
            if (!algorithm.getWorkflow().existsLane(outer)) {
                continue;
            }

            AggregationResult bestResult;
            List<Lane> partners = candidateList.subList(i + 1, candidateList.size());

            do {
                bestResult = null;
                List<Lane> nextRoundPartners = new ArrayList<Lane>(partners.size());

                // XXX problem by starting with index i+1. Aggregations are
                // only performed by
                // shift A behind B or shift B behind A without a gap between
                // both
                // but the shift must be "behind". A depth reduction shift like
                // shift A before B or shift B before A is prohibited since it
                // destroys flexibility!
                for (Lane inner : partners) {
                    if (inner == outer || !algorithm.getWorkflow().existsLane(inner)) {
                        continue;
                    }

                    AggregationResult result;
                    if (resultCache.containsKey(outer, inner)) {
                        result = resultCache.get(outer, inner);
                    } else {
                        result = tryCombineLanes_Successively_NoGaps(outer, inner);
                        resultCache.put(outer, inner, result);
                    }

                    if (result != null) {
                        nextRoundPartners.add(inner);
                    }

                    // XXX check comparator
                    bestResult = DR_ResultSelector.selectResult(bestResult, result);

                    if (bestResult != null) {
                        // TODO if best result = optimal result -> break
                    }
                } // inner for

                if (bestResult != null) {
                    bestResult.reassign();
                    if (algorithm.getWorkflow().existsLane(bestResult.getCatcher())) {
                        outer = bestResult.getCatcher();
                    } else {
                        outer = bestResult.getThrower();
                    }
                    // catcher does not exists anymore -> no erase ->
                    // performance
                    resultCache.delete(outer);
                    partners = nextRoundPartners;
                }
            } while (bestResult != null);
            ++i;
        } // outer for
    }

    private AggregationResult tryCombineLanes_Successively_NoGaps(Lane lane1, Lane lane2) {
        AggregationResult result;
        if (lane1.getInstanceSize() == lane2.getInstanceSize()) {
            result = manager_FP_Succ_NoGaps_EqualSize_FixedDL.checkEqualInstanceSize(lane1, lane2);
        } else {
            result = manager_FP_Succ_NoGaps_DifferentSize_Scale_FixedDL.checkDifferentInstanceSize(lane1, lane2);
        }
        return result;
    }

}
