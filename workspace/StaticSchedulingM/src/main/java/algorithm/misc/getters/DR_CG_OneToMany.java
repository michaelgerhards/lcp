package algorithm.misc.getters;

import java.util.ArrayList;
import java.util.List;

import algorithm.StaticSchedulingAlgorithm;
import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;

public class DR_CG_OneToMany implements DR_CatcherGetter {

    private final StaticSchedulingAlgorithm algorithm;

    public DR_CG_OneToMany(StaticSchedulingAlgorithm algorithm) {
        this.algorithm = algorithm;

    }

    /**
     *
     * @return a list of lane indexes for lanes that have more than one successor with exactly one predecessor
     */
    @Override
    public List<LaneIndex> getCatchers() {
        List<LaneIndex> oneToManyLanes = new ArrayList<LaneIndex>();
        for (Lane lane : algorithm.getWorkflow().getLanes()) {
//			if (lane == null) {
//				continue;
//			}
            if (lane.getUmodChildren().size() > 1) {
                boolean add = true;
                for (Lane succ : lane.getUmodChildren()) {
                    if (succ.getUmodParents().size() > 1) {
                        add = false;
                    }
                }
                if (add) {
                    oneToManyLanes.add(lane.getId());
                }
            }
        }
        return oneToManyLanes;
    }

}
