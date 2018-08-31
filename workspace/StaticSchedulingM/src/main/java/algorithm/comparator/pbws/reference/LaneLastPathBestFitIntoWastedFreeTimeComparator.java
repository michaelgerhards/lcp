package algorithm.comparator.pbws.reference;

import java.util.Comparator;

import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Util;

/**
 * Combines catchers execution time with last path execution times of o1,o2. combinations with less free Remaining time before combinations with more free
 * remaining time
 *
 * @author Gerhards
 *
 */
public class LaneLastPathBestFitIntoWastedFreeTimeComparator
        implements Comparator<Lane>, ReferenceComparator<Lane, Lane> {

    private Lane lane;

    @Override
    public int compare(Lane o1, Lane o2) {

        double o1LpExecutionTime = Util.getExecutionTimeOfLastPathUsingSdlFix(o1, lane.getInstanceSize());

        double o2LpExecutionTime = Util.getExecutionTimeOfLastPathUsingSdlFix(o2, lane.getInstanceSize());

        if (o1LpExecutionTime == o2LpExecutionTime) {
            return 0;
        }
        if (o1LpExecutionTime < 0) {
            return 1;
        }
        if (o2LpExecutionTime < 0) {
            return -1;
        }

        BillingUtil bu = BillingUtil.getInstance();

        double executionTime = lane.getExecutionTime();
        double o1PcatcherFreeRemainingTime = bu.getUnusedCapacity(executionTime + o1LpExecutionTime);
        double o2PcatcherFreeRemainingTime = bu.getUnusedCapacity(executionTime + o2LpExecutionTime);

        if (o1PcatcherFreeRemainingTime > o2PcatcherFreeRemainingTime) {
            return 1;
        } else if (o1PcatcherFreeRemainingTime < o2PcatcherFreeRemainingTime) {
            return -1;
        } else {
            return 0;
        }

    }

    @Override
    public void setReference(Lane t) {
        this.lane = t;
    }

    @Override
    public Lane getReference() {
        return lane;
    }

}
