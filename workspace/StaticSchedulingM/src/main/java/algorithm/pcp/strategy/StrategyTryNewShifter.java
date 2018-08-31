package algorithm.pcp.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import algorithm.AbstractAlgorithm;
import algorithm.Visualizer;
import algorithm.pcp.CriticalPathAlgorithm;
import cloud.Instance;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.BillingUtil;
import statics.util.Util;

public class StrategyTryNewShifter extends AbstractStrategy {

    public static final String NAME = "Try New Shifter";
    
    private Instance l;

    public StrategyTryNewShifter(CriticalPathAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    public List<SchedulingTask> applyIntern() {
        l = getAlgorithm().getWorkflow().instantiate(getInstance().getInstanceSize());
        setInstance(l);
        List<SchedulingTask> scheduledTasks = new ArrayList<>(getScheduledTasks());

        getAlgorithm().schedulePathOnInstance(scheduledTasks, l, getUmodLocalTaskStarttimes());

        Set<SchedulingTask> modified = new HashSet<>();
        for (SchedulingTask task : getPcp()) {
            repairDependencies(task, scheduledTasks, modified);
        }

        for (SchedulingTask t : modified) {
            t.setLatestEndTime(-1.);
        }

        for (SchedulingTask t : modified) {
            AbstractAlgorithm.calcLFT(t);
        }

        return scheduledTasks;
    }

    /**
     *
     * @param ti
     * @param scheduledTasks
     * @param modified
     * @param dependentTasks tasks on same resource and child tasks!
     * @param taskTime
     */
    private void repairDependencies(SchedulingTask ti, List<SchedulingTask> scheduledTasks, Set<SchedulingTask> modified) {
        Collection<SchedulingTask> children = ti.getChildren();
        // repairChildren
        for (SchedulingTask child : children) {
            repairTask(ti, child, scheduledTasks, modified);
        }
        // repair tasks on same resource
        if (ti.getLane() != null) {
            List<SchedulingTask> tasksOnInstance = ti.getLane().getUmodTasks();
            int index = tasksOnInstance.indexOf(ti);
            int nextIndex = index + 1;
            if (nextIndex < tasksOnInstance.size()) {
                SchedulingTask nextTask = tasksOnInstance.get(nextIndex);
                repairTask(ti, nextTask, scheduledTasks, modified);
            }
        }
    }

    private void repairTask(SchedulingTask ti, SchedulingTask dependenTask,
            List<SchedulingTask> scheduledTasks, Set<SchedulingTask> modified) {
        if (dependenTask != getAlgorithm().getWorkflow().getExit()) {
            if (ti.getEndTime() > dependenTask.getStartTime()) {
                double diff = ti.getEndTime() - dependenTask.getStartTime();
                double st = dependenTask.getStartTime();
                double et = dependenTask.getEndTime();
                st += diff;
                et += diff;
                dependenTask.setStartTime(st);
                dependenTask.setEndTime(et);
                scheduledTasks.add(dependenTask);
                modified.add(dependenTask);
                if (getAlgorithm().getWorkflow().getDeadline() - et < -Util.DOUBLE_THRESHOLD) {
                    throw new RuntimeException("StrategyTryNewShift: et= " + et + " pcp= " + getPcp());
                }

                repairDependencies(dependenTask, scheduledTasks, modified);
            }
        }
    }

    @Override
    public String getMessage() {
        return "select new instance " + l + " starttime= " + getStartTime() + " , endtime= " + getEndTime() + " for tasks " + Visualizer.formatList(getPcp());

    }

    @Override
    public StrategyResult internalTryStrategy() {
        l = null;
        double taskTime = getPcp().get(0).getStartTime();
        if (Time.getInstance().getActualTime() > taskTime) {
            taskTime = Time.getInstance().getActualTime();
        }

        this.setStarttime(Double.MAX_VALUE);
        this.setEndtime(Double.MIN_VALUE);
        taskTime = tryForwardShifter(getPcp(), taskTime, getInstance().getInstanceSize());

        // if applicable
        if (taskTime > 0) {
            // if cheapest
            double newDuration = this.getEndTime() - this.getStartTime();
            BillingUtil bu = BillingUtil.getInstance();
            int newAtus = bu.getUsedATUs(newDuration);
            this.setCosts(bu.getCosts(newAtus, getInstance().getInstanceSize()));
            return clone();
        } else {
            return null;
        }

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected StrategyResult clone() {
        StrategyTryNewShifter clone = new StrategyTryNewShifter(getAlgorithm());
        clone.l = l;
        return fill(clone);
    }

    @Override
    public final List<SchedulingTask> getScheduledTasks() {
//        List<SchedulingTask> result = new ArrayList<>(getPcp());
        List<SchedulingTask> result = getPcp();
        return WorkflowInstance.performanceMode ? result : Collections.unmodifiableList(result);
    }

    @Override
    public int getStartIndex() {
        return 0;
    }

}
