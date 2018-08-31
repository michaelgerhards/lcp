package algorithm.pbws.strategies.aggregation.dr.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws.Comparators;
import algorithm.pbws.PBWSInit;
import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;
import statics.util.Debug;

public class DR_OneToOneRelation {

    private final PBWSInit algorithm;
    private final DR_Manager_EqualSize dr_Manager_EqualSize;
    private final DR_Manager_DifferSize dr_Manager_DifferSize;

    public DR_OneToOneRelation(PBWSInit algorithm,
            DR_Manager_EqualSize dr_Manager_EqualSize,
            DR_Manager_DifferSize dr_Manager_DifferSize) {
        this.algorithm = algorithm;
        this.dr_Manager_EqualSize = dr_Manager_EqualSize;
        this.dr_Manager_DifferSize = dr_Manager_DifferSize;
    }

    /**
     * combines lanes that have exactly one successor with no other predecessors.
     */
    public boolean combineOneToOneRelation(String name) {
        Debug.INSTANCE.println(2, "start combine " + name);
        boolean found = false;
        Map<LaneIndex, LaneIndex> remap = new HashMap<LaneIndex, LaneIndex>();
        List<LaneIndex> catchers = getOneToOneRelation();
        Collections.sort(catchers,
                Comparators.laneIndexLowerStartTimeComparator);
        for (LaneIndex catcherIndex : catchers) {
            Lane catcher = catcherIndex.getLane();
            while (catcher == null) {
                catcherIndex = remap.get(catcherIndex);
                catcher = catcherIndex.getLane();
            }

            Lane thrower = catcher.getUmodChildren().iterator().next();

            AggregationResult result;
            if (thrower.getInstanceSize() != catcher.getInstanceSize()) {
                result = dr_Manager_DifferSize.checkDifferentInstanceSize(
                        catcher, thrower);
            } else {
                result = dr_Manager_EqualSize.checkEqualInstanceSize(catcher,
                        thrower);
            } // if else

            if (result != null) {
                Lane deleted = null;
                Lane existing = null;
                result.reassign();
                if (algorithm.getWorkflow().existsLane(thrower)) {
                    existing = thrower;
                    deleted = catcher;
                } else {
                    existing = catcher;
                    deleted = thrower;
                }
                remap.put(deleted.getId(), existing.getId());
                catcher = existing;
                found = true;
            }
        } // for
        Debug.INSTANCE.println(2, "end combine " + name);
        return found;
    }

    /**
     *
     * @return a list of lane indexes for lanes that have exactly one successor with no other predecessors.
     */
    private List<LaneIndex> getOneToOneRelation() {
        List<LaneIndex> oneToOneLanes = new ArrayList<LaneIndex>();
        for (Lane lane : algorithm.getWorkflow().getLanes()) {

            if (lane.getUmodChildren().size() == 1) {
                Lane succ = lane.getUmodChildren().iterator().next();
                if (succ.getUmodParents().size() == 1) {
                    oneToOneLanes.add(lane.getId());
                }
            }
        }
        return oneToOneLanes;
    }

}
