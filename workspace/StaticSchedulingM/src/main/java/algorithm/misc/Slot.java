package algorithm.misc;

import statics.initialization.SchedulingTask;

/**
 *
 * @author mike
 */
public class Slot {

    public final SchedulingTask task;
    public final double start;
    public final double duration;
    public final Gap gap;

//    public Slot() {
//        // nothing
//    }
    public Slot(SchedulingTask task, double start, double duration, Gap gap) {
        this.task = task;
        this.start = start;
        this.duration = duration;
        this.gap = gap;
    }

    public Slot(SchedulingTask task, double start, double duration) {
        this(task, start, duration, null);
    }

    @Override
    public String toString() {
        return String.format("Slot task=%5d st=%10.2f et=%10.2f gap=%s", task.getId(), start, start + duration, gap);
    }

}
