package statics.util;

import cloud.Cloud;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cloud.InstanceSize;
import java.util.List;
import reality.executiontimes.ExecutionTimes;
import statics.initialization.DependencyGraph;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.initialization.WorkflowTemplate;
import statics.initialization.impl.MeanAlphaSigmaPlanner;

public class CompareAlgorithm {

    public static double getCheapestRealTime(Set<SchedulingTask> tasks, Cloud cloud, ExecutionTimes daxExTimes) {
        InstanceSize mostProfitableSize = new CloudUtil(cloud.getSizes()).getMostProfitableSzie();
        
        double totalExecutionTime = 0.;
        for (SchedulingTask task : tasks) {
            if (task.getId() == DependencyGraph.ENTRY_ID || task.getId() == DependencyGraph.EXIT_ID) {
                continue;
            }
            totalExecutionTime += daxExTimes.getExecutionTime(task, mostProfitableSize);
        }
        BillingUtil bu = new BillingUtil(cloud.getAtuLength());
        int atus = bu.getUsedATUs(totalExecutionTime);
         // if the most profitable instance size is not the cheapest one, theoretically cheaper solutions could exist!
        double totalCost = bu.getCosts(atus, mostProfitableSize);
        
        return totalCost;
    }

    private CompareAlgorithm() {
        // nothing
    }

    public static double getCheapest(WorkflowInstance workflow) {
        double[] cheapestCostAndMakespan = getCheapestCostAndMakespan(workflow);
        double cost = cheapestCostAndMakespan[0];
        return cost;
    }

    /**
     * [cheapest cost, sum of all execution times on cheapest instance]
     *
     * @param workflow
     * @return
     */
    public static double[] getCheapestCostAndMakespan(WorkflowInstance workflow) {

        CloudUtil cu = new CloudUtil(workflow.getInstanceSizes());
        InstanceSize cheapestSize = cu.getCheapestSize();

        double sumEx = 0.;
        for (SchedulingTask task : workflow.getTasks()) {
            if (task == workflow.getEntry() || task == workflow.getExit()) {
                continue;
            }

            double ex = task.getExecutionTime(cheapestSize);
            sumEx += ex;
        }

        BillingUtil bu = BillingUtil.getInstance();

        int usedATUs = bu.getUsedATUs(sumEx);
        double cost = bu.getCosts(usedATUs, cheapestSize);

        return new double[]{cost, sumEx};
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
                double estParent = calcEST(parent, sts);
                double metParent;
                if (parent.getLane() == null) {
                    metParent = cloudUtil.getFastestExecutionTime(parent);
                } else {
                    metParent = parent.getExecutionTime(parent.getLane().getInstanceSize());
                }
                double value = estParent + metParent;
                if (value > maxValue) {
                    maxValue = value;
                }
            }
            sts.put(current, maxValue);
            return maxValue;
        }
    }

    public static double getMaxAlpha(WorkflowTemplate template, double dl) {

        double eps = 10;
        double alphaMin = 0.;

        WorkflowInstance createWorkflowInstance = template
                .createWorkflowInstance(dl, new MeanAlphaSigmaPlanner(alphaMin));
        double fastest = getFastest(createWorkflowInstance);

        double res = dl - fastest;

        if (res < 0) {
            throw new RuntimeException("dl < fastest: dl= " + dl + " fastest= " + fastest);
        }

        if (res < eps) {
            return alphaMin;
        }

        double alphaMax = 1;

        do {
            template.updatePlanner(createWorkflowInstance, new MeanAlphaSigmaPlanner(alphaMax));
            fastest = getFastest(createWorkflowInstance);

            res = dl - fastest;
            if (res > 0) {
                alphaMax *= 2;
            }
        } while (res > 0);

        double alpha = (alphaMin + alphaMax) / 2.;

        do {
            template.updatePlanner(createWorkflowInstance, new MeanAlphaSigmaPlanner(alpha));

            fastest = getFastest(createWorkflowInstance);

            res = dl - fastest;

            if (res < 0) {
                alphaMax = alpha;
            } else if (res < eps) {
                return alpha;
            } else {
                alphaMin = alpha;
            }
            alpha = (alphaMin + alphaMax) / 2.;
        } while (res > eps || res < 0);
        return alpha;
    }

}
