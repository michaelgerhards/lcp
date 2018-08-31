package algorithm.comparator.pbws.reference;

import java.util.Comparator;

import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Util;

/**
 * Combines lanes execution time with full path execution times of o1,o2. combinations with less free Remaining time before combinations with more free
 * remaining time
 *
 * @author Gerhards
 *
 */
public class LaneFullPathBestFitIntoWastedFreeTimeComparator implements
        Comparator<Lane>, ReferenceComparator<Lane, Lane> {

    private Lane lane;

    @Override
    public int compare(Lane o1, Lane o2) {
        // List<SchedulingTask> o1LP = o1.getUmodTasks();
        double o1LpExecutionTime = Util.getExecutionTimeOfLastPathUsingSdlFix(o1,
                lane.getInstanceSize());

        // List<SchedulingTask> o2LP = o2.getUmodTasks();
        double o2LpExecutionTime = Util.getExecutionTimeOfLastPathUsingSdlFix(o2,
                lane.getInstanceSize());

        if (o1LpExecutionTime == o2LpExecutionTime) {
            return 0;
        }
        if (o1LpExecutionTime < 0) {
            return 1;
        }
        if (o2LpExecutionTime < 0) {
            return -1;
        }

        double executionTime = lane.getExecutionTime();
        BillingUtil bu = BillingUtil.getInstance();
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
