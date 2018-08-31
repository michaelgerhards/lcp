package algorithm.pcp;

import java.util.List;

import cloud.BasicInstance;
import cloud.InstanceSize;
import cloud.InstanceStatus;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;

class TestInstance implements BasicInstance<SchedulingTask> {

    private final InstanceSize instanceSize;
    private final WorkflowInstance workflow;

    public TestInstance(InstanceSize instanceSize, WorkflowInstance workflow) {
        this.instanceSize = instanceSize;
        this.workflow = workflow;
    }

    @Override
    public InstanceStatus getStatus() {
        return null;
    }

    @Override
    public double getStartTime() {
        throw new RuntimeException();
    }

    @Override
    public double getEndTime() {
        throw new RuntimeException();
    }

    @Override
    public InstanceSize getInstanceSize() {
        return instanceSize;
    }

    @Override
    public String getName() {
        return "test " + instanceSize.getName();
    }

    @Override
    public List<SchedulingTask> getUmodTasks() {
        throw new RuntimeException();
    }

    @Override
    public double getTerminatedTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WorkflowInstance getWorkflow() {
        return workflow;
    }

}
