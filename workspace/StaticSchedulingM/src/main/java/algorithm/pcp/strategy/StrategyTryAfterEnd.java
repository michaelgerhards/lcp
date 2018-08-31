package algorithm.pcp.strategy;

import java.util.Collections;
import java.util.List;

import algorithm.Visualizer;
import algorithm.pcp.CriticalPathAlgorithm;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.BillingUtil;

public class StrategyTryAfterEnd extends AbstractStrategy {

    public StrategyTryAfterEnd(CriticalPathAlgorithm algorithm) {
        super(algorithm);
    }

    public String getMessage() {
        return "select existing instance " + getInstance() + " for tasks " + Visualizer.formatList(getPcp()) + "\nvia try direct after end";
    }

    @Override
    public StrategyResult internalTryStrategy() {
        int index = getInstance().getUmodTasks().size() - 1;
        double resourceComputationEnd = getInstance().getUmodTasks().get(index).getEndTime();

        double taskTime = Math.max(resourceComputationEnd, getPcp().get(0).getStartTime());

        if (Time.getInstance().getActualTime() > taskTime) {
            taskTime = Time.getInstance().getActualTime();
        }

        BillingUtil bu = BillingUtil.getInstance();
        double billingPeriodEnd = bu.getBillingEndTime(getInstance());
        if (taskTime > billingPeriodEnd) {
            return null;
        }

        double combinedExTime = statics.util.Util.calcExecutionTime(getPcp(), getInstance().getInstanceSize());
        if (taskTime + combinedExTime - getAlgorithm().getWorkflow().getDeadline() > statics.util.Util.DOUBLE_THRESHOLD) {
            return null;
        }

        setStarttime(getInstance().getStartTime());
        setEndtime(getInstance().getEndTime());
        taskTime = tryForward(getPcp(), taskTime, getInstance().getInstanceSize(), getAlgorithm().getWorkflow().getDeadline());

        if (taskTime < 0) {
            return null;
        }

        int atuDiff = bu.getAtuDiff(getInstance(), this);
        double costs = bu.getCosts(atuDiff, getInstance().getInstanceSize());

        this.setCosts(costs);

        return clone();
    }

    public static final String NAME = "Try Direct After End";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected StrategyResult clone() {
        AbstractStrategy clone = new StrategyTryAfterEnd(getAlgorithm());
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
        return getInstance().getUmodTasks().size();
    }

}
