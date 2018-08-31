package statics.initialization.impl.scaler;

import executionprofile.generated.Adag;

/**
 *
 * @author mike
 */
public class DefaultDaxScaler implements DaxScaler {

    @Override
    public double getExecutionTime(Adag.Job job, double value) {
        return value;
    }

    @Override
    public double getVarTime(Adag.Job job, double value) {
        return value;
    }

}
