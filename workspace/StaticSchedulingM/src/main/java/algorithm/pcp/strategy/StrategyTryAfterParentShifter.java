package algorithm.pcp.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import algorithm.Visualizer;
import algorithm.pcp.CriticalPathAlgorithm;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.initialization.WorkflowInstance;
import statics.util.BillingUtil;

public class StrategyTryAfterParentShifter extends AbstractStrategy {

    private List<SchedulingTask> newPcp = new ArrayList<>();
    private int index;

    public StrategyTryAfterParentShifter(CriticalPathAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    protected StrategyResult clone() {
        StrategyTryAfterParentShifter clone = new StrategyTryAfterParentShifter(getAlgorithm());
        clone.index = index;
        clone.newPcp.addAll(this.newPcp);
        return fill(clone);
    }

    @Override
    public String getMessage() {
        return "select existing instance " + getInstance() + " for tasks " + Visualizer.formatList(getScheduledTasks()) + "\nvia" + getName();
    }

    public static final String NAME = "Try After Parent";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public StrategyResult internalTryStrategy() {
        SchedulingTask firstTask = getPcp().get(0);
        Collection<SchedulingTask> parentsOfFirstTask = firstTask.getParents();
        int indexOfLastParentInPath = getLastParentInPath(parentsOfFirstTask, getInstance().getUmodTasks());
        if (indexOfLastParentInPath < 0) {
            return null;
        }
        SchedulingTask lastParentInPath = getInstance().getUmodTasks().get(indexOfLastParentInPath);
//        int indexOfLastParentInPath = getInstance().getUmodTasks().indexOf(lastParentInPath);
        if (indexOfLastParentInPath == getInstance().getUmodTasks().size() - 1) {
            // same result as "try after end"
            return null;
        }
        SchedulingTask next = getInstance().getUmodTasks().get(indexOfLastParentInPath + 1);
        if (next.getStatus() == TaskStatus.COMPLETED || next.getStatus() == TaskStatus.RUNNING) {
            return null;
        }

        double taskTime = lastParentInPath.getEndTime();

        if (Time.getInstance().getActualTime() > taskTime) {
            taskTime = Time.getInstance().getActualTime();
        }

        if (taskTime > firstTask.getLatestEndTime()) {
            return null;
        }
        List<SchedulingTask> subList = getInstance().getUmodTasks().subList(indexOfLastParentInPath + 1, getInstance().getUmodTasks().size());
        newPcp = new ArrayList<>(getPcp().size() + subList.size() + 1);
        newPcp.addAll(getPcp());
        newPcp.addAll(subList);
        index = indexOfLastParentInPath + 1;
        this.setEndtime(getInstance().getEndTime());

        double starttime = getInstance().getUmodTasks().get(0).getStartTime();
        this.setStarttime(starttime);

        taskTime = tryForward(newPcp, taskTime, getInstance().getInstanceSize(), getAlgorithm().getWorkflow().getDeadline());
        if (taskTime < 0) {
            return null;
        }

        BillingUtil bu = BillingUtil.getInstance();
        int atuDiff = bu.getAtuDiff(getInstance(), this);
        double costs = bu.getCosts(atuDiff, getInstance().getInstanceSize());

        this.setCosts(costs);

        return clone();
    }

    private int getLastParentInPath(Collection<SchedulingTask> parentsOfFirstTask, List<SchedulingTask> tasksOnResource) {
        for (int i = tasksOnResource.size() - 1; i >= 0; --i) {
            SchedulingTask task = tasksOnResource.get(i);
            if (parentsOfFirstTask.contains(task)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public final List<SchedulingTask> getScheduledTasks() {
        List<SchedulingTask> result = newPcp;
//        List<SchedulingTask> result = new ArrayList<>(newPcp);
        return WorkflowInstance.performanceMode ? result : Collections.unmodifiableList(result);
    }

    @Override
    public int getStartIndex() {
        return index;
    }

}
