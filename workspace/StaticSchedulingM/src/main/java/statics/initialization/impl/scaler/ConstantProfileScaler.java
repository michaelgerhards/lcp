package statics.initialization.impl.scaler;

import executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs.Job;

public class ConstantProfileScaler implements ProfileScaler {

    private final double factor;

    public ConstantProfileScaler(double factor) {
        this.factor = factor;
    }

    @Override
    public double getExecutionTime(Job job, double value) {
        double result = value * factor;
        return result;
    }

    @Override
    public double getVarTime(Job job, double value) {
        double result = value * factor;
        return result;
    }

}
