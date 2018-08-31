package algorithm.pbws2.strategies.aggregation.dr.impl;

import java.util.List;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.misc.DR_ResultSelector;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.getters.DR_CatcherGetter;
import algorithm.pbws2.PBWSInit2;
import statics.initialization.WorkflowInstance;
import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;
import statics.util.Debug;

public class DR_MeToMany2 {

    private final PBWSInit2 algorithm;
    private final DR_Manager_EqualSize dr_Manager_EqualSize;
    private final DR_Manager_DifferSize dr_Manager_DifferSize;
    private final DR_CatcherGetter dr_CatcherGetter;

    public DR_MeToMany2(PBWSInit2 algorithm,
            DR_Manager_EqualSize dr_Manager_EqualSize,
            DR_Manager_DifferSize dr_Manager_DifferSize, DR_CatcherGetter dr_CatcherGetter) {
        this.algorithm = algorithm;
        this.dr_Manager_EqualSize = dr_Manager_EqualSize;
        this.dr_Manager_DifferSize = dr_Manager_DifferSize;
        this.dr_CatcherGetter = dr_CatcherGetter;
    }

    /**
     * combines lanes that have more than one successor with no other predecessors.
     */
    public boolean combineMeToManyRelation(String relation) {
        boolean found = false;
        Debug.INSTANCE.println(2, "start combine " + relation);
//		Map<LaneIndex, LaneIndex> remap = new HashMap<>();
        List<LaneIndex> catchers = dr_CatcherGetter.getCatchers();
        WorkflowInstance workflow = algorithm.getWorkflow();
//		Collections.sort(catchers,
//				Comparators.laneIndexLowerStartTimeComparator);

        for (LaneIndex catcherIndex : catchers) {
            Lane catcher = catcherIndex.getLane();
//			while (catcher == null) {
//				catcherIndex = remap.get(catcherIndex);
//				catcher = catcherIndex.getLane();
//			}
            if (catcher == null || !workflow.existsLane(catcher)) {
                continue;
            }

            AggregationResult finalResult;
            do {
//				Set<Lane> successors = new HashSet<Lane>(
//						catcher.getUmodChildren());
                finalResult = null;
//				for (Lane thrower : successors) {
                for (Lane thrower : catcher.getUmodChildren()) {
                    AggregationResult result;
                    if (thrower.getInstanceSize() != catcher.getInstanceSize()) {
                        result = dr_Manager_DifferSize
                                .checkDifferentInstanceSize(catcher, thrower);
                    } else {
                        result = dr_Manager_EqualSize.checkEqualInstanceSize(
                                catcher, thrower); // TODO
                        // activate!!!
                    }
                    finalResult = DR_ResultSelector.selectResult(
                            finalResult, result);
                    if (finalResult != null) {
                        break;
                    }

                } // for throwers

                if (finalResult != null) {
                    finalResult.reassign();

                    // CheckResourceAllocationPlan.checkPlan(algorithm.getWorkflow());
//                    Lane deleted = null;
                    Lane existing = null;
                    Lane thrower = finalResult.getThrower();
                    catcher = finalResult.getCatcher();
                    if (algorithm.getWorkflow().existsLane(thrower)) {
                        existing = thrower;
//                        deleted = catcher;
                    } else {
                        existing = catcher;
//                        deleted = thrower;
                    }
//                    remap.put(deleted.getId(), existing.getId());
                    catcher = existing;
                    found = true;
                }
            } while (finalResult != null);
        } // for catchers

        Debug.INSTANCE.println(2, "end combine " + relation);
        return found;
    }

}
