package algorithm.pcp.strategy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithm.pcp.CriticalPathAlgorithm;
import cloud.BasicInstance;
import cloud.InstanceSize;
import statics.initialization.SchedulingTask;
import statics.util.CloudUtil;
import statics.util.Duration;
import statics.util.Util;

import static statics.initialization.WorkflowInstance.performanceMode;

abstract class AbstractStrategy implements Duration, StrategyResult, Strategy {

    private BasicInstance<SchedulingTask> instance;
    private double starttime = -1;
    private double endtime = -1;
    private final CriticalPathAlgorithm algorithm;
    private List<SchedulingTask> pcp;
    private final Map<SchedulingTask, Double> localTaskStarttimes = new HashMap<>();
    private double costs = -1;
    private int loopCount = 0;
    private int accept = 0; // strategy produced result
    private int take = 0; // strategy was selected
    private AbstractStrategy parent;

    private CloudUtil cloudUtil;

    public AbstractStrategy(CriticalPathAlgorithm algorithm) {
        this.algorithm = algorithm;

    }

    public CloudUtil getCloudUtil() {
        if (cloudUtil == null) {
            cloudUtil = CloudUtil.getInstance();
        }
        return cloudUtil;
    }

    @Override
    public final List<SchedulingTask> apply() {
        if (parent != null) {
            parent.take++;
        }
        return applyIntern();
    }

    protected List<SchedulingTask> applyIntern() {
        List<SchedulingTask> scheduledTasks = getScheduledTasks();
        algorithm.schedulePathOnInstance(scheduledTasks, getInstance(), localTaskStarttimes);
        return scheduledTasks;
    }

    @Override
    public final Map<SchedulingTask, Double> getUmodLocalTaskStarttimes() {
        return performanceMode ? localTaskStarttimes : Collections.unmodifiableMap(localTaskStarttimes);
    }

    @Override
    protected abstract StrategyResult clone();

    protected final StrategyResult fill(AbstractStrategy clone) {
        clone.setInstance(getInstance());
        clone.pcp = pcp;
        clone.setStarttime(getStartTime());
        clone.setEndtime(getEndTime());
        clone.setCosts(getCosts());
        clone.localTaskStarttimes.putAll(localTaskStarttimes);
        clone.parent = this;
        return clone;
    }

    protected final CriticalPathAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public final int getLoopCount() {
        return loopCount;
    }

    @Override
    public final double getCosts() {
        return costs;
    }

    @Override
    public final int getTaken() {
        return take;
    }

    @Override
    public final int getAccepted() {
        return accept;
    }

    @Override
    public final double getEndTime() {
        return endtime;
    }

    @Override
    public final BasicInstance<SchedulingTask> getInstance() {
        return instance;
    }

    @Override
    public abstract String getMessage();

    @Override
    public abstract String getName();

    protected final List<SchedulingTask> getPcp() {
        return pcp;
    }

    @Override
    public final double getStartTime() {
        return starttime;
    }

    protected final void reset(BasicInstance<SchedulingTask> instance, List<SchedulingTask> pcp) {
        this.pcp = pcp;
        this.setInstance(instance);
        starttime = -1;
        endtime = -1;
        localTaskStarttimes.clear();
        costs = -1;
    }

    protected final void setCosts(double costs) {
        this.costs = costs;
    }

    protected final void setEndtime(double endtime) {
        this.endtime = endtime;
    }

    protected final void setStarttime(double starttime) {
        this.starttime = starttime;
    }

    protected final double tryForward(List<SchedulingTask> pcp, double taskTime, InstanceSize instanceSize, double deadline) {
        for (SchedulingTask ti : pcp) {
            taskTime = Double.max(taskTime, ti.getStartTime());
            localTaskStarttimes.put(ti, taskTime);
            if (taskTime < getStartTime()) {
                setStarttime(taskTime);
            }

            taskTime += ti.getExecutionTime(instanceSize);
            if (taskTime > ti.getLatestEndTime()) {
                taskTime = -1;
                break;
            }
            if (taskTime > deadline) {
                taskTime = -1;
                break;
            }
            if (!checkScheduledChildren(ti, taskTime, pcp)) {
                taskTime = -1;
                break;
            }

            if (taskTime > getEndTime()) {
                setEndtime(taskTime);
            }
            ++loopCount;
        } // for tasks in path

        return taskTime;
    }

    private boolean checkScheduledChildren(SchedulingTask ti, double taskTime, Collection<SchedulingTask> pcp) {
        Collection<SchedulingTask> children = ti.getChildren();
        for (SchedulingTask child : children) {
            if (child.getLane() != null && child != getAlgorithm().getWorkflow().getExit()) {
                if (child.getLane() != getInstance() || (child.getLane() == getInstance() && !pcp.contains(child))) {

                    if (taskTime - child.getStartTime() > Util.DOUBLE_THRESHOLD) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public final StrategyResult tryStrategy(BasicInstance<SchedulingTask> instance, List<SchedulingTask> pcp) {
        reset(instance, pcp);
        StrategyResult result = internalTryStrategy();
        if (result != null) {
            accept++;
        }
        return result;
    }

    protected abstract StrategyResult internalTryStrategy();

    @Override
    public String toString() {
        return getMessage();

    }

    protected final double fullSpeadAhead(List<SchedulingTask> pcp, double taskTime, InstanceSize instanceSize, Collection<SchedulingTask> tasksOnInstance) {
        for (SchedulingTask ti : pcp) {
            taskTime = Double.max(taskTime, ti.getStartTime());

            localTaskStarttimes.put(ti, taskTime);
            if (taskTime < getStartTime()) {
                setStarttime(taskTime);
            }
            taskTime += ti.getExecutionTime(instanceSize);
            if (taskTime > getEndTime()) {
                setEndtime(taskTime);
            }
            ++loopCount;
        } // for tasks in path
        return taskTime;
    }

    protected final double tryForwardShifter(List<SchedulingTask> pcp, double taskTime, InstanceSize instanceSize) {
        for (SchedulingTask ti : pcp) {

            // calc new est
            taskTime = Double.max(taskTime, ti.getStartTime());

            // double newInstanceStart = taskTime;
            localTaskStarttimes.put(ti, taskTime);
            if (taskTime < getStartTime()) {
                setStarttime(taskTime);
            }

            taskTime += ti.getExecutionTime(instanceSize);
            if (taskTime > ti.getLatestEndTime()) {
                taskTime = -1;
                break;
            }

            if (taskTime > getEndTime()) {
                setEndtime(taskTime);
            }
            if (taskTime > getAlgorithm().getWorkflow().getDeadline()) {
                taskTime = -1;
                break;
            }
            ++loopCount;
        } // for tasks in path

        return taskTime;
    }

    protected void setInstance(BasicInstance<SchedulingTask> instance) {
        this.instance = instance;
    }

    @Override
    public abstract int getStartIndex();

}
