package algorithm.misc.getters;

import java.util.ArrayList;
import java.util.List;

import algorithm.StaticSchedulingAlgorithm;
import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;

public class DR_CG_ManyToManyOutgoing implements DR_CatcherGetter {

    private final StaticSchedulingAlgorithm algorithm;

    public DR_CG_ManyToManyOutgoing(StaticSchedulingAlgorithm algorithm) {
        this.algorithm = algorithm;

    }

    @Override
    public List<LaneIndex> getCatchers() {
        List<LaneIndex> oneToManyLanes = new ArrayList<LaneIndex>();
        for (Lane lane : algorithm.getWorkflow().getLanes()) {
            if (lane.getUmodChildren().size() > 1) {
                oneToManyLanes.add(lane.getId());
            }
        }
        return oneToManyLanes;
    }

}
