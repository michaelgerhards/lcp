package statics.result;

import java.util.Collection;

import cloud.Instance;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.Debug;

public class CheckResourceAllocationPlan {

    public static final double THRESHOLD = 0.1;

    private final WorkflowInstance workflow;

    private boolean checkDeadline = true;

    public CheckResourceAllocationPlan(WorkflowInstance result) {
        this.workflow = result;
    }

    public boolean check() {
        String a = checkResourceAssignments();
        String c = checkServices();
        String d = checkTransitions();
        String[] results = new String[]{a, c, d};

        for (String r : results) {
            if (r != null) {
                Debug.INSTANCE.aPrintln();
                Debug.INSTANCE.aPrintln("check start");
                Debug.INSTANCE.aPrintln(r);
                Debug.INSTANCE.aPrintln("check end");
                return false;
            }
        }
        return true;
    }

    private String checkResourceAssignments() {
        for (SchedulingTask task : workflow.getTasks()) {
            if (task.getResource() == null) {
                return "Not all Tasks are scheduled, Resource missing for task= " + task;
            } else if (!workflow.getInstances().containsValue(task.getResource())) {
                return "Resource is not part of Workflow Economy: task= " + task + " instance= " + task.getResource();
            }
        }
        return null;
    }

    private String checkServices() {
        for (Instance service : workflow.getInstances().values()) {
            double time = service.getUmodTasks().get(0).getStartTime();
            for (SchedulingTask task : service.getUmodTasks()) {
                if (task.getResource() != service) {
                    return "task is scheduled twice: task= " + task + " instance= " + service;
                }

                double currentStartTime = task.getStartTime();
                if (currentStartTime - time < -THRESHOLD) {
                    // current task starts while predecessor is running
                    String append = "\ntime=" + time + " currentstarttime="
                            + currentStartTime;
                    return "problem with start time of task " + task + append;
                }

                double currentEndTime = task.getEndTime();
                // double currentEndTime = currentStartTime
                // + task.getExecutionTime(service.getInstanceSize()); // XXX
                // only for planning!
                if (Math.abs(currentEndTime - task.getEndTime()) > THRESHOLD) {
                    String append = "\ntime=" + task.getEndTime()
                            + " currentendtime=" + currentEndTime;
                    return "problem with end time of task " + task + append;
                }
                time = currentEndTime;

                if (checkDeadline && workflow.getDeadline() - time < -THRESHOLD) {
                    return "Deadline Violated: time= " + time + " dl= "
                            + workflow.getDeadline();
                }
            }
        }
        return null;
    }

    private String checkTransitions() {
        return checkTransitions(workflow.getEntry());
    }

    private String checkTransitions(SchedulingTask task) {
        Collection<SchedulingTask> children = task.getChildren();
        for (SchedulingTask child : children) {
            if (child.getStartTime() - task.getEndTime() < -THRESHOLD) {
                return "child starts before parent ends: parent=" + task
                        + " child=" + child + " parentend=" + task.getEndTime()
                        + " childstart=" + child.getStartTime();
            }
            String message = checkTransitions(child);
            if (message != null) {
                return message;
            }
        }
        return null;
    }

    public static void checkPlan(WorkflowInstance workflow) {
        {
            CheckResourceAllocationPlan cr = new CheckResourceAllocationPlan(
                    workflow);

            boolean check = cr.check();

            if (!check) {
                Debug.INSTANCE.aPrintln("check2= " + check);
                System.exit(0);
            }
        }
    }

    public boolean isCheckDeadline() {
        return checkDeadline;
    }

    public void setCheckDeadline(boolean checkDeadline) {
        this.checkDeadline = checkDeadline;
    }

}
