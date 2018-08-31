package statics.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;

public class GetFastestByMeanTime {

    private GetFastestByMeanTime() {
        // nothing
    }

    public static double getFastest(WorkflowInstance workflow) {
        double fastTime = calcEST(workflow);
        return fastTime;
    }

    private static double calcEST(WorkflowInstance workflow) {
        Map<SchedulingTask, Double> sts = new HashMap<>(workflow.getTasks().size());
        return calcEST(workflow.getExit(), sts);
    }

    private static double calcEST(SchedulingTask current, Map<SchedulingTask, Double> sts) {
        WorkflowInstance workflow = current.getWorkflow();
        CloudUtil cloudUtil = CloudUtil.getInstance();

        // EST = earliest start time
        if (current == workflow.getEntry()) {
            return 0.;
        } else if (sts.containsKey(current)) {
            return sts.get(current);
        } else {
            Set<SchedulingTask> parentNodes = current.getParents();
            double maxValue = -1;
            for (SchedulingTask parent : parentNodes) {
                double metParent = parent.getMeanExecutionTime(cloudUtil.getFastestSize(parent));
               
                double estParent = calcEST(parent, sts);
                double value = estParent + metParent;
                if (value > maxValue) {
                    maxValue = value;
                }
            }
            sts.put(current, maxValue);
            return maxValue;
        }
    }

}
