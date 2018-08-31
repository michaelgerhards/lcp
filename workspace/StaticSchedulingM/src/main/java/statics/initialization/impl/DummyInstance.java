package statics.initialization.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import cloud.Instance;
import cloud.InstanceSize;
import cloud.InstanceStatus;
import reality.RealResourceManager;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.initialization.WorkflowInstance;

// TODO modify some methods!!!
class DummyInstance implements Instance {

    private static final long serialVersionUID = -3157778183892086630L;

    private final List<SchedulingTask> tasks = new ArrayList<>();

    private RealResourceManager resourceManager = null;

    private final SchedulingTaskList tasksView;

    private double terminatedTime = 0;

    private InstanceStatus status = InstanceStatus.OFFLINE;

    private int nextTaskToSchedule = 0;

    private final WorkflowInstance workflow;

    private final InstanceSize size;

    DummyInstance(WorkflowInstance workflow, InstanceSize size) {
        this.workflow = workflow;
        this.size = size;
        tasksView = new SchedulingTaskList(tasks, size, workflow);
        
        if (!size.isDummy()) {
            throw new RuntimeException();
        }
    }

    @Override
    public void addTaskAtEnd(SchedulingTask task) {
        // algorithm.getWorkflow().getEntry()

        if (task == workflow.getEntry() || task == workflow.getExit()) {

            // if (task.getType().equals(DependencyGraph.ENTRY_ID)
            // || task.getType().equals(DependencyGraph.EXIT_ID)) {
            tasks.add(task);
            task.setResource(this);
        } else {
            throw new RuntimeException("tried to scheduled task on dummy instance");
        }
    }

    @Override
    public SchedulingTaskList getUmodTasks() {
        return tasksView; // XXX remove?
    }

    @Override
    public InstanceSize getInstanceSize() {
        return size;
    }

    @Override
    public InstanceStatus getStatus() {
        return status;
    }

    @Override
    public double getStartTime() {
        // XXX check with task starttime
        return 0;
    }

    @Override
    public double getEndTime() {
        // XXX check with task endtime
        // return algorithm.getWorkflow().getDeadline();

        return tasks.get(tasks.size() - 1).getEndTime();
    }

    @Override
    public SchedulingTask getNextTaskToSchedule() {
        if (nextTaskToSchedule < tasks.size()) {
            SchedulingTask task = tasks.get(nextTaskToSchedule);
            return task;
        } else {
            return null;
        }
    }

    @Override
    public void taskCompletedExecution(SchedulingTask task) {
        if (task != tasks.get(nextTaskToSchedule)) {
            throw new RuntimeException();
        }
        nextTaskToSchedule++;
        goIdling();
        SchedulingTask schedulingTask = getNextTaskToSchedule();
        if (schedulingTask == null) {
            // goTerminated();
        } else if (schedulingTask.getStatus() == TaskStatus.READY) {
            goReady();
        }

    }

    @Override
    public void taskReadyForExecution(SchedulingTask task) {
        SchedulingTask nextTask = getNextTaskToSchedule();
        if (status == InstanceStatus.OFFLINE && nextTask == task) {
            goReadyToStart();
        }
        if (status == InstanceStatus.IDLING && nextTask == task) {
            goReady();
        }

    }

    @Override
    public void taskStartExecution(SchedulingTask task) {
        SchedulingTask nextTask = getNextTaskToSchedule();
        if (nextTask != task) {
            throw new RuntimeException();
        }
        goRunning();
    }

    @Override
    public void setManager(RealResourceManager resourceManager) {
        if (this.resourceManager != null) {
            throw new RuntimeException();
        }
        this.resourceManager = resourceManager;
    }

    @Override
    public RealResourceManager getManager() {
        return resourceManager;
    }

    private void goIdling() {
        if (getStatus() != InstanceStatus.RUNNING) {
            throw new RuntimeException();
        }
        status = InstanceStatus.IDLING;
        resourceManager.goIdling(getName());
    }

    private void goRunning() {
        if (getStatus() != InstanceStatus.READY && getStatus() != InstanceStatus.READY_TO_START) {
            throw new RuntimeException();
        }
        status = InstanceStatus.RUNNING;
        resourceManager.goRunning(getName());
    }

    private void goReady() {
        if (getStatus() != InstanceStatus.IDLING) {
            throw new RuntimeException();
        }
        status = InstanceStatus.READY;
        resourceManager.goReady(getName());
    }

    private void goReadyToStart() {
        if (getStatus() != InstanceStatus.OFFLINE) {
            throw new RuntimeException();
        }
        status = InstanceStatus.READY_TO_START;
        resourceManager.activateResource(this);
    }

    @Override
    public void goTerminated(double time) {
        if (getStatus() != InstanceStatus.IDLING) {
            throw new RuntimeException(getStatus().toString());
        }
        status = InstanceStatus.TERMINATED;
        this.terminatedTime = time;
        resourceManager.goTerminate(getName(), time);
    }

    // @Override
    // public void catchUp(SchedulingTask endTask) {
    // throw new UnsupportedOperationException();
    // }
    // @Override
    // public Lane extractLane(List<SchedulingTask> lastPath) {
    // throw new UnsupportedOperationException();
    // }
    @Override
    public double getCost() {
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTasksCount() {
        return tasks.size();
    }

    @Override
    public SortedSet<Lane> getUmodChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SchedulingTaskList getUmodLastPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet<Lane> getUmodParents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return "DUMMY";
    }

    @Override
    public double getTerminatedTime() {
        if (getStatus() != InstanceStatus.TERMINATED) {
            throw new RuntimeException();
        }
        return terminatedTime;
    }

    @Override
    public void updateStatus() {
        SchedulingTask nextTask = getNextTaskToSchedule();
        if (nextTask != null && nextTask.getStatus() == TaskStatus.READY) {
            if (getStatus() == InstanceStatus.OFFLINE) {
                goReadyToStart();
            }
            if (getStatus() == InstanceStatus.IDLING) {
                goReady();
            }
        }
    }

    @Override
    public WorkflowInstance getWorkflow() {
        return workflow;
    }

    @Override
    public void addTaskAtStart(SchedulingTask task) {
        throw new UnsupportedOperationException("addTaskAtStart Not supported yet.");
    }

    @Override
    public void reset() {
        resourceManager = null;
        terminatedTime = 0.;
        status = InstanceStatus.OFFLINE;
        nextTaskToSchedule = 0;
    }

}
