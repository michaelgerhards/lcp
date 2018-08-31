package algorithm.misc;

import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.util.Util;

/**
 *
 * @author mike
 */
public class Gap implements Comparable<Gap> {

    public final Lane lane;
    public final double start;
    public final double end;
    public final SchedulingTask pred;
    public final SchedulingTask succ;

    public Gap(Lane lane, double start, double end, SchedulingTask pred, SchedulingTask succ) {
        this.lane = lane;
        this.start = start;
        this.end = end;
        this.pred = pred;
        this.succ = succ;

    }

    @Override
    public String toString() {
        return String.format("gap: lid=%5s, st=%10.2f et=10%.2f pred=%d succ=%d", lane.getId().toString(), start, end, pred.getId(), succ.getId());
    }

    @Override
    public int compareTo(Gap o) {
        double diff = start - o.start;
        if (diff < -Util.DOUBLE_THRESHOLD) {
            return -1;
        } else if (diff > Util.DOUBLE_THRESHOLD) {
            return 1;
        } else {
            return 0;
        }
    }

}
