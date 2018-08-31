package algorithm.comparator;

import cloud.InstanceSize;
import java.util.Comparator;
import statics.initialization.SchedulingTask;
import statics.util.CloudUtil;

/**
 *
 * @author mike
 */
public class LeastFlexibleComparator implements Comparator<SchedulingTask> {

    @Override
    public int compare(SchedulingTask t1, SchedulingTask t2) {
        if (t1 == t2) {
            return 0;
        }
        SchedulingTask exit = t1.getWorkflow().getExit();
        SchedulingTask entry = t1.getWorkflow().getExit();
        if (t1 == entry) {
            return -1;
        } else if (t2 == entry) {
            return 1;
        } else if (t1 == exit) {
            return 1;
        } else if (t2 == exit) {
            return -1;
        } else {
            InstanceSize s1 = CloudUtil.getInstance().getFastestSize(t1);
            double d1 = t1.getLatestEndTime() - (t1.getStartTime() + t1.getExecutionTime(s1));
            InstanceSize s2 = CloudUtil.getInstance().getFastestSize(t2);
            double d2 = t2.getLatestEndTime() - (t2.getStartTime() + t2.getExecutionTime(s2));
            double diff = d1 - d2;
            if (diff > statics.util.Util.DOUBLE_THRESHOLD) {
                return 1;
            } else if (diff < -statics.util.Util.DOUBLE_THRESHOLD) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
