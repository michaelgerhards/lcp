package algorithm.misc.getters;

import java.util.ArrayList;
import java.util.List;

import algorithm.StaticSchedulingAlgorithm;
import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;

public class DR_TG_ManyToManyIncoming implements DR_ThrowerGetter {

    private final StaticSchedulingAlgorithm algorithm;

    public DR_TG_ManyToManyIncoming(StaticSchedulingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     *
     * @return a list of lane indexes for lanes that have more than one predecessor each with more than one successor
     */
    @Override
    public List<LaneIndex> getThrowers() {
        List<LaneIndex> manyToManyLanes = new ArrayList<LaneIndex>();
        for (Lane lane : algorithm.getWorkflow().getLanes()) {
            if (lane == null) {
                continue;
            }
            if (lane.getUmodParents().size() > 1) {
                manyToManyLanes.add(lane.getId());
            }
        }
        return manyToManyLanes;
    }

}
