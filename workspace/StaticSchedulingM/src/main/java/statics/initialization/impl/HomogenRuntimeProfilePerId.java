package statics.initialization.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class HomogenRuntimeProfilePerId implements Serializable {

    private static final long serialVersionUID = 5387768987157323724L;
    private final Map<Integer, Double> executionTimesView;
    private final Map<Integer, Double> variances;

    /**
     * String = id, Double = executionTime
     *
     * @param executionTimes
     * @param variances
     */
    public HomogenRuntimeProfilePerId(Map<Integer, Double> executionTimes, Map<Integer, Double> variances) {
        this.executionTimesView = executionTimes;
        this.variances = variances;
    }

    public double getExecutionTime(Integer id) {
        return executionTimesView.get(id);
    }

    public boolean containsId(Integer taskType) {
        return executionTimesView.containsKey(taskType);
    }

    public Set<Integer> getIds() {
        return executionTimesView.keySet();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        executionTimesView.keySet().stream().forEach((type) -> {
            double exTime = executionTimesView.get(type);
            builder.append(String.format("%20s\t%15.5f%n", type, exTime));
        });
        String result = builder.toString();
        return result;
    }

    public double getVariance(int id) {
        return variances.get(id);
    }

}
