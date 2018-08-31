package dynamic.scheduling.engine;

import java.util.List;

import statics.initialization.SchedulingTask;
import statics.util.Debug;
import statics.util.outputproxy.Proxy;

public class EngineUtil {

    public static List<SchedulingTask> printLongestPath(SchedulingTask task) {
        List<SchedulingTask> newList = task.calcLongestPathToExit();
        Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_REMAP_CHECK, Proxy.collectionToString(newList));

        for (SchedulingTask t : newList) {
            Debug.INSTANCE.printf(WorkflowEngineImpl.DEBUG_REMAP_CHECK, "%10s\t%s%n", t, t.getResource());
        }

        if (newList.size() > 1) {
            SchedulingTask newFirst = newList.get(0);
            SchedulingTask newLast = newList.get(newList.size() - 2);

            double newDuration = newLast.getEndTime() - newFirst.getStartTime();

            String newS = String.format("%10.3f %10.3f %10.3f", newFirst.getStartTime(), newLast.getEndTime(), newDuration);
            Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_REMAP_CHECK, newS);
        }
        return newList;
    }

}
