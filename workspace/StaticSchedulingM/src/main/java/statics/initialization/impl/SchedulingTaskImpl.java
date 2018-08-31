package statics.initialization.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cloud.Instance;
import cloud.InstanceSize;
import reality.RealResourceManager;
import reality.Time;
import statics.initialization.SchedTaskManager;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.initialization.WorkflowInstance;
import statics.util.Debug;
import statics.util.Util;

class SchedulingTaskImpl implements SchedulingTask, Serializable {
    
    private static final long serialVersionUID = -6565404723742048849L;
    
    private final Map<InstanceSize, Double> ets;
    private final Map<InstanceSize, Double> var;
    private double startTime;
    private double endTime;
    private double latestEndTime;
    private Set<SchedulingTaskImpl> children; // final
    private Set<SchedulingTaskImpl> parents; // final
    private Set<SchedulingTaskImpl> descendants; // final
    private Set<SchedulingTaskImpl> anchestors; // final
    private Set<SchedulingTaskImpl> uncompletedParents; // final
    private final int id;
    private final int type;
    private Instance resource;
    private TaskStatus status = TaskStatus.WAITING;
    private SchedTaskManager taskManager = null;
    private final WorkflowInstanceImpl workflow;
    // private double threshold;
    private double predictedEndtime = -1;
    private double delayBuffer = Util.UNSET;
    private double spareTime = Util.UNSET;
    
    @Override
    public double getSpareTime() {
        return spareTime;
    }
    
    @Override
    public void setSpareTime(double spareTime) {
        this.spareTime = spareTime;
    }
    
    SchedulingTaskImpl(int id, int type, Map<InstanceSize, Double> ets, Map<InstanceSize, Double> var, WorkflowInstanceImpl workflow) {
        this.id = id;
        this.type = type;
        this.ets = ets;
        this.var = var;
        this.workflow = workflow;
        this.startTime = -1;
        this.endTime = -1;
        this.latestEndTime = -1;
    }
    
    void setOnce(Set<SchedulingTaskImpl> children, Set<SchedulingTaskImpl> parents, Set<SchedulingTaskImpl> descendants, Set<SchedulingTaskImpl> anchestors) {
        this.children = children;
        this.parents = parents;
        this.uncompletedParents = new HashSet<>(parents);
        this.descendants = descendants;
        this.anchestors = anchestors;
    }
    
    private double calcEarliestStartTimeFixForCollection(Collection<SchedulingTask> tasks) {
        if (this == workflow.getExit() || this == workflow.getEntry()) {
            return this.getStartTime();
        }
        
        double est = 0.;
        for (SchedulingTask parent : tasks) {
            double pet = parent.getEndTime();
            if (pet > est) {
                est = pet;
                if (getStartTime() - est < -Util.DOUBLE_THRESHOLD) {
                    throw new RuntimeException(this.toString() + " st= " + getStartTime() + " est= " + est);
                } else if (getStartTime() - est < Util.DOUBLE_THRESHOLD) {
                    est = getStartTime();
                    // return est;
                    break;
                }
            }
        }
        return est;
    }
    
    private double calcIDLflex_NoGaps(Collection<Lane> statics) {
        if (Lane.isUseCache() && workflow.getTSDflexCache().containsKey(this) && statics.isEmpty()) {
            return workflow.getTSDflexCache().get(this);
        }
        
        double tIDLflex = Double.MAX_VALUE;
        Collection<SchedulingTask> childrenLocal = this.getChildren();
        Lane laneOfTask = this.getLane();
        for (SchedulingTask child : childrenLocal) {
            Lane laneOfChild = child.getLane();
            if (laneOfChild != laneOfTask) {
                double childStartTime = child.getStartTime();
                double idl;
                if (!statics.contains(laneOfChild) && child != workflow.getExit()) {
                    
                    if (laneOfChild.getUmodChildren().contains(laneOfTask)) {
                        throw new RuntimeException("cyclic dependency between lanes: " + laneOfTask + " and " + laneOfChild);
                    }
                    
                    idl = childStartTime + laneOfChild.getRSWflexConsideringStatic(statics);
                } else {
                    idl = childStartTime;
                }
                if (idl < tIDLflex) {
                    tIDLflex = idl;
                    
                    if (Math.abs(tIDLflex - this.getEndTime()) < Util.DOUBLE_THRESHOLD) {
                        tIDLflex = this.getEndTime();
                        break;
                    }
                }
            }
        }
        
        if (Lane.isUseCache() && statics.isEmpty()) {
            workflow.getTSDflexCache().put(this, tIDLflex);
        }
        
        return tIDLflex;
    }

    // ignore dependencies created by resource
    @Override
    public double calcLft() {
        if (this == workflow.getExit() || this == workflow.getEntry()) {
            return this.getEndTime();
        }
        
        double lft = Double.POSITIVE_INFINITY;
        for (SchedulingTask child : children) {
            double cth = child.calcLft();
            double cst = cth - child.getCurrentExecutionTime();
            if (cst < lft) {
                lft = cst;
                if (lft - getEndTime() < -Util.DOUBLE_THRESHOLD) {
                    throw new RuntimeException(this.toString());
                } else if (lft - getEndTime() < Util.DOUBLE_THRESHOLD) {
                    lft = getEndTime();
                    return lft;
                }
            }
        }
        return lft;
    }

    // consider dependencies created by resource
    @Override
    public List<SchedulingTask> calcLongestPathToExit() {
        List<List<SchedulingTask>> followers = getFollowers(workflow.getExit());
        
        double dist = -1.;
        int index = -1;
        
        for (int i = 0; i < followers.size(); ++i) {
            List<SchedulingTask> path = followers.get(i);
            
            if (path.size() - 2 < 0) {
                if (index == -1) {
                    index = i;
                }
                continue;
            }
            SchedulingTask first = path.get(0);
            SchedulingTask parentOfExit = path.get(path.size() - 2);
            double duration = parentOfExit.getEndTime() - first.getStartTime();
            if (duration > dist) {
                dist = duration;
                index = i;
            }
            
        }
        List<SchedulingTask> result = followers.get(index);
        return result;
    }
    
    @Override
    public Set<SchedulingTask> getChildren() {
        @SuppressWarnings("rawtypes")
        Set c = (Set) children;
        @SuppressWarnings("unchecked")
        Set<SchedulingTask> c2 = (Set<SchedulingTask>) c;
        return c2;
    }
    
    @Override
    public Set<SchedulingTask> getAnchestors() {
        @SuppressWarnings("rawtypes")
        Set c = (Set) anchestors;
        @SuppressWarnings("unchecked")
        Set<SchedulingTask> c2 = (Set<SchedulingTask>) c;
        return c2;
    }
    
    @Override
    public Set<SchedulingTask> getDescendants() {
        @SuppressWarnings("rawtypes")
        Set c = (Set) descendants;
        @SuppressWarnings("unchecked")
        Set<SchedulingTask> c2 = (Set<SchedulingTask>) c;
        return c2;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SchedulingTaskImpl other = (SchedulingTaskImpl) obj;
        return id == other.id;
    }
    
    @Override
    public double getCurrentExecutionTime() {
        if (this == workflow.getExit() || this == workflow.getEntry()) {
            return 0.;
        }
        InstanceSize instanceSize = getLane().getInstanceSize();
        return getExecutionTime(instanceSize);
    }
    
    @Override
    public Set<SchedulingTask> getNextTasks() {
        SchedulingTask nextTask = getLane().getNextTask(this);
        if (nextTask != null && !getChildren().contains(nextTask)) {
            Set<SchedulingTask> result = new HashSet<>(getChildren());
            result.add(nextTask);
            return result;
        } else {
            return getChildren();
        }
    }
    
    @Override
    public double getEarliestStartTimeFix() {
        return calcEarliestStartTimeFixForCollection(getParents());
    }
    
    @Override
    public double getEarliestStartTimeFixConsideringLanePredecessor() {
        return calcEarliestStartTimeFixForCollection(getPrevTasks());
    }
    
    @Override
    public double getEndTime() {
        return endTime;
    }
    
    @Override
    public double getExecutionTime(InstanceSize size) {
        double et = workflow.getEtPlanner().getPlannedExecutionTime(this, size);
        return et;
    }
    
    private List<List<SchedulingTask>> getFollowers(SchedulingTask dest) {
        List<List<SchedulingTask>> result = new ArrayList<>();
        if (this == dest) {
            List<SchedulingTask> list = new LinkedList<>();
            result.add(list);
            return result;
        }
        
        Set<SchedulingTask> dependentTasks = getNextTasks();
        
        for (SchedulingTask child : dependentTasks) {
            List<List<SchedulingTask>> followers = ((SchedulingTaskImpl) child).getFollowers(dest);
            for (List<SchedulingTask> list : followers) {
                list.add(0, child);
                result.add(list);
            }
        }
        
        return result;
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public double getIDLfix() {
        return getIDLfix(Collections.emptyList());
    }
    
    @Override
    public double getIDLfix(Collection<SchedulingTask> notLimitingTasks) {
        if (this == workflow.getExit()) {
            return getEndTime();
        }
        int s = notLimitingTasks.size(); // performance tweak

        double idlfixt = Double.MAX_VALUE;
        for (SchedulingTask child : children) {
            if (s == 0 || !notLimitingTasks.contains(child)) {
                double childStartTime = child.getStartTime();
                if (childStartTime < idlfixt) {
                    idlfixt = childStartTime;
                    
                    if (Math.abs(idlfixt - this.getEndTime()) < Util.DOUBLE_THRESHOLD) {
                        idlfixt = this.getEndTime();
                        break;
                    }
                }
            }
        }
        return idlfixt;
    }
    
    @Override
    public double getIDLflex() {
        return calcIDLflex_NoGaps(Collections.emptyList());
    }
    
    @Override
    public Lane getLane() {
        if (this == workflow.getEntry() || this == workflow.getExit()) {
            return null;
        } else {
            return (Lane) getResource();
        }
    }
    
    @Override
    public double getMeanExecutionTime(InstanceSize size) {
        return ets.get(size);
    }
    
    @Override
    public Set<SchedulingTask> getParents() {
        @SuppressWarnings("rawtypes")
        Set p = (Set) parents;
        @SuppressWarnings("unchecked")
        Set<SchedulingTask> p2 = (Set<SchedulingTask>) p;
        return p2;
    }
    
    @Override
    public double getPredictedEndtime() {
        return predictedEndtime;
    }
    
    @Override
    public Set<SchedulingTask> getPrevTasks() {
        SchedulingTaskImpl prevTask = (SchedulingTaskImpl) getLane().getPrevTask(this);
        if (prevTask != null && !parents.contains(prevTask)) {
            Set<SchedulingTask> result = new HashSet<SchedulingTask>(parents);
            result.add(prevTask);
            return result;
        } else {
            return getParents();
        }
    }
    
    @Override
    public Instance getResource() {
        return resource;
    }
    
    @Override
    public double getStartTime() {
        return startTime;
    }
    
    @Override
    public TaskStatus getStatus() {
        return status;
    }
    
    @Override
    public double getSWfix(Collection<SchedulingTask> notLimitingTasks) {
        if (this == workflow.getExit()) {
            return 0.;
        }
        
        double idlfixt = getIDLfix(notLimitingTasks);
        double taskEndTime = getEndTime();
        double tSWfix = idlfixt - taskEndTime;
        return tSWfix;
    }
    
    @Override
    public boolean satisfyRSWfix(Collection<SchedulingTask> notLimitingTasks, double requiredSW) {
        
        if (this == workflow.getExit()) {
            return false;
        }
        if (requiredSW < Util.DOUBLE_THRESHOLD) {
            return true;
        }
        
        double taskEndTime = getEndTime();
        double newTaskEndTime = taskEndTime + requiredSW;
        
        double idlfixt = Double.MAX_VALUE;
        for (SchedulingTask child : children) {
            if (!notLimitingTasks.contains(child)) {
                double childStartTime = child.getStartTime();
                if (childStartTime < idlfixt) {
                    idlfixt = childStartTime;
                    if (idlfixt - newTaskEndTime < -Util.DOUBLE_THRESHOLD) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    public double getSWflex(Collection<Lane> statics) {
        if (Lane.isUseCache() && workflow.getTSWflexCache().containsKey(this) && statics.isEmpty()) {
            return workflow.getTSWflexCache().get(this);
        }
        double endTime = this.getEndTime();
        double idlflex = calcIDLflex_NoGaps(statics);
        double tswflex = idlflex - endTime;
        
        if (Lane.isUseCache() && statics.isEmpty()) {
            workflow.getTSWflexCache().put(this, tswflex);
        }
        return tswflex;
    }
    
    @Override
    public int getType() {
        return type;
    }
    
    @Override
    public double getVariance(InstanceSize size) {
        return var.get(size);
    }
    
    @Override
    public Set<SchedulingTask> goCompleted() {
        if (status != TaskStatus.RUNNING) {
            throw new RuntimeException(status.toString());
        }
        status = TaskStatus.COMPLETED;
        taskManager.goCompleted(this);
        
        getResource().taskCompletedExecution(this);
        
        Set<SchedulingTask> rdyChildren = new HashSet<>();
        for (SchedulingTaskImpl child : children) {
            boolean childRdy = child.parentCompleted(this);
            if (childRdy) {
                rdyChildren.add(child);
            }
        }
        return rdyChildren;
    }
    
    void goReady() {
        if (status != TaskStatus.WAITING) {
            throw new RuntimeException(status.toString());
        }
        status = TaskStatus.READY;
        taskManager.goReady(this);
        Instance instance = getResource();
        if (instance != null) {
            instance.taskReadyForExecution(this);
            // algorithm has to call instance.updateStatus() 
        }
    }
    
    @Override
    public void goRunning() {
        if (status != TaskStatus.READY) {
            throw new RuntimeException(status.toString());
        }
        status = TaskStatus.RUNNING;
        taskManager.goRunning(this);
        getResource().taskStartExecution(this);
    }
    
    private boolean parentCompleted(SchedulingTaskImpl parent) {
        boolean removed = uncompletedParents.remove(parent);
        if (!removed) {
            throw new RuntimeException();
        }
        if (uncompletedParents.isEmpty()) {
            taskManager = parent.taskManager;
            
            if (resource != null && resource.getManager() == null) {
                resource.setManager(parent.getResource().getManager());
            }
            goReady();
            return true;
        }
        return false;
    }
    
    @Override
    public void readyEntry(SchedTaskManager taskManager, RealResourceManager resourceManager) {
        if (workflow.getEntry() != this) {
            throw new RuntimeException("start Entry called on non entry task");
        }
        this.taskManager = taskManager;
        resource.setManager(resourceManager);
        goReady();
    }
    
    @Override
    public void setEndTime(double endTime) {
        if (status == TaskStatus.READY || status == TaskStatus.WAITING || status == TaskStatus.RUNNING) {
            if (endTime != -1 && endTime < Time.getInstance().getActualTime()) {
                throw new RuntimeException("task scheduled before NOW: new end time=" + endTime + " now=" + Time.getInstance().getActualTime() + " task=" + this.toString());
            }
            this.endTime = endTime;
            Lane lane = getLane();
            if (lane != null) {
                lane.invalidateRsw();
            } else {
                for (SchedulingTask parent : getParents()) {
                    Lane plane = parent.getLane();
                    if (plane != null) {
                        plane.invalidateRsw();
                    }
                }
            }
        } else {
            throw new RuntimeException();
        }
    }
    
    @Override
    public void setPredictedEndtime(double predictedEndtime) {
        if (this.predictedEndtime == -1) {
            this.predictedEndtime = predictedEndtime;
        } else {
            throw new RuntimeException(this.predictedEndtime + " " + predictedEndtime);
        }
    }
    
    @Override
    public void setResource(Instance resource) {
        if (status == TaskStatus.READY || status == TaskStatus.WAITING) {
            this.resource = resource;
        } else {
            throw new RuntimeException();
        }
    }
    
    @Override
    public void setStartTime(double startTime) {
        if (status == TaskStatus.READY || status == TaskStatus.WAITING) {
            if (startTime != -1 && startTime < Time.getInstance().getActualTime()) {
                throw new RuntimeException("task scheduled before NOW: new start time=" + startTime + " now=" + Time.getInstance().getActualTime() + " task=" + this.toString());
            }
            
            this.startTime = startTime;
            Lane lane = getLane();
            if (lane != null) {
                lane.invalidateRsw();
            } else {
                for (SchedulingTask parent : getParents()) {
                    Lane plane = parent.getLane();
                    if (plane != null) {
                        plane.invalidateRsw();
                    }
                }
            }
        } else {
            throw new RuntimeException(status.toString());
        }
    }
    
    @Override
    public String toString() {
        if (Debug.INSTANCE.getDebug() < 0) {
            new RuntimeException("called to string of " + getClass()).printStackTrace(System.out);
            System.exit(0);
        }
        return String.format("ID%05d", getId());
    }
    
    @Override
    public WorkflowInstance getWorkflow() {
        return workflow;
    }
    
    @Override
    public double getDelayBuffer() {
        if (delayBuffer == Util.UNSET) {
            throw new RuntimeException("unset delay buffer on " + this);
        }
        return delayBuffer;
    }
    
    @Override
    public void setDelayBuffer(double buffer) {
        this.delayBuffer = buffer;
    }
    
    @Override
    public double updateDelayBuffer() {
        if (delayBuffer != Util.UNSET) {
            return delayBuffer;
        }
        
        Set<SchedulingTask> nextTasks;
        if (this == workflow.getEntry() || this == workflow.getExit()) {
            nextTasks = getChildren();
        } else {
            // XXX problem with getLane for t_entry
            nextTasks = getNextTasks();
        }
        
        if (this == workflow.getExit()) {
            double dl = workflow.getDeadline();
            double et = endTime;
            delayBuffer = dl - et;
        } else {
            double min = Double.MAX_VALUE;
            double et = endTime;
            for (SchedulingTask next : nextTasks) {
                double st = next.getStartTime();
                double buf = next.updateDelayBuffer();
                double newBuffer = st + buf - et;
                min = Math.min(min, newBuffer);
                if (min < -Util.DOUBLE_THRESHOLD) {
                    throw new RuntimeException("negative buffer in: " + this);
                } else if (min < Util.DOUBLE_THRESHOLD) {
                    min = 0;
                    break;
                }
            }
            
            delayBuffer = min;
        }
        return delayBuffer;
    }
    
    @Override
    public double getLatestEndTime() {
        return latestEndTime;
    }
    
    @Override
    public void setLatestEndTime(double latestEndTime) {
        this.latestEndTime = latestEndTime;
    }
    
    @Override
    public void reset() {
        status = TaskStatus.WAITING;
        taskManager = null;
        
        predictedEndtime = -1;
        delayBuffer = Util.UNSET;
        spareTime = Util.UNSET;
        uncompletedParents.addAll(parents);
    }
    
}
