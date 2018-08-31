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
import statics.initialization.WorkflowInstance;
import statics.util.CloudUtil;
import statics.util.Util;

public class SchedulingInformationImplPerType implements SchedulingInformation {

    private Map<Integer, Map<InstanceSize, Double>> executionTimes;
    private Map<Integer, Map<InstanceSize, Double>> variances;
    
    private final List<InstanceSize> sizes;
    private final double atuLength;

    public SchedulingInformationImplPerType(Cloud cloud, HomogenRuntimeProfilePerType profiles) {
        atuLength = cloud.getAtuLength();
        sizes = cloud.getSizes();
        initExecutionTimeMap(profiles);
    }
    
    public Set<Integer> getTaskTypes() {
        return executionTimes.keySet();
    }

    @Override
    public Map<InstanceSize, Double> getExecutionTimes(DependencyTask task) {
        Map<InstanceSize, Double> ets = executionTimes.get(task.getType());
        return ets;
    }
    
    public Map<InstanceSize, Double> getExecutionTimes(int taskType) {
        return executionTimes.get(taskType);
    }

    @Override
    public List<InstanceSize> getSizes() {
        return sizes;
    }

    @Override
    public double getAtuLength() {
        return atuLength;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        List<Integer> sortedTaskNames = new ArrayList<>(executionTimes.keySet());
        Collections.sort(sortedTaskNames);

        for (int type : sortedTaskNames) {
            String typeName = WorkflowInstance.intToJobName(type);
            buffer.append(String.format("t %25s et", typeName));
            Map<InstanceSize, Double> ets = executionTimes.get(type);
            List<InstanceSize> sortedSizes = new ArrayList<>(ets.keySet());
            Collections.sort(sortedSizes);
            for (InstanceSize size : sortedSizes) {
                Double et = ets.get(size);
                buffer.append(String.format("\t%15.5f", et));
            }
            buffer.append("\tvar\t");
            Map<InstanceSize, Double> vars = variances.get(type);
            // for (InstanceSize size : vars.keySet()) {

            for (InstanceSize size : sortedSizes) {
                Double var = vars.get(size);
                buffer.append(String.format("\t%30.5f", var));
            }
            buffer.append(String.format("%n"));
        }
        String result = buffer.toString();
        return result;
    }

    private void initExecutionTimeMap(HomogenRuntimeProfilePerType profiles) {
        executionTimes = new HashMap<>();
        variances = new HashMap<>();
        initArtificialTasks();

        CloudUtil cUtil = new CloudUtil(sizes);
        InstanceSize fastest = cUtil.getFastestSize();

        for (int taskType : profiles.getTaskTypes()) {
            if (taskType == DependencyGraph.ENTRY_ID || taskType == DependencyGraph.EXIT_ID) {
                continue;
            }

            Map<InstanceSize, Double> ets = new HashMap<>();
            Map<InstanceSize, Double> var = new HashMap<>();
            executionTimes.put(taskType, ets);
            variances.put(taskType, var);

            final double fastestEt = profiles.getExecutionTime(taskType);
            final double lowestV = profiles.getVariance(taskType);
            for (InstanceSize instanceSize : sizes) {
                double slowDown = fastest.getSpeedup() / instanceSize.getSpeedup();
                double et = fastestEt * slowDown;
                double v;

                if (lowestV == 0) {
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
        for (InstanceSize size : sizes) {
            if (size.isDummy()) {
                zeros.put(size, 0.0);
            } else {
                zeros.put(size, Double.POSITIVE_INFINITY);
            }
        }

        executionTimes.put(DependencyGraph.ENTRY_ID, zeros);
        executionTimes.put(DependencyGraph.EXIT_ID, zeros);
        variances.put(DependencyGraph.ENTRY_ID, zeros);
        variances.put(DependencyGraph.EXIT_ID, zeros);
    }

    @Override
    public Map<InstanceSize, Double> getVariances(DependencyTask task) {
        return variances.get(task.getType());
    }
}
