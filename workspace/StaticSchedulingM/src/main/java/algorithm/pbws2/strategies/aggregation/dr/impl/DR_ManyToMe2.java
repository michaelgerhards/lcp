package algorithm.pbws2.strategies.aggregation.dr.impl;

import algorithm.StaticSchedulingAlgorithm;
import java.util.List;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.misc.DR_ResultSelector;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.getters.DR_ThrowerGetter;
import statics.initialization.WorkflowInstance;
import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;
import statics.util.Debug;

public class DR_ManyToMe2 {

    private final StaticSchedulingAlgorithm algorithm;
    private final DR_Manager_EqualSize dr_Manager_EqualSize;
    private final DR_Manager_DifferSize dr_Manager_DifferSize;
    private final DR_ThrowerGetter dr_ThrowerGetter;

    public DR_ManyToMe2(StaticSchedulingAlgorithm algorithm, DR_Manager_EqualSize dr_Manager_EqualSize, DR_Manager_DifferSize dr_Manager_DifferSize, DR_ThrowerGetter dr_ThrowerGetter) {
        this.algorithm = algorithm;
        this.dr_Manager_EqualSize = dr_Manager_EqualSize;
        this.dr_Manager_DifferSize = dr_Manager_DifferSize;
        this.dr_ThrowerGetter = dr_ThrowerGetter;
    }

    /**
     * combines lanes that have more than one predecessor with no other successor.
     *
     * @param name
     * @return
     */
    public boolean combineManyToMeRelation(String name) {
        Debug.INSTANCE.println(2, "start combine " + name);
        boolean found = false;
//        Map<LaneIndex, LaneIndex> remap = new HashMap<>();
        List<LaneIndex> throwers = dr_ThrowerGetter.getThrowers();
//        Collections.sort(throwers,
//                Comparators.laneIndexLowerStartTimeComparator);
        WorkflowInstance workflow = algorithm.getWorkflow();

        for (LaneIndex throwerIndex : throwers) {
            Lane thrower = throwerIndex.getLane();
            if (thrower == null || !workflow.existsLane(thrower)) {
                continue;
            }

            // TODO optimize loop to O(1)
//            while (thrower == null) {
//                throwerIndex = remap.get(throwerIndex);
//                thrower = throwerIndex.getLane();
//            }
            AggregationResult finalResult;
            do { // TODO use caching only for valid combinations?

//                Set<Lane> predecessors = new HashSet<>(thrower.getUmodParents());
                finalResult = null;
//                for (Lane catcher : predecessors) {
                for (Lane catcher : thrower.getUmodParents()) {
                    AggregationResult result;
                    if (thrower.getInstanceSize() != catcher.getInstanceSize()) {
                        result = dr_Manager_DifferSize.checkDifferentInstanceSize(thrower, catcher);
                    } else {
                        result = dr_Manager_EqualSize.checkEqualInstanceSize(thrower, catcher);
                    }
                    finalResult = DR_ResultSelector.selectResult(finalResult, result);
                    if (finalResult != null) {
                        break;
                    }
                } // for catcher

                if (finalResult != null) {
//                    Lane deleted;
                    Lane existing;
                    finalResult.reassign();
                    Lane catcher = finalResult.getCatcher();
                    thrower = finalResult.getThrower();
                    if (algorithm.getWorkflow().existsLane(thrower)) {
                        existing = thrower;
//                        deleted = catcher;
                    } else {
                        existing = catcher;
//                        deleted = thrower;
                    }
//                    remap.put(deleted.getId(), existing.getId()); // TODO O(1)
                    found = true;
                    thrower = existing;
                }
            } while (finalResult != null);
        } // for thrower
        Debug.INSTANCE.println(2, "end combine " + name);
        return found;
    }

}
