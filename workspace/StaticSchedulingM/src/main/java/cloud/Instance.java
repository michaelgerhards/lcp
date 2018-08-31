package cloud;

import java.io.Serializable;
import java.util.Set;

import reality.RealResourceManager;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.initialization.impl.SchedulingTaskList;

public interface Instance extends Serializable, BasicInstance<SchedulingTask> {

    // init lanes & Fastest
    void addTaskAtEnd(SchedulingTask task);

    void addTaskAtStart(SchedulingTask task);

//	void catchUp(SchedulingTask endTask);
    double getCost();

    int getTasksCount();

    Set<Lane> getUmodChildren();

    SchedulingTaskList getUmodLastPath();

    Set<Lane> getUmodParents();

    SchedulingTask getNextTaskToSchedule();

    void taskCompletedExecution(SchedulingTask task);

    void taskReadyForExecution(SchedulingTask task);

    void taskStartExecution(SchedulingTask task);

    void setManager(RealResourceManager resourceManager);

    RealResourceManager getManager();

    void goTerminated(double time);

    void updateStatus();

    public void reset();

}
