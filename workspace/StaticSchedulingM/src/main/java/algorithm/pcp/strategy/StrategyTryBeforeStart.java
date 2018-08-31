package algorithm.pcp.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import algorithm.Visualizer;
import algorithm.pcp.CriticalPathAlgorithm;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.initialization.WorkflowInstance;
import statics.util.BillingUtil;

public class StrategyTryBeforeStart extends AbstractStrategy {

    public StrategyTryBeforeStart(CriticalPathAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    public String getMessage() {
        return "select existing instance " + getInstance() + " for tasks " + Visualizer.formatList(getPcp())
                + "\nvia try direct before start";
    }

    @Override
    public StrategyResult internalTryStrategy() {

        SchedulingTask firstOnInstance = getInstance().getUmodTasks().get(0);
        if (firstOnInstance.getStatus() == TaskStatus.COMPLETED || firstOnInstance.getStatus() == TaskStatus.RUNNING) {
            return null;
        }

        double resourceComputationStart = firstOnInstance.getStartTime();
        SchedulingTask lastTaskOfPCP = getPcp().get(getPcp().size() - 1);
        double taskTime = Math.min(resourceComputationStart, lastTaskOfPCP.getLatestEndTime());
        if (Time.getInstance().getActualTime() > taskTime) {
            taskTime = Time.getInstance().getActualTime();
        }
        BillingUtil bu = BillingUtil.getInstance();
        double billingStartTime = bu.getBillingStartTime(getInstance());
        if (taskTime < billingStartTime) {
            return null;
        }
        this.setStarttime(getInstance().getStartTime());
        this.setEndtime(getInstance().getEndTime());

//        List<SchedulingTask> parentsOnInstance = new ArrayList<>(getPcp().size() + getInstance().getUmodTasks().size() + 2);
//        parentsOnInstance.addAll(getPcp());
//        parentsOnInstance.addAll(getInstance().getUmodTasks());
        // taskTime = tryBackward(getPcp(), taskTime,
        // getInstance().getInstanceSize(), tasksOnInstance);
        taskTime = tryForward(getPcp(), 0, getInstance().getInstanceSize(), resourceComputationStart);
        if (taskTime > 0) {
            double endTimeOfLastTaskOfPCP = getUmodLocalTaskStarttimes().get(lastTaskOfPCP) + lastTaskOfPCP.getExecutionTime(getInstance().getInstanceSize());
            if (endTimeOfLastTaskOfPCP < billingStartTime) {
                return null;
            }

            int atuDiff = bu.getAtuDiff(getInstance(), this);
            double costs = bu.getCosts(atuDiff, getInstance().getInstanceSize());
            this.setCosts(costs);
            return clone();
        } else {
            return null;
        }
    }
    
    public static final String NAME = "try Direct Before Start";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected StrategyResult clone() {
        AbstractStrategy clone = new StrategyTryBeforeStart(getAlgorithm());
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
