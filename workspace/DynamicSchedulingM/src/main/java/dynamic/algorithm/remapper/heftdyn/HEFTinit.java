package dynamic.algorithm.remapper.heftdyn;

import algorithm.StaticSchedulingAlgorithm;
import statics.initialization.WorkflowInstance;

public class HEFTinit implements StaticSchedulingAlgorithm {

	private WorkflowInstance workflow;

	@Override
	public String getAlgorithmName() {
		return "HEFT Dynamic Initializer";
	}

	@Override
	public String getAlgorithmNameAbbreviation() {
		return "HEFTdyn";
	}

	@Override
	public WorkflowInstance schedule(WorkflowInstance workflow) {
		this.workflow = workflow;
		workflow.setAlgorithmName(getAlgorithmName());
		return workflow;
	}

	@Override
	public WorkflowInstance getWorkflow() {
		return workflow;
	}

}
