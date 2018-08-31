package algorithm.pcp.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import algorithm.Visualizer;
import algorithm.pcp.CriticalPathAlgorithm;
import cloud.Instance;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.BillingUtil;

public class StrategyTryNew extends AbstractStrategy {

    public static final String NAME = "Try New";
    
    public StrategyTryNew(CriticalPathAlgorithm algorithm) {
        super(algorithm);
    }

    private Instance l;

    @Override
    public List<SchedulingTask> applyIntern() {
        l = getAlgorithm().getWorkflow().instantiate(getInstance().getInstanceSize());
        setInstance(l);
        List<SchedulingTask> scheduledTasks = getScheduledTasks();
        getAlgorithm().schedulePathOnInstance(scheduledTasks, l, getUmodLocalTaskStarttimes());

        return scheduledTasks;
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
        taskTime = tryForward(getPcp(), taskTime, getInstance().getInstanceSize(), getAlgorithm().getWorkflow().getDeadline());

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
        StrategyTryNew clone = new StrategyTryNew(getAlgorithm());
        clone.l = l;
        return fill(clone);
    }

    @Override
    public final List<SchedulingTask> getScheduledTasks() {
        List<SchedulingTask> result = getPcp();
//        List<SchedulingTask> result = new ArrayList<SchedulingTask>(getPcp().size());
//        result.addAll(getPcp());
        return WorkflowInstance.performanceMode ? result : Collections.unmodifiableList(result);
    }

    @Override
    public int getStartIndex() {
        return 0;
    }

}
