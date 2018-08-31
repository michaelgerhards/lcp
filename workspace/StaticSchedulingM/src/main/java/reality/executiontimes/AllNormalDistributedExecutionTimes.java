package reality.executiontimes;

import cloud.InstanceSize;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import statics.initialization.DependencyGraph;
import statics.initialization.SchedulingTask;
import statics.util.Util;

public final class AllNormalDistributedExecutionTimes implements ExecutionTimes {

    private final Random RANDOM;

    private final Map<Integer, Map<InstanceSize, Double>> executionTimes = new HashMap<>();

    public AllNormalDistributedExecutionTimes(Collection<SchedulingTask> tasks, Collection<InstanceSize> sizes, Long seed) {
        if (seed != null) {
            RANDOM = new Random(seed);
        } else {
            RANDOM = null;
        }
        List<SchedulingTask> tasksSorted = new ArrayList<>(tasks);
        Collections.sort(tasksSorted);
        List<InstanceSize> sizesSorted = new ArrayList<>(sizes);
        Collections.sort(sizesSorted);

        for (SchedulingTask task : tasksSorted) {
            Map<InstanceSize, Double> exTimes = new HashMap<>();
            executionTimes.put(task.getId(), exTimes);
            for (InstanceSize size : sizesSorted) {
                double ex = getTime(task, size);
                exTimes.put(size, ex);
            }
        }

    }

    private double nextGaussian(double y, double o) {
        double val = RANDOM == null ? 0. : RANDOM.nextGaussian();
        double result = val * o + y;
        return result;
    }

    private double getTime(SchedulingTask task, InstanceSize size) {
        double sExTime = task.getMeanExecutionTime(size);
        double plannedExTime = task.getExecutionTime(size);
        if (sExTime == Double.POSITIVE_INFINITY) { // artificial size/task
            return sExTime;
        } else if (size.isDummy() && (task.getId() == DependencyGraph.ENTRY_ID || task.getId() == DependencyGraph.EXIT_ID)) {
            return 0; // not effected by Util.Min_Ex_Time
        }

        double sVarTime = task.getVariance(size);
        double o = Math.sqrt(sVarTime);
        double newExTime = nextGaussian(sExTime, o);
        if (newExTime < Util.MIN_EX_TIME) {
            newExTime = Util.MIN_EX_TIME;
        }

        double exTime = Util.round2Digits(newExTime);
        return exTime;
    }

    @Override
    public double getExecutionTime(SchedulingTask task, InstanceSize size) {
        Map<InstanceSize, Double> map = executionTimes.get(task.getId());
        double exTime = map.get(size);
        return exTime;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        List<Integer> sortedTaskIds = new ArrayList<>(executionTimes.keySet());
        Collections.sort(sortedTaskIds);

        for (int taskId : sortedTaskIds) {

            buffer.append(String.format("t %25d et", taskId));
            Map<InstanceSize, Double> ets = executionTimes.get(taskId);
            List<InstanceSize> sortedSizes = new ArrayList<>(ets.keySet());
            Collections.sort(sortedSizes);
            for (InstanceSize size : sortedSizes) {
                Double et = ets.get(size);
                buffer.append(String.format("\t%15.5f", et));
            }
            buffer.append(String.format("%n"));
        }
        String result = buffer.toString();
        return result;
    }

}
