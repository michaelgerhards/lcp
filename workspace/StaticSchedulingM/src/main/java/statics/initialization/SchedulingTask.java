package statics.initialization;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cloud.Instance;
import cloud.InstanceSize;
import reality.RealResourceManager;
import statics.initialization.impl.Lane;
import statics.util.Duration;

public interface SchedulingTask extends Comparable<SchedulingTask>, Duration {

    double calcLft();

    List<SchedulingTask> calcLongestPathToExit();

    @Override
    default int compareTo(SchedulingTask o) {
        return getId() - o.getId();
    }

    Set<SchedulingTask> getChildren();

    Set<SchedulingTask> getAnchestors();

    Set<SchedulingTask> getDescendants();

    double getCurrentExecutionTime();

    double getDelayBuffer();

    double getEarliestStartTimeFix();

    double getEarliestStartTimeFixConsideringLanePredecessor();

    double getExecutionTime(InstanceSize size);

    int getId();

    double getIDLfix();

    double getIDLfix(Collection<SchedulingTask> notLimitingTasks);

    double getIDLflex();

    Lane getLane();

    double getMeanExecutionTime(InstanceSize size);

    Set<SchedulingTask> getNextTasks();

    Set<SchedulingTask> getParents();

    double getPredictedEndtime();

    Set<SchedulingTask> getPrevTasks();

    Instance getResource();

    TaskStatus getStatus();

    double getSWfix(Collection<SchedulingTask> notLimitingTasks);

    double getSWflex(Collection<Lane> statics);

    int getType();

    double getVariance(InstanceSize size);

    WorkflowInstance getWorkflow();

    Set<SchedulingTask> goCompleted();

    void goRunning();

    void readyEntry(SchedTaskManager taskManager, RealResourceManager resourceManager);

    boolean satisfyRSWfix(Collection<SchedulingTask> notLimitingTasks, double requiredSW);

    void setEndTime(double endTime);

    void setPredictedEndtime(double predictedEndtime);

    void setResource(Instance resource);

    void setStartTime(double startTime);

    double updateDelayBuffer();

    void setDelayBuffer(double buffer);

    void setLatestEndTime(double latestEndTime);

    double getLatestEndTime();

    double getSpareTime();

    void setSpareTime(double spareTime);

    public void reset();

}
