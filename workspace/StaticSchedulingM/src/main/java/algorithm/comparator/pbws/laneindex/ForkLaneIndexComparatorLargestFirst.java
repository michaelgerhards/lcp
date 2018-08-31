package algorithm.comparator.pbws.laneindex;

import java.util.Comparator;

import statics.initialization.impl.LaneIndex;

/**
 * forks with more branches before forks with less branches.
 *
 *
 * @author Gerhards
 *
 */
public class ForkLaneIndexComparatorLargestFirst implements Comparator<LaneIndex> {

    @Override
    public int compare(LaneIndex arg0, LaneIndex arg1) {
        return arg1.getLane().getUmodChildren().size() - arg0.getLane().getUmodChildren().size();

    }

}
