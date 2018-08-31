package statics.initialization.impl.scaler;

/**
 *
 * @author mike
 */
public interface DaxScaler {

    double getExecutionTime(executionprofile.generated.Adag.Job job, double value);

    double getVarTime(executionprofile.generated.Adag.Job job, double value);

}
