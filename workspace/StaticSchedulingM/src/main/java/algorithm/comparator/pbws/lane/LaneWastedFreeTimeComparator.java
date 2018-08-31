package algorithm.comparator.pbws.lane;

import java.util.Comparator;

import statics.initialization.impl.Lane;
import statics.util.BillingUtil;

/**
 * lanes with more wasted free time before lanes with less wasted free time.
 *
 * @author Gerhards
 *
 */
public class LaneWastedFreeTimeComparator implements Comparator<Lane> {

    @Override
    public int compare(Lane o1, Lane o2) {
        BillingUtil bu = BillingUtil.getInstance();

        double o1WastedTime = bu.getUnusedCapacity(o1.getExecutionTime());
        double o2WastedTime = bu.getUnusedCapacity(o2.getExecutionTime());

        if (o1WastedTime > o2WastedTime) {
            return -1;
        } else if (o1WastedTime < o2WastedTime) {
            return 1;
        } else {
            return 0;
        }

    }

}
