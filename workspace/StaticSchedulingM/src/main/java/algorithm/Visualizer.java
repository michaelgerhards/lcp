package algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import algorithm.comparator.console.LaneForIdComparator;
import algorithm.comparator.console.SchedulingTaskForIdComparator;
import algorithm.pcp.strategy.StrategyResult;
import cloud.BasicInstance;
import cloud.Instance;
import cloud.InstanceSize;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Debug;

public class Visualizer {

    private static final double sec_in_min = 60.;

    private final BillingUtil billing;

    private final WorkflowContainer algorithm;

    public Visualizer(WorkflowContainer algorithm) {
        this.algorithm = algorithm;
        billing = BillingUtil.getInstance();
    }

    private String formatRowStartTime() {
        List<SchedulingTask> tasks = new ArrayList<>(algorithm.getWorkflow().getTasks());
        Collections.sort(tasks, new SchedulingTaskForIdComparator());

        StringBuilder buf = new StringBuilder(tasks.size() * 4 + 1);
        for (SchedulingTask task : tasks) {
            buf.append(String.format("%8.0f    ", task.getStartTime()));
        }
        return buf.toString();
    }

    private String formatRowEndTime() {
        List<SchedulingTask> tasks = new ArrayList<>(algorithm.getWorkflow().getTasks());
        Collections.sort(tasks, new SchedulingTaskForIdComparator());

        StringBuilder buf = new StringBuilder(tasks.size() * 4 + 1);
        for (SchedulingTask task : tasks) {
            buf.append(String.format("%8.0f    ", task.getEndTime()));
        }
        return buf.toString();
    }

    private String formatRowLatestEndTime() {
        List<SchedulingTask> tasks = new ArrayList<>(algorithm.getWorkflow().getTasks());
        Collections.sort(tasks, new SchedulingTaskForIdComparator());

        StringBuilder buf = new StringBuilder(tasks.size() * 4 + 1);
        for (SchedulingTask task : tasks) {
            buf.append(String.format("%8.0f    ", task.getLatestEndTime()));
        }
        return buf.toString();
    }

    private String formatHeadline() {
        List<SchedulingTask> tasks = new ArrayList<>(algorithm.getWorkflow().getTasks());
        Collections.sort(tasks, new SchedulingTaskForIdComparator());
        StringBuilder buf = new StringBuilder(algorithm.getWorkflow().getTasks().size() * 4 + 1);
        for (SchedulingTask task : tasks) {
            buf.append(String.format("%8s    ", task.toString()));
        }
        String result = buf.toString();
        return result;
    }

    private String formatInstances() {
        List<SchedulingTask> tasks = new ArrayList<>(algorithm.getWorkflow().getTasks());
        Collections.sort(tasks, new SchedulingTaskForIdComparator());

        StringBuilder buf = new StringBuilder();
        for (SchedulingTask task : tasks) {

            String value;
            if (task.getResource() != null) {
                value = task.getResource().getName();
            } else {
                value = "???";
            }
            buf.append(String.format("%8s    ", value));
        }
        return buf.toString();
    }

    public static String formatList(List<SchedulingTask> instances) {
        int length = ("" + instances.size()).length();
        StringBuilder result = new StringBuilder((length + 1) * instances.size() + 10);
        result.append("[");
        for (SchedulingTask inst : instances) {
            result.append(String.format("%s ", inst));
        }
        result.append("]");
        return result.toString();
    }

    public void printInstancesInMin() {
        Debug.INSTANCE.aPrintf("%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s\t%10s%n", "instan", "start (m)", "stop (m)", "durati (m)", "atus", "total cost", "remaining", "assigned tasks");
        double totalCosts = 0.;

        List<Lane> instances = new ArrayList<>(algorithm.getWorkflow().getLanes());
        Collections.sort(instances, new LaneForIdComparator());

        double endtime = 0.;
        double sumRemaining = 0;
        for (Instance instance : instances) {
            if (instance.getEndTime() > endtime) {
                endtime = instance.getEndTime();
            }
            double duration = instance.getEndTime() - instance.getStartTime();
            int atus = billing.getUsedATUs(duration);
            double resourceCosts = billing.getCosts(atus, instance.getInstanceSize());
            totalCosts += resourceCosts;
            double remaining = billing.getUnusedCapacity(instance.getExecutionTime()) / 60;
            sumRemaining += remaining;
            String assignedTasks = formatList(instance.getUmodTasks());

            Debug.INSTANCE.aPrintf("%-10s\t", instance);
            Debug.INSTANCE.aPrintf("%10.2f\t%10.2f\t%10.2f\t", instance.getStartTime() / sec_in_min, instance.getEndTime() / sec_in_min, duration / sec_in_min);
            Debug.INSTANCE.aPrintf("%10s\t", atus);
            Debug.INSTANCE.aPrintf("%10s\t", resourceCosts);
            Debug.INSTANCE.aPrintf("%10.2f\t", remaining);
            Debug.INSTANCE.aPrintf("%s", assignedTasks);
            Debug.INSTANCE.aPrintln();
        }
        Debug.INSTANCE.aPrintln("remaining (m): " + sumRemaining);
        Debug.INSTANCE.aPrintln("total costs  : " + totalCosts);
        Debug.INSTANCE.aPrintln("end time (m) : " + endtime / sec_in_min);
        Debug.INSTANCE.aPrintln("deadline (m) : " + algorithm.getWorkflow().getDeadline() / sec_in_min);
    }

    public void printInstancesInSec() {
        double totalCosts = 0.;

        List<Lane> instances = new ArrayList<>(algorithm.getWorkflow().getLanes());
        Collections.sort(instances, new LaneForIdComparator());

        double endtime = 0.;
        double sumRemaining = 0;
        for (Instance instance : instances) {

            if (instance.getEndTime() > endtime) {
                endtime = instance.getEndTime();
            }
            double duration = instance.getExecutionTime();
            int atus = billing.getUsedATUs(duration);
            double resourceCosts = billing.getCosts(atus, instance.getInstanceSize());
            totalCosts += resourceCosts;
            double remaining = billing.getUnusedCapacity(instance.getExecutionTime());
            sumRemaining += remaining;
            String assignedTasks = formatList(instance.getUmodTasks());

            Debug.INSTANCE.aPrintf("%-10s\t", instance);
            Debug.INSTANCE.aPrintf("%10.2f\t%10.2f\t%10.2f\t", instance.getStartTime(), instance.getEndTime(), duration);
            Debug.INSTANCE.aPrintf("%10s\t", atus);
            Debug.INSTANCE.aPrintf("%10s\t", resourceCosts);
            Debug.INSTANCE.aPrintf("%10.2f\t", remaining);
            Debug.INSTANCE.aPrintf("%s", assignedTasks);
            Debug.INSTANCE.aPrintln();
        }
        Debug.INSTANCE.aPrintln("remaining (s): " + sumRemaining);
        Debug.INSTANCE.aPrintln("total costs  : " + totalCosts);
        Debug.INSTANCE.aPrintln("end time (s) : " + endtime);
        Debug.INSTANCE.aPrintln("deadline (s) : " + algorithm.getWorkflow().getDeadline());
    }

    public void printDependenciesWithCosts() {
        Debug.INSTANCE.aPrintln("printDependenciesWithCosts");
        List<SchedulingTask> tasks = new ArrayList<>(algorithm.getWorkflow().getTasks());
        Collections.sort(tasks, new SchedulingTaskForIdComparator());

        for (SchedulingTask parent : tasks) {
            double executionTime = 0;
            if (parent.getResource() != null) {
                executionTime = parent.getExecutionTime(parent.getResource()
                        .getInstanceSize());
            }

            Debug.INSTANCE.aPrintf("%3d | %10.2f\t", parent, executionTime);

            Collection<SchedulingTask> children = parent.getChildren();

            List<SchedulingTask> sortedChildren = new ArrayList<SchedulingTask>();
            sortedChildren.addAll(children);
            Collections.sort(sortedChildren);

            for (SchedulingTask child : sortedChildren) {
                Debug.INSTANCE.aPrintf("%s | %10.2f\t", child, 0);
            }
            Debug.INSTANCE.aPrintln();
        }
    }

    public void printTaskInformation() {
        Debug.INSTANCE.aPrintln("printTaskInformation");
        Debug.INSTANCE.aPrintln("id\tname\tfastest ex time");

        List<SchedulingTask> tasks = new ArrayList<>(algorithm.getWorkflow().getTasks());
        Collections.sort(tasks, new SchedulingTaskForIdComparator());

        for (SchedulingTask task : tasks) {
            Debug.INSTANCE.aPrintf("%s\t", task);
            String name = task.toString(); // XXX or type?
            Debug.INSTANCE.aPrintf("%20s\t", name);
            for (InstanceSize size : algorithm.getWorkflow().getInstanceSizes()) {
                double executionTime = task.getExecutionTime(size);
                Debug.INSTANCE.aPrintf("%10.3f\t", executionTime);
            }

            Debug.INSTANCE.aPrintln();
        }

    }

    public <T> void debugAssignPath(List<SchedulingTask> pcp,
            BasicInstance<T> instance, StrategyResult data, String strategyName) {
        if (Debug.INSTANCE.getDebug() == Integer.MAX_VALUE) {
            if (data != null) {
                Debug.INSTANCE.aPrintln("strategy " + strategyName
                        + " applies yes for " + formatList(pcp)
                        + " on instance " + instance + " costs= "
                        + data.getCosts());
            } else if (Debug.INSTANCE.getDebug() > 50) {
                Debug.INSTANCE.aPrintln("strategy " + strategyName
                        + " applies not for " + formatList(pcp)
                        + " on instance " + instance);
            }
        }
    }

    public void printSchedulingTable() {
        if (Debug.INSTANCE.getDebug() > 1) {
            Debug.INSTANCE.aPrintln("-----------------------------------------");
            Debug.INSTANCE.aPrintln("tasks " + formatHeadline());
            Debug.INSTANCE.aPrintln("est   " + formatRowStartTime());
            Debug.INSTANCE.aPrintln("eft   " + formatRowEndTime());
            Debug.INSTANCE.aPrintln("inst. " + formatInstances());
            Debug.INSTANCE.aPrintln("-----------------------------------------");
        }
    }

    public void printSchedulingTableWithoutInstances() {
        if (Debug.INSTANCE.getDebug() > 1) {
            Debug.INSTANCE.aPrintln("-----------------------------------------");
            Debug.INSTANCE.aPrintln("tasks " + formatHeadline());
            Debug.INSTANCE.aPrintln("est   " + formatRowStartTime());
            Debug.INSTANCE.aPrintln("eft   " + formatRowEndTime());
            Debug.INSTANCE.aPrintln("-----------------------------------------");
        }
    }

    public void printSchedulingTableWithLftWithoutInstances() {
        if (Debug.INSTANCE.getDebug() > 1) {
            Debug.INSTANCE.aPrintln("-----------------------------------------");
            Debug.INSTANCE.aPrintln("tasks " + formatHeadline());
            Debug.INSTANCE.aPrintln("est   " + formatRowStartTime());
            Debug.INSTANCE.aPrintln("eft   " + formatRowEndTime());
            Debug.INSTANCE.aPrintln("lft   " + formatRowLatestEndTime());
            Debug.INSTANCE.aPrintln("-----------------------------------------");
        }
    }

}
