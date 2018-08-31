package algorithm.pcp.strategy;

import java.util.List;
import java.util.Map;

import cloud.BasicInstance;
import statics.initialization.SchedulingTask;
import statics.util.Duration;

public interface StrategyResult extends Duration {

    List<SchedulingTask> getScheduledTasks();

    Map<SchedulingTask, Double> getUmodLocalTaskStarttimes();

    List<SchedulingTask> apply();

    String getMessage();

    BasicInstance<SchedulingTask> getInstance();

    double getCosts();

    String getName();

    int getStartIndex();

}
