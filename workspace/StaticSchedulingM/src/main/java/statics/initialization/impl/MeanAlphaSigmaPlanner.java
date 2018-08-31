package statics.initialization.impl;

import cloud.InstanceSize;
import statics.initialization.Planner;
import statics.initialization.SchedulingTask;

public class MeanAlphaSigmaPlanner implements Planner {

    private static final long serialVersionUID = 3259664428939087946L;

    private final double alpha;

    public MeanAlphaSigmaPlanner(double alpha) {
        if (alpha < 0) {
            throw new RuntimeException();
        }
        this.alpha = alpha;
    }

    @Override
    public double getPlannedExecutionTime(SchedulingTask task, InstanceSize size) {
        double mean = task.getMeanExecutionTime(size);
        double var = task.getVariance(size);
        double sigma = Math.sqrt(var);
        double et;
        if (sigma != Double.POSITIVE_INFINITY) {
            et = mean + alpha * sigma;
        } else {
            et = Double.POSITIVE_INFINITY;
        }
        return et;
    }

}
