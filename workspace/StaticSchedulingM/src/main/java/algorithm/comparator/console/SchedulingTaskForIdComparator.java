package algorithm.comparator.console;

import java.util.Comparator;

import statics.initialization.SchedulingTask;

public class SchedulingTaskForIdComparator implements
        Comparator<SchedulingTask> {

    @Override
    public int compare(SchedulingTask o1, SchedulingTask o2) {
        if (o1 == o2) {
            return 0;
        }
        SchedulingTask entry = o1.getWorkflow().getEntry();
        SchedulingTask exit = o1.getWorkflow().getExit();

        if (o1 == entry) {
            return -1;
        } else if (o2 == entry) {
            return 1;
        }

        if (o1 == exit) {
            return 1;

        } else if (o2 == exit) {
            return -1;
        }

        return o1.compareTo(o2);
    }

}
