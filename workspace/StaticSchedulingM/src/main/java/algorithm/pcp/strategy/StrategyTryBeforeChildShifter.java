package algorithm.pcp.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import algorithm.Visualizer;
import algorithm.pcp.CriticalPathAlgorithm;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.BillingUtil;

public class StrategyTryBeforeChildShifter extends AbstractStrategy {

    private List<SchedulingTask> newPcp = new ArrayList<>();
    private int index;

    public StrategyTryBeforeChildShifter(CriticalPathAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    public String getMessage() {
        return "select existing instance " + getInstance() + " for tasks " + Visualizer.formatList(getScheduledTasks()) + "\nvia try before child";
    }

    @Override
    public StrategyResult internalTryStrategy() {

        SchedulingTask lastTask = getPcp().get(getPcp().size() - 1);
        Collection<SchedulingTask> childrenOfLastTask = lastTask.getChildren();
        int indexOfFirstChildInPath = getIndexOfFirstchildInPath(childrenOfLastTask, getInstance().getUmodTasks());
        if (indexOfFirstChildInPath < 0) {
            return null;
        }

        int indexPredInPath = indexOfFirstChildInPath - 1;
        if (indexPredInPath < 0) {
            return null;
        }
        double taskTime = getInstance().getUmodTasks().get(indexPredInPath).getEndTime();

        if (Time.getInstance().getActualTime() > taskTime) {
            taskTime = Time.getInstance().getActualTime();
        }
        if (taskTime < getPcp().get(0).getStartTime()) {
            return null;
        }

        List<SchedulingTask> subList = getInstance().getUmodTasks().subList(indexOfFirstChildInPath, getInstance().getUmodTasks().size());
        newPcp = new ArrayList<>(getPcp().size() + subList.size() + 1);
        newPcp.addAll(getPcp());
        newPcp.addAll(subList);
        index = indexOfFirstChildInPath;
        this.setStarttime(getInstance().getStartTime());
        this.setEndtime(getInstance().getEndTime());
        double starttime = getInstance().getUmodTasks().get(indexPredInPath).getStartTime();
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

    public static final String NAME = "Try Before Child";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected StrategyResult clone() {
        StrategyTryBeforeChildShifter clone = new StrategyTryBeforeChildShifter(getAlgorithm());
        clone.index = index;
        clone.newPcp.addAll(this.newPcp);
        return fill(clone);
    }

    private int getIndexOfFirstchildInPath(Collection<SchedulingTask> childrenOfLastTask, List<SchedulingTask> tasksOnResource) {
        int i = 0;
        for (SchedulingTask firstTaskOnResource : tasksOnResource) {
            if (childrenOfLastTask.contains(firstTaskOnResource)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    @Override
    public final List<SchedulingTask> getScheduledTasks() {
//        List<SchedulingTask> result = new ArrayList<>(newPcp);
        List<SchedulingTask> result = newPcp;
        return WorkflowInstance.performanceMode ? result : Collections.unmodifiableList(result);
    }

    @Override
    public int getStartIndex() {
        return index;
    }

}
