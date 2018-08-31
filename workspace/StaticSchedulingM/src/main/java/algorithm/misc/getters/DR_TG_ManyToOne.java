package algorithm.misc.getters;

import java.util.ArrayList;
import java.util.List;

import algorithm.StaticSchedulingAlgorithm;
import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;

public class DR_TG_ManyToOne implements DR_ThrowerGetter {

    private final StaticSchedulingAlgorithm algorithm;

    public DR_TG_ManyToOne(StaticSchedulingAlgorithm algorithm) {
        this.algorithm = algorithm;

    }

    /**
     *
     * @return a list of lane indexes for lanes that have more than one predecessor with not other successors
     */
    @Override
    public List<LaneIndex> getThrowers() {
        List<LaneIndex> manyToOneLanes = new ArrayList<>();
        for (Lane lane : algorithm.getWorkflow().getLanes()) {
            if (lane == null) {
                continue;
            }
            if (lane.getUmodParents().size() > 1) {
                boolean add = true;
                for (Lane pred : lane.getUmodParents()) {
                    if (pred.getUmodChildren().size() > 1) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    manyToOneLanes.add(lane.getId());
                }
            }
        }
        return manyToOneLanes;
    }

}
