package statics.initialization.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import statics.initialization.WorkflowInstance;

public class HomogenRuntimeProfilePerType implements Serializable {

    private static final long serialVersionUID = 5387768987157323724L;
    private final Map<Integer, Double> executionTimesView;
    private final Map<Integer, Double> variances;

    /**
     * int = taskType, Double = executionTime
     *
     * @param executionTimes
     * @param variances
     *
     */
    public HomogenRuntimeProfilePerType(Map<Integer, Double> executionTimes, Map<Integer, Double> variances) {
        this.executionTimesView = executionTimes;
        this.variances = variances;
    }

    public double getExecutionTime(int taskType) {
        return executionTimesView.get(taskType);
    }

    public boolean containsTaskType(int taskType) {
        return executionTimesView.containsKey(taskType);
    }

    public Set<Integer> getTaskTypes() {
        return executionTimesView.keySet();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        executionTimesView.entrySet().stream().forEach((e) -> {
            String typeName = WorkflowInstance.intToJobName(e.getKey());
            double exTime = e.getValue();
            builder.append(String.format("%20s\t%15.5f%n", typeName, exTime));
        });
        String result = builder.toString();
        return result;
    }

    public double getVariance(int taskType) {
        return variances.get(taskType);
    }

}
