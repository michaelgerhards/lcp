package statics.initialization.impl.scaler;

import executionprofile.generated.Adag;

/**
 *
 * @author mike
 */
public class ConstantDaxScaler implements DaxScaler {

    private final double factor;

    public ConstantDaxScaler(double factor) {
        this.factor = factor;
    }

    @Override
    public double getExecutionTime(Adag.Job job, double value) {
        return factor * value;
    }

    @Override
    public double getVarTime(Adag.Job job, double value) {
        return factor * value;
    }

}
