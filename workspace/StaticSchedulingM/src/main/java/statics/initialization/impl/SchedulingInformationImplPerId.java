package statics.initialization.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloud.Cloud;
import cloud.InstanceSize;
import java.util.Set;
import statics.initialization.DependencyGraph;
import statics.initialization.DependencyTask;
import statics.initialization.SchedulingInformation;
import statics.util.CloudUtil;
import statics.util.Util;

public class SchedulingInformationImplPerId implements SchedulingInformation {

    private final double atuLength;
    private final List<InstanceSize> sizes;
    private Map<Integer, Map<InstanceSize, Double>> executionTimes;
    private Map<Integer, Map<InstanceSize, Double>> variances;

    public SchedulingInformationImplPerId(Cloud cloud, HomogenRuntimeProfilePerId profiles) {
        atuLength = cloud.getAtuLength();
        sizes = cloud.getSizes();
        initExecutionTimeSortedMap(profiles);
    }

    public Set<Integer> getTaskIds() {
        return executionTimes.keySet();
    }
    
    @Override
    public Map<InstanceSize, Double> getExecutionTimes(DependencyTask task) {
        return executionTimes.get(task.getId());
    }
    
    public Map<InstanceSize, Double> getExecutionTimes(int taskId) {
        return executionTimes.get(taskId);
    }

    @Override
    public List<InstanceSize> getSizes() {
        return sizes;
    }

    @Override
    public double getAtuLength() {
        return atuLength;
    }

    private void initExecutionTimeSortedMap(HomogenRuntimeProfilePerId profiles) {
        executionTimes = new HashMap<>();
        variances = new HashMap<>();
        initArtificialTasks();

        CloudUtil cUtil = new CloudUtil(sizes);
        InstanceSize fastest = cUtil.getFastestSize();

        for (int id : profiles.getIds()) {
            if (id == DependencyGraph.ENTRY_ID || id == DependencyGraph.EXIT_ID) {
                continue;
            }

            Map<InstanceSize, Double> ets = new HashMap<>();
            Map<InstanceSize, Double> var = new HashMap<>();
            executionTimes.put(id, ets);
            variances.put(id, var);

            final double fastestEt = profiles.getExecutionTime(id);
            final double lowestV = profiles.getVariance(id);

            for (InstanceSize instanceSize : sizes) {
                double slowDown = fastest.getSpeedup() / instanceSize.getSpeedup();
                double et = fastestEt * slowDown;
                double v;
                if (lowestV == 0.) {
                    v = 0;
                } else {
                    v = lowestV * slowDown * slowDown;
                }

                if (et < Util.MIN_EX_TIME) {
                    et = Util.MIN_EX_TIME;
                }
                et = Util.round2Digits(et);
                ets.put(instanceSize, et);
                v = Util.round2Digits(v);
                var.put(instanceSize, v);
            }
        }
    }

    private void initArtificialTasks() {
        Map<InstanceSize, Double> zeros = new HashMap<>();

        sizes.stream().forEach((size) -> {
            if (size.isDummy()) {
                zeros.put(size, 0.0);
            } else {
                zeros.put(size, Double.POSITIVE_INFINITY);
            }
        });

        executionTimes.put(DependencyGraph.ENTRY_ID, zeros);
        executionTimes.put(DependencyGraph.EXIT_ID, zeros);
        variances.put(DependencyGraph.ENTRY_ID, zeros);
        variances.put(DependencyGraph.EXIT_ID, zeros);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getClass()).append(String.format("%n"));
        List<Integer> sortedTaskNames = new ArrayList<>(executionTimes.keySet());
        Collections.sort(sortedTaskNames);

        for (int id : sortedTaskNames) {
            buffer.append(String.format("t %25s ex", id));
            Map<InstanceSize, Double> ets = executionTimes.get(id);

            List<InstanceSize> sortedSizes = new ArrayList<>(ets.keySet());
            Collections.sort(sortedSizes);
            sortedSizes.stream().map((size) -> ets.get(size)).forEach((et) -> {
                buffer.append(String.format("\t%15.5f", et));
            });
            buffer.append("\tvar\t");
            Map<InstanceSize, Double> vars = variances.get(id);
            // for (InstanceSize size : vars.keySet()) {
            sortedSizes.stream().map((size) -> vars.get(size)).forEach((var) -> {
                buffer.append(String.format("\t%30.5f", var));
            });
            buffer.append(String.format("%n"));
        }
        String result = buffer.toString();
        return result;
    }

    @Override
    public Map<InstanceSize, Double> getVariances(DependencyTask task) {
        return variances.get(task.getId());
    }

}
