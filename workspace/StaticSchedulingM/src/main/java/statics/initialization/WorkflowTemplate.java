package statics.initialization;

public interface WorkflowTemplate {

	WorkflowInstance createWorkflowInstance(double deadline, Planner etPlanner);

	void updatePlanner(WorkflowInstance workflow, Planner p);

}