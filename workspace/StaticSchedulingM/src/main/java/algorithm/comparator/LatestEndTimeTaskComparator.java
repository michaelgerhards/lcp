package algorithm.comparator;

import java.util.Comparator;
import statics.initialization.SchedulingTask;

/**
 *
 * @author mike
 */
public class LatestEndTimeTaskComparator implements Comparator<SchedulingTask> {

    private final Comparator<SchedulingTask> second = new LeastFlexibleComparator();
    
    @Override
    public int compare(SchedulingTask t1, SchedulingTask t2) {
        if (t1 == t2) {
            return 0;
        }
        SchedulingTask exit = t1.getWorkflow().getExit();
        if (t1 == exit) {
            return 1;
        } else if (t2 == exit) {
            return -1;
        } else {
            double d1 = t1.getLatestEndTime();
            double d2 = t2.getLatestEndTime();
            double diff = d1 - d2;
            if (diff > statics.util.Util.DOUBLE_THRESHOLD) {
                return 1;
            } else if (diff < -statics.util.Util.DOUBLE_THRESHOLD) {
                return -1;
            } else {
                return second.compare(t1, t2);
            }
        }
    }

}
