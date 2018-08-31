package statics.initialization.impl.scaler;

import executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs.Job;

public class DefaultProfileScaler implements ProfileScaler {

	@Override
	public double getExecutionTime(Job job, double value) {
		return value;
	}

	@Override
	public double getVarTime(Job job, double value) {
		return value;
	}


	
}
