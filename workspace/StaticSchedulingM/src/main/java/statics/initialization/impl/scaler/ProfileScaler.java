package statics.initialization.impl.scaler;

import executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs;

public interface ProfileScaler {

    double getExecutionTime(Jobs.Job job, double value);

    double getVarTime(Jobs.Job job, double value);

}
