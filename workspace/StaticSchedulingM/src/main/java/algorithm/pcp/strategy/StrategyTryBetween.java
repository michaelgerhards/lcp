package algorithm.pcp.strategy;

import java.util.ArrayList;
import java.util.List;

import algorithm.Visualizer;
import algorithm.pcp.CriticalPathAlgorithm;
import cloud.InstanceSize;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.util.BillingUtil;
import statics.util.Util;

public class StrategyTryBetween extends AbstractStrategy {

//    private List<SchedulingTask> newPcp = new ArrayList<>();
    private int index;

    public StrategyTryBetween(CriticalPathAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    protected StrategyResult clone() {
        StrategyTryBetween clone = new StrategyTryBetween(getAlgorithm());
        clone.index = index;
//        clone.newPcp.addAll(this.newPcp);
        return fill(clone);
    }

    @Override
    public String getMessage() {
        return "select existing instance " + getInstance() + " for tasks " + Visualizer.formatList(getScheduledTasks()) + "\nvia" + getName();
    }

    public static final String NAME = "Try between";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public StrategyResult internalTryStrategy() {
        final double executionTime = Util.calcExecutionTime(getPcp(), getInstance().getInstanceSize());

        SchedulingTask first = getPcp().get(0);
        SchedulingTask last = getPcp().get(getPcp().size() - 1);
        if (first.getStartTime() - getInstance().getEndTime() > Util.DOUBLE_THRESHOLD) {
            return null; // pcp starts after resource end
        } else if (getInstance().getStartTime() - last.getLatestEndTime() > Util.DOUBLE_THRESHOLD) {
            return null; // lane starts after pcp ends.
        }

        // search free maximal free space
        List<Integer> indexOfFirstSpaceTask = new ArrayList<>();

        for (int i = 0; i < getInstance().getUmodTasks().size() - 1; ++i) {
            SchedulingTask taskPred = getInstance().getUmodTasks().get(i);
            SchedulingTask taskSucc = getInstance().getUmodTasks().get(i + 1);
            if (taskSucc.getStatus() == TaskStatus.COMPLETED || taskSucc.getStatus() == TaskStatus.RUNNING) {
                continue;
            }

            double space = taskSucc.getStartTime() - taskPred.getEndTime();

            if (space - executionTime > -statics.util.Util.DOUBLE_THRESHOLD) {
                // large enough
                indexOfFirstSpaceTask.add(i);
            }

        }

        if (indexOfFirstSpaceTask.isEmpty()) {
            return null;
        }

        final SchedulingTask firstTask = getPcp().get(0);

        // TODO possible optimiziation: sort indexOfFirstSpaceTask
        for (int i : indexOfFirstSpaceTask) {
            reset(getInstance(), getPcp());
            double taskTime = getInstance().getUmodTasks().get(i).getEndTime();
            if (Time.getInstance().getActualTime() > taskTime) {
                taskTime = Time.getInstance().getActualTime();
            }

            if (taskTime > firstTask.getLatestEndTime() - firstTask.getExecutionTime(getInstance().getInstanceSize())) {
                continue;
            }

            this.index = i + 1;

            this.setEndtime(getInstance().getEndTime());

            this.setStarttime(getPcp().get(0).getStartTime());

            SchedulingTask oldStart = getInstance().getUmodTasks().get(i + 1);

            taskTime = tryForward(getPcp(), taskTime, getInstance().getInstanceSize(), oldStart.getStartTime());
            if (taskTime < 0) {
                continue;
            }
            BillingUtil bu = BillingUtil.getInstance();
            int atuDiff = bu.getAtuDiff(getInstance(), this);
            double costs = bu.getCosts(atuDiff, getInstance().getInstanceSize());

            this.setCosts(costs);
            return clone();
        }
        return null;
    }

    @Override
    public int getStartIndex() {
        return index;
    }

    @Override
    public List<SchedulingTask> getScheduledTasks() {
        return getPcp();
    }

}
