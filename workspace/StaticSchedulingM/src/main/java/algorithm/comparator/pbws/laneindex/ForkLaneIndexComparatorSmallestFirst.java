package algorithm.comparator.pbws.laneindex;

import java.util.Comparator;

import statics.initialization.impl.LaneIndex;

/**
 * forks with less branches before forks with more branches
 *
 * @author Gerhards
 *
 */
public class ForkLaneIndexComparatorSmallestFirst extends ForkLaneIndexComparatorLargestFirst implements Comparator<LaneIndex> {

    @Override
    public int compare(LaneIndex arg0, LaneIndex arg1) {
        return -super.compare(arg0, arg1);
    }

}
