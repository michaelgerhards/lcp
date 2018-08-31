package dynamic.algorithm.remapper.my;

import dynamic.scheduling.engine.WorkflowEngineImpl;

class DeadlineIsViolatedAdapter {

	private final WorkflowEngineImpl engine;

	public DeadlineIsViolatedAdapter(WorkflowEngineImpl engine) {
		this.engine = engine;
	}
	
	public void notifyThatDeadlineIsViolated() {
		// maximum scale out / up
	}
	
}
