package algorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.initialization.impl.Lane;
import statics.util.CloudUtil;
import statics.util.Util;

import static statics.initialization.WorkflowInstance.performanceMode;

public abstract class AbstractAlgorithm implements StaticSchedulingAlgorithm {

    public static final double RESOURCE_OVERHEAD = 0;

    public boolean printPerformance = false;
    private Visualizer visualizer;
    private WorkflowInstance workflow;

    public final Logger logger;

    private CloudUtil cloudUtil;

    public AbstractAlgorithm() {
        this(Logger.getRootLogger());
    }

    public AbstractAlgorithm(Logger logger) {
        this.logger = logger;
        info("");
        info("created algorithm: " + getAlgorithmName());
    }

    private void calcEST() {
        info("calcEST start");
        long s = System.currentTimeMillis();
        calcEST(getWorkflow().getExit());
        long e = System.currentTimeMillis();
        long d = e - s;
        info("calcEST took ms:\t" + d);
    }

    public static double calcEST(SchedulingTask current) {
        // EST = earliest start time
        if (current == current.getWorkflow().getEntry()) {
            current.setStartTime(0);
            current.setEndTime(0);
        } else if (current.getStartTime() >= 0) {
            // nothing, value already exists.
        } else if (current.getStartTime() < 0) {
            Set<SchedulingTask> parentNodes = current.getParents();
            // cannot be scheduled before now, planning value is 0
            double maxValue = Time.getInstance().getActualTime();
            CloudUtil cloudUtil = CloudUtil.getInstance();
            for (SchedulingTask parent : parentNodes) {
                double estParent = calcEST(parent);
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
            current.setStartTime(maxValue);
        }
        return current.getStartTime();
    }

    private void calcEFT() {
        info("calcEFT start");
        long s = System.currentTimeMillis();

        // eft = earliest finish time
        for (SchedulingTask task : getWorkflow().getTasks()) {
            // only for tasks without end-time
            if (task.getEndTime() < 0) {
                double starTime = task.getStartTime();
                double endTime = starTime + getCloudUtil().getFastestExecutionTime(task);
                task.setEndTime(endTime);

                if (Time.getInstance().getActualTime() == 0 && endTime - getWorkflow().getDeadline() > -Util.DOUBLE_THRESHOLD) {
                    throw new RuntimeException("invalid deadline: " + task + " et=" + endTime + " st=" + starTime + " dl=" + getWorkflow().getDeadline());
                }
            }
        }

        long e = System.currentTimeMillis();
        long d = e - s;
        info("calcEFT took ms:\t" + d);
    }

    @Override
    public abstract String getAlgorithmName();

    @Override
    public abstract String getAlgorithmNameAbbreviation();

    @Override
    public final WorkflowInstance schedule(WorkflowInstance workflow) {
        info("schedule start for " + workflow.getWorkflowName() + " with deadline " + workflow.getDeadline());
        long s = System.currentTimeMillis();

        this.setWorkflow(workflow);
        cloudUtil = CloudUtil.getInstance();
        visualizer = new Visualizer(this);

        if (workflow.getAlgorithmName() == null || workflow.getAlgorithmName().isEmpty()) {
            workflow.setAlgorithmName(getAlgorithmName());
        }

        calcEST();
        calcEFT();
        scheduleInternLog();
        long e = System.currentTimeMillis();
        long d = e - s;
        info("schedule took ms:\t" + d);
        return getWorkflow();
    }

    private void scheduleInternLog() {
//        info("scheduleInternLog start");
//        long s = System.currentTimeMillis();
        scheduleIntern();
//        long e = System.currentTimeMillis();
//        long d = e - s;
//        info("scheduleInternLog took ms:\t" + d);
    }

    protected abstract void scheduleIntern();

    @Override
    public WorkflowInstance getWorkflow() {
        return workflow;
    }

    public Visualizer getVisualizer() {
        return visualizer;
    }

    protected CloudUtil getCloudUtil() {
        return cloudUtil;
    }

    protected void setWorkflow(WorkflowInstance workflow) {
        this.workflow = workflow;
    }

    public static double calcLFT(SchedulingTask current) {
        if (current == current.getWorkflow().getExit()) {
            current.setLatestEndTime(current.getWorkflow().getDeadline());
        } else if (current.getLatestEndTime() >= 0) {
            // nothing
        } else if (current.getLatestEndTime() < 0) {
            Collection<SchedulingTask> childNodes = current.getChildren();
            double minValue = Double.MAX_VALUE;
            for (SchedulingTask child : childNodes) {
                double lftChild = calcLFT(child);
                double metChild;
                if (child.getLane() == null) {
                    metChild = CloudUtil.getInstance().getFastestExecutionTime(child);
                } else {
                    metChild = child.getExecutionTime(child.getLane().getInstanceSize());
                }
                double value = lftChild - metChild;
                if (value < minValue) {
                    minValue = value;
                }
                if (minValue < -Util.DOUBLE_THRESHOLD) {
                    throw new RuntimeException("lft < 0: task= " + current + " child= " + child + " lft met tt " + lftChild + " " + metChild);
                } else if (minValue < 0) {
                    minValue = 0; // value lower 0 but higher threshold
                }
            }
            current.setLatestEndTime(minValue);
        }
        return current.getLatestEndTime();
    }

    public static Set<Lane> getParentLanesOfTaskSetReadOnly(SchedulingTask joinTask) {
        if (joinTask.getLane().getTasksCount() > 1) {
            Set<Lane> parentsOfJoinTask = new HashSet<>(Math.min(100, joinTask.getParents().size()));

            Collection<SchedulingTask> parents = joinTask.getParents();
            for (SchedulingTask parent : parents) {
                Lane parentLane = parent.getLane();
                parentsOfJoinTask.add(parentLane);
            }
            Lane joinLane = joinTask.getLane();
            parentsOfJoinTask.remove(joinLane);
            parentsOfJoinTask = performanceMode ? parentsOfJoinTask : Collections.unmodifiableSet(parentsOfJoinTask);
            return parentsOfJoinTask;
        } else {
            return joinTask.getLane().getUmodParents();
        }
    }

    public static boolean isJoinLane(Lane lane) {
        return lane.getUmodParents().size() > 1;
    }

    public static boolean isJoinTask(SchedulingTask task) {
        return task.getParents().size() > 1;
    }

    public static boolean isStillJoinTask(SchedulingTask task) {
        if (!isJoinTask(task)) {
            return false;
        }
        Collection<SchedulingTask> parents = task.getParents();
        Lane joinLane = task.getLane();
        Set<Lane> parentLanes = new HashSet<Lane>();
        parentLanes.add(joinLane);
        for (SchedulingTask parent : parents) {
            Lane parentLane = parent.getLane();
            parentLanes.add(parentLane);
            if (parentLanes.size() >= 3) {
                // 3 lanes: task, parent1, parent2 -> task is join
                return true;
            }
        }
        return false;
    }

    private String indent = "\t";

    protected final void info(String s) {
        logger.info(indent + s);
    }

    protected final void indent() {
        indent += "\t";
    }

    protected final void unindent() {
        if(indent.length() == 1) {
            throw new RuntimeException();
        }
        
        indent = indent.substring(0, indent.length() - 1);
    }
    
    @Override
    public Logger getLogger() {
        return logger;
    }

}
