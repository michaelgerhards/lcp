package statics.initialization.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import algorithm.Visualizer;
import algorithm.misc.ScaledPseudoLane;
import cloud.Instance;
import cloud.InstanceSize;
import cloud.InstanceStatus;
import reality.RealResourceManager;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.initialization.WorkflowInstance;
import statics.util.BillingUtil;
import statics.util.Debug;
import statics.util.Duration;
import statics.util.Tupel;
import statics.util.Util;

public class Lane implements Duration, Instance {

    private static final long serialVersionUID = 6751907817071120552L;

    public static boolean validate = true;

    private static boolean useCache = true;

    public static boolean isUseCache() {
//        util.Debug.INSTANCE.aPrintln("called IsUse Cache with " + useCache);
//        new RuntimeException("called IsUse Cache with " + useCache).printStackTrace(System.out);
//        System.exit(0);

        return useCache;
    }

    public static void setUseCache(boolean aUseCache, String name) {
        statics.util.Debug.INSTANCE.aPrintln("set use lane cache to " + aUseCache + " by " + name);
        useCache = aUseCache;
    }

    static double calcRSWfix(Collection<SchedulingTask> tasks) {
        double rSWfix = Double.MAX_VALUE;

        for (SchedulingTask task : tasks) {
            double tSWfix = task.getSWfix(tasks);
            if (tSWfix < rSWfix) {
                rSWfix = tSWfix;

                if (rSWfix < Util.DOUBLE_THRESHOLD) {
                    rSWfix = 0.;
                    break;
                }
            }
        }
        return rSWfix;
    }

    public static double calcRSWflex(Collection<SchedulingTask> tasksToShift, Collection<Lane> statics) {
        double rSWflex = Double.MAX_VALUE;
        for (SchedulingTask task : tasksToShift) {
            double tSWflex = task.getSWflex(statics);
            if (tSWflex < rSWflex) {
                rSWflex = tSWflex;
                if (rSWflex < Util.DOUBLE_THRESHOLD) {
                    rSWflex = 0.;
                    break;
                }
            }
        }
        return rSWflex;
    }

    // only used during planning
    public static void inverseShiftRecursiveForSuccessorsOf(Lane lane) {
        Set<Lane> children = lane.getUmodChildren();
        for (Lane succ : children) {
            succ.shiftInverseRecursiveWithAllSuccessorsMaxDist();
        }
    }

    public static boolean satisfyRSWfix(Collection<SchedulingTask> tasks, double requiredRsw) { // tasks
        // =
        // task
        // series
        if (requiredRsw < Util.DOUBLE_THRESHOLD) {
            return true;
        }

        double rSWfix = Double.MAX_VALUE;
        for (SchedulingTask task : tasks) {
            double tSWfix = task.getSWfix(tasks); // TODO requiredRsw
            if (tSWfix < rSWfix) {
                rSWfix = tSWfix;

                if (rSWfix < requiredRsw) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void shiftIntern(SchedulingTask task, double requiredThrowerShift) {
        double est = task.getStartTime() + requiredThrowerShift;
        double eft = task.getEndTime() + requiredThrowerShift;
        task.setStartTime(est);
        task.setEndTime(eft);
    }

    private static String[] toNames(Collection<Lane> lanes) {
        String[] result = new String[lanes.size()];
        int i = 0;
        for (Lane lane : lanes) {
            result[i] = lane.getId().toString();
            i++;
        }
        return result;
    }

    private final List<SchedulingTask> tasks;
    private final SchedulingTaskList tasksView;
    private final Set<Lane> children;
    private final Set<Lane> childrenView;
    private final Set<Lane> parents;
    private final Set<Lane> parentsView;
    private final LaneIndex name;
    private InstanceSize instanceSize;
    private double rswFix = Util.UNSET;
    private double rswFlex = Util.UNSET;
    private int nextTaskToSchedule = 0;
    private RealResourceManager resourceManager = null; // TODO move to workflow
    private InstanceStatus status = InstanceStatus.OFFLINE;
    private final WorkflowInstanceImpl workflow;
    private double terminatedTime = Util.UNSET;

    private final Map<Integer, SchedulingTaskList> lastPathCache = new HashMap<>(100);

    Lane(WorkflowInstanceImpl workflow, InstanceSize size, int id) {
        this.workflow = workflow;
        tasks = new ArrayList<>(1000);
        tasksView = new SchedulingTaskList(tasks, size, workflow);
        setInstanceSize(size);
        children = new HashSet<>();
        parents = new HashSet<>();
        childrenView = WorkflowInstance.performanceMode ? children : Collections.unmodifiableSet(children);
        parentsView = WorkflowInstance.performanceMode ? parents : Collections.unmodifiableSet(parents);
        this.name = new LaneIndex(id, this);
    }

    private void addChild(Lane child) {
        if (child == this || child == null) {
            return;
        }
        children.add(child);
        child.parents.add(this);
    }

    private void addParent(Lane parent) {
        if (parent == this || parent == null) {
            return;
        }
        parents.add(parent);
        parent.children.add(this);
    }

    private void addTaskAfterIntern(SchedulingTask task, SchedulingTask prev) {
        validateAddTasks(task);
        int index = tasks.indexOf(prev);
        if (index < 0) {
            throw new RuntimeException(prev + " not hosted on " + this);
        }

        lastPathCache.clear();

        // add
        tasks.add(index + 1, task);
        task.setResource(this);

        // update children
        Collection<SchedulingTask> children = task.getChildren();
        for (SchedulingTask child : children) {
            Lane laneOfChild = child.getLane();
            addChild(laneOfChild);
        }

        // update parents
        Collection<SchedulingTask> parents = task.getParents();
        for (SchedulingTask parent : parents) {
            Lane laneOfParent = parent.getLane();
            addParent(laneOfParent);
        }
    }

    @Override
    public void addTaskAtEnd(SchedulingTask task) {
        if (task.getLane() != null) {
            throw new RuntimeException(task.toString() + " " + task.getLane().getName() + " " + this.getName());
        }
        TaskStatus taskStatus = task.getStatus();
        if (taskStatus == TaskStatus.COMPLETED || taskStatus == TaskStatus.RUNNING) {
            throw new RuntimeException();
        }

        addTaskAtEndIntern(task);
        invalidateRsw();
        validateDependencies();
    }

    @Override
    public void addTaskAtStart(SchedulingTask task) {
        if (task.getLane() != null) {
            throw new RuntimeException(task.toString());
        }
        TaskStatus taskStatus = task.getStatus();
        if (taskStatus == TaskStatus.COMPLETED || taskStatus == TaskStatus.RUNNING) {
            throw new RuntimeException(task.toString());
        }

        addTaskAtStartIntern(task);
        invalidateRsw();
        validateDependencies();
    }

    private void addTaskAtEndIntern(SchedulingTask task) {
        validateAddTasks(task);
        if (tasks.size() > 0 && task.getStartTime() - getEndTime() < -Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException("tasks= " + task + " starts before end of lane: " + this);
        }

        lastPathCache.clear();

        // add
        tasks.add(task);
        task.setResource(this);

        // update children
        Collection<SchedulingTask> children = task.getChildren();
        for (SchedulingTask child : children) {
            Lane laneOfChild = child.getLane();
            addChild(laneOfChild);
        }

        // update parents
        Collection<SchedulingTask> parents = task.getParents();
        for (SchedulingTask parent : parents) {
            Lane laneOfParent = parent.getLane();
            addParent(laneOfParent);
        }
    }

    private void addTaskAtStartIntern(SchedulingTask task) {
        validateAddTasks(task);
        if (tasks.size() > 0 && getStartTime() - task.getEndTime() < -Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException("tasks= " + task + " ends after start of lane: " + this);
        }

        lastPathCache.clear();

        // add
        tasks.add(0, task);
        task.setResource(this);

        // update children
        Collection<SchedulingTask> children = task.getChildren();
        for (SchedulingTask child : children) {
            Lane laneOfChild = child.getLane();
            addChild(laneOfChild);
        }

        // update parents
        Collection<SchedulingTask> parents = task.getParents();
        for (SchedulingTask parent : parents) {
            Lane laneOfParent = parent.getLane();
            addParent(laneOfParent);
        }
    }

    private void calcDependencies() {
        Collection<Lane> oldS = new ArrayList<Lane>(children.size());
        oldS.addAll(children);

        Collection<Lane> oldP = new ArrayList<Lane>(parents.size());
        oldP.addAll(parents);

        children.clear();
        parents.clear();

        for (SchedulingTask task : tasks) {
            Collection<SchedulingTask> children = task.getChildren();
            for (SchedulingTask child : children) {
                Lane laneOfChild = child.getLane();
                addChild(laneOfChild);
            }

            Collection<SchedulingTask> parents = task.getParents();
            for (SchedulingTask parent : parents) {
                Lane laneOfParent = parent.getLane();
                addParent(laneOfParent);
            }
        }

        for (Lane lane : oldS) {
            if (!children.contains(lane)) {
                removeChild(lane);
            }
        }

        for (Lane lane : oldP) {
            if (!parents.contains(lane)) {
                removeParent(lane);
            }
        }
    }

    // @Override
    // public void catchUp(SchedulingTask endTask) {
    // int endIndex = tasks.indexOf(endTask);
    //
    // if (endIndex < 0) {
    // throw new RuntimeException("lane shift startTask < 0 startTask= "
    // + endTask + " in " + this);
    // }
    //
    // boolean modified = false;
    //
    // for (int i = endIndex - 1; i >= 0; --i) {
    // SchedulingTask task = tasks.get(i);
    // TaskStatus taskStatus = task.getStatus();
    // if (taskStatus == TaskStatus.COMPLETED
    // || taskStatus == TaskStatus.RUNNING) {
    // break; // not continue!!!
    // }
    // SchedulingTask succTask = tasks.get(i + 1);
    // double minEst = succTask.getStartTime();
    // double idl = task.getIDLfix();
    // if (idl < minEst) {
    // minEst = idl;
    // }
    // double eft = task.getEndTime();
    // double diff = minEst - eft;
    //
    // if (diff > Util.DOUBLE_THRESHOLD) {
    // shiftIntern(task, diff);
    // modified = true;
    // }
    // }
    // if (modified) {
    // invalidateRsw();
    // }
    // }
    public void deconstruct() {
        Debug.INSTANCE.println(4, "release lane ", this);

        if (status == InstanceStatus.READY_TO_START) {
            getManager().deactivateResource(getName());
        } else if (status == InstanceStatus.RUNNING || status == InstanceStatus.TERMINATED) {
            throw new RuntimeException();
        }

        workflow.removeLane(this);
        this.getId().invalidateLane();
        lastPathCache.clear();

        for (Lane succ : children) {
            succ.parents.remove(this);
        }
        for (Lane pred : parents) {
            pred.children.remove(this);
        }
        for (SchedulingTask t : tasks) {
            t.setResource(null);
        }
        parents.clear();
        children.clear();
        tasks.clear();

    }

    // run
    public void enrichDependenciees(SchedulingTask task) {
        {
            SchedulingTask nextTask = getNextTask(task);
            if (nextTask != null) {
                double est = nextTask.getEarliestStartTimeFixConsideringLanePredecessor();

                double startTime = nextTask.getStartTime();
                double maximalShift = startTime - est;
                double shift = maximalShift;
                shiftInverseRecursiveWithAllSuccessor(shift, nextTask);
            }
        }
        for (SchedulingTask child : task.getChildren()) {
            if (child == workflow.getExit()) {
                break;
            }
            Lane childLLane = child.getLane();
            if (childLLane == this) {
                continue;
            }

            double est = child.getEarliestStartTimeFixConsideringLanePredecessor();
            double startTime = child.getStartTime();
            double maximalShift = startTime - est;
            double shift = maximalShift;
            childLLane.shiftInverseRecursiveWithAllSuccessor(shift, child);
        }
        workflow.repairExit();
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
        Lane other = (Lane) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public Lane extractLane(List<SchedulingTask> lastPath) {

        Lane l = workflow.instantiate(instanceSize);
        l.setManager(getManager());
        int tsize = tasks.size();
        // if (lastPath.size() == tasks.size()) {
        // throw new RuntimeException("whole lane extract!");
        // }

        for (int i = lastPath.size() - 1; i >= 0; --i) {
            SchedulingTask task = lastPath.get(i);

            TaskStatus taskStatus = task.getStatus();
            if (taskStatus == TaskStatus.COMPLETED || taskStatus == TaskStatus.RUNNING) {
                throw new RuntimeException();
            }

            // XXX performance optimization to calc parents / children
            removeTaskIntern(task);
        }

        for (SchedulingTask task : lastPath) {
            l.addTaskAtEndIntern(task);
        }

        invalidateRsw();
        l.invalidateRsw();

        validateDependencies();
        l.validateDependencies();

        if (lastPath.size() == tsize) {
            deconstruct();
        }

        return l;
    }

    public Lane extractLane(SchedulingTask start) {
        int index = tasks.indexOf(start);
        return extractLane(index);
    }

    public Lane extractLane(int startIndex) {
        List<SchedulingTask> subList = new ArrayList<>(tasks.subList(startIndex, tasks.size()));
        return extractLane(subList);
    }

    @Override
    public double getCost() {
        return BillingUtil.getInstance().getCost(this);
    }

    @Override
    public double getEndTime() {
        int s = tasks.size();
        if (s > 0) {
            return tasks.get(s - 1).getEndTime();
        } else {
            return 0;
        }
    }

    @Override
    public double getExecutionTime() {
        double ex = getEndTime() - getStartTime();
        return ex;
    }

    public LaneIndex getId() {
        return name;
    }

    public double getIDLfix() {
        return getEndTime() + getRSWfix();
    }

    @Override
    public InstanceSize getInstanceSize() {
        return instanceSize;
    }

    public double getInverseRSWfix() {
        double inverseRswFix = Double.MAX_VALUE;
        for (SchedulingTask task : tasks) {
            Collection<SchedulingTask> parents = task.getParents();
            double est = task.getStartTime();
            for (SchedulingTask parent : parents) {
                if (!tasks.contains(parent)) {
                    double pEft = parent.getEndTime();
                    double diff = est - pEft;
                    if (diff < inverseRswFix) {
                        inverseRswFix = diff;
                        if (inverseRswFix < Util.DOUBLE_THRESHOLD) {
                            inverseRswFix = 0;
                            break;
                        }
                    }
                }
            }
            if (inverseRswFix < Util.DOUBLE_THRESHOLD) {
                inverseRswFix = 0;
                break;
            }
        }
        return inverseRswFix;
    }

    @Override
    public RealResourceManager getManager() {
        if (resourceManager == null) {
            if (tasks.isEmpty()) {
                throw new RuntimeException();
            }
            setManager(tasks.get(0).getWorkflow().getEntry().getResource().getManager());
        }
        return resourceManager;
    }

    @Override
    public String getName() {
        return getId().toString();
    }

    public SchedulingTask getNextTask(SchedulingTask task) {
        int index = tasks.indexOf(task);
        if (index < 0) {
            throw new RuntimeException(task + " " + this);
        }

        int next = index + 1;
        SchedulingTask result = null;
        if (next < tasks.size()) {
            result = tasks.get(next);
        }
        return result;
    }

    @Override
    public SchedulingTask getNextTaskToSchedule() {
        if (status == InstanceStatus.TERMINATED) {
            throw new RuntimeException();
        }

        if (nextTaskToSchedule < tasks.size()) {
            SchedulingTask task = tasks.get(nextTaskToSchedule);
            return task;
        } else {
            return null;
        }
    }

    public int getPathCount() {
        if (tasks.isEmpty()) {
            return 0;
        }

        if (Lane.isUseCache()) {
            int max = lastPathCache.size();
            SchedulingTaskList schedulingTaskList = lastPathCache.get(max);
            if (schedulingTaskList != null && schedulingTaskList.contains(tasks.get(0))) {
                return max;
            }
        }

        int count = 1;
        List<SchedulingTask> lastPath = new LinkedList<>();
        lastPath.add(tasks.get(tasks.size() - 1));
        for (int ti = tasks.size() - 2; ti >= 0; --ti) {
            SchedulingTask task = tasks.get(ti);
            if (Util.isSequentialRelation(task, lastPath.get(0))) {
                lastPath.add(0, task);
            } else {
                if (Lane.isUseCache() && !lastPathCache.containsKey(count)) {
                    lastPathCache.put(count, new SchedulingTaskList(new ArrayList<>(lastPath), instanceSize, workflow));
                }
                count++;
                lastPath.add(0, task);
            }
        }
        return count;
    }

    public SchedulingTask getPrevTask(SchedulingTask task) {
        int index = tasks.indexOf(task);
        if (index < 0) {
            throw new RuntimeException(task + " " + this);
        }

        int prev = index - 1;
        SchedulingTask result = null;
        if (prev >= 0) {
            result = tasks.get(prev);
        }
        return result;
    }

    public double getRSWfix() {
        if (rswFix == Util.UNSET || !isUseCache()) {
            rswFix = calcRSWfix(tasks);
        } else {
            validateRSWfixCache();
        }
        return rswFix;
    }

    public double getRSWflex() {
        if (rswFlex == Util.UNSET || !isUseCache()) {
            rswFlex = calcRSWflex(tasks, Collections.emptyList());
        } else {
            validateRSWflexCache();
        }
        return rswFlex;
    }

    public double getRSWflexConsideringStatic(Collection<Lane> statics) {
        if (statics.isEmpty()) {
            return getRSWflex();
        } else {
            return calcRSWflex(tasks, statics);
        }
    }

    @Override
    public double getStartTime() {
        if (tasks.isEmpty()) {
            return 0;
        }
        return tasks.get(0).getStartTime();
    }

    @Override
    public InstanceStatus getStatus() {
        return status;
    }

    @Override
    public int getTasksCount() {
        return tasks.size();
    }

    @Override
    public double getTerminatedTime() {
        if (getStatus() != InstanceStatus.TERMINATED) {
            throw new RuntimeException();
        }
        return terminatedTime;
    }

    @Override
    public Set<Lane> getUmodChildren() {
        return childrenView;

    }

    @Override
    public SchedulingTaskList getUmodLastPath() {
        return getUmodLastPath(1);
    }

    /**
     *
     * @param pathnumber 1, 2, 3, 4 ... the number of path to find. see getLastPathCount. 1 -> LastPath
     * @return
     */
    public SchedulingTaskList getUmodLastPath(int pathnumber) {
        SchedulingTaskList result = lastPathCache.get(pathnumber);
        if (Lane.isUseCache() && result != null) {
            return result;
        }

        List<SchedulingTask> lastPath = new LinkedList<SchedulingTask>();
        SchedulingTaskList prevResult = lastPathCache.get(pathnumber - 1);
        if (Lane.isUseCache() && prevResult != null) {
            lastPath.addAll(prevResult);
            int lastIndexOf = tasks.lastIndexOf(prevResult.getFirst());
            lastPath.add(0, tasks.get(lastIndexOf - 1));
            for (int ti = lastIndexOf - 2; ti >= 0; --ti) {
                SchedulingTask task = tasks.get(ti);
                if (Util.isSequentialRelation(task, lastPath.get(0))) {
                    lastPath.add(0, task);
                } else {
                    break;
                }
            }
        } else {
            int found = 0;
            lastPath.add(tasks.get(tasks.size() - 1));
            for (int ti = tasks.size() - 2; ti >= 0; --ti) {
                SchedulingTask task = tasks.get(ti);
                if (Util.isSequentialRelation(task, lastPath.get(0))) {
                    lastPath.add(0, task);
                } else {
                    ++found;
                    if (found == pathnumber) {
                        break;
                    } else {
                        lastPath.add(0, task);
                    }
                }
            }
        }

        result = new SchedulingTaskList(lastPath, instanceSize, workflow);
        if (Lane.isUseCache()) {
            lastPathCache.put(pathnumber, result);
        }
        return result;
    }

    @Override
    public Set<Lane> getUmodParents() {
        return parentsView;
    }

    @Override
    public SchedulingTaskList getUmodTasks() {
        return tasksView;
    }

    private void goIdling() {
        if (getStatus() != InstanceStatus.RUNNING) {
            throw new RuntimeException();
        }
        status = InstanceStatus.IDLING;
        getManager().goIdling(getName());
    }

    private void goReady() {
        if (getStatus() != InstanceStatus.IDLING) {
            throw new RuntimeException();
        }
        status = InstanceStatus.READY;
        getManager().goReady(getName());
    }

    private void goReadyToStart() {
        if (getStatus() != InstanceStatus.OFFLINE) {
            throw new RuntimeException();
        }
        status = InstanceStatus.READY_TO_START;
        getManager().activateResource(this);
    }

    private void goRunning() {
        if (getStatus() != InstanceStatus.READY && getStatus() != InstanceStatus.READY_TO_START) {
            throw new RuntimeException(status.toString());
        }
        status = InstanceStatus.RUNNING;
        getManager().goRunning(getName());
    }

    @Override
    public void goTerminated(double time) {
        if (getStatus() != InstanceStatus.IDLING && getNextTaskToSchedule() == null) {
            throw new RuntimeException();
        }
        status = InstanceStatus.TERMINATED;
        getManager().goTerminate(getName(), time);
        terminatedTime = time;
    }

    public boolean hasGaps() {
        Collection<Tupel<SchedulingTask, SchedulingTask>> gaps = Util.getGaps(tasks, Util.DOUBLE_THRESHOLD);
        boolean result = gaps.size() > 0;
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    // TODO make private!!!
    public void invalidateRsw() {
        if (useCache) {

            for (SchedulingTask t : tasks) {
                workflow.getTSWflexCache().remove(t);
                workflow.getTSDflexCache().remove(t);
            }

            rswFix = Util.UNSET;
            rswFlex = Util.UNSET;
            Set<Lane> visited = new HashSet<Lane>();
            visited.add(this);

            for (Lane parent : parents) {
                parent.rswFix = Util.UNSET;
                parent.invalidateRSWflexIncludingPredecessorsRecursive(visited);
            }
        }
    }

    private void invalidateRSWflexIncludingPredecessorsRecursive(Set<Lane> visited) {
        for (SchedulingTask t : tasks) {
            workflow.getTSWflexCache().remove(t);
            workflow.getTSDflexCache().remove(t);
        }
        visited.add(this);
        if (rswFlex != Util.UNSET) {
            // if own rswFlex is unset, automatically all predecessor rswFlex
            // are also unset since recursive structure. Nothing to do then.
            rswFlex = Util.UNSET;

            for (Lane parent : parents) {
                if (!visited.contains(parent)) {
                    parent.invalidateRSWflexIncludingPredecessorsRecursive(visited);
                }
            }
        }
    }

    public boolean isOnline() {
        return status == InstanceStatus.IDLING || status == InstanceStatus.RUNNING || status == InstanceStatus.READY;
    }

    // planning
    public void prepareSuccessorsForOwnVertScale(ScaledPseudoLane scaledLane) {
        boolean scaleUp = scaledLane.getInstanceSize().isFaster(getInstanceSize());
        if (scaleUp) {
            return;
        }
        prepareSuccessorsForOwnVertScale(scaledLane, 0);
    }

    // planning
    public Set<Lane> prepareSuccessorsForOwnVertScale(ScaledPseudoLane scaledLane, double ownShift) {
        if (ownShift < -Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException("negative own shift not possible: " + ownShift + " l= " + scaledLane);
        } else if (ownShift < Util.DOUBLE_THRESHOLD) {
            boolean scaleUp = scaledLane.getInstanceSize().isFaster(getInstanceSize());
            if (scaleUp) {
                return Collections.emptySet();
            }
        }

        validateInputTasksEqTasks(scaledLane.getOriginalTasks());

        Set<Lane> shifted = new HashSet<Lane>();
        shifted.add(this);

        for (SchedulingTask task : scaledLane.getOriginalTasks()) {
            double oldEnd = scaledLane.getEndTimeOfTask(task);
            double newEnd = oldEnd + ownShift;

            Collection<SchedulingTask> children = task.getChildren();
            for (SchedulingTask child : children) {
                Lane laneOfChild = child.getLane();
                if (this != laneOfChild) {
                    double childEst = child.getStartTime();
                    double shift = newEnd - childEst;
                    if (shift > Util.DOUBLE_THRESHOLD) {
                        Set<Lane> tmp = laneOfChild.shiftEquallyRecursiveWithAllSuccessors(shift);
                        shifted.addAll(tmp);
                    }
                }
            }
        }
        return shifted;
    }

    public void reassignTaskAfterTaskOf(SchedulingTask taskToReassign, SchedulingTask prevTaskOnTargetLane, Lane targetLane) {
        TaskStatus taskStatus = taskToReassign.getStatus();
        if (taskStatus == TaskStatus.COMPLETED || taskStatus == TaskStatus.RUNNING) {
            throw new RuntimeException();
        }

        removeTaskIntern(taskToReassign);

        targetLane.addTaskAfterIntern(taskToReassign, prevTaskOnTargetLane);

        invalidateRsw();
        targetLane.invalidateRsw();

        validateDependencies();
        targetLane.validateDependencies();

    }

    public void addTaskAfterTask(SchedulingTask newTask, SchedulingTask predTask) {
        if (newTask.getLane() != null) {
            throw new RuntimeException();
        }
        addTaskAfterIntern(newTask, predTask);
        invalidateRsw();
        validateDependencies();
    }

    public void reassignTaskToEndOf(SchedulingTask taskToReassign, Lane targetLane) {

        TaskStatus taskStatus = taskToReassign.getStatus();
        if (taskStatus == TaskStatus.COMPLETED || taskStatus == TaskStatus.RUNNING) {
            throw new RuntimeException();
        }

        removeTaskIntern(taskToReassign);

        targetLane.addTaskAtEndIntern(taskToReassign);

        invalidateRsw();
        targetLane.invalidateRsw();

        validateDependencies();
        targetLane.validateDependencies();

    }

    public void reassignToEndFrom(Lane thrower) {
        Debug.INSTANCE.println(4, "migrate ", statics.util.outputproxy.Proxy.collectionToString(thrower.tasks), " from ", thrower, " to end of ", this);
        if (thrower.getInstanceSize() != getInstanceSize()) {
            throw new RuntimeException("Lane: different instance size");
        }

        Collection<SchedulingTask> throwerTasks = new ArrayList<>(thrower.tasks);
        thrower.deconstruct();

        for (SchedulingTask task : throwerTasks) {
            TaskStatus taskStatus = task.getStatus();
            if (taskStatus == TaskStatus.RUNNING || taskStatus == TaskStatus.COMPLETED) {
                throw new RuntimeException();
            }

            // XXX use own add method. calc parents / children different:
            // loop over all throwerParentLanes and check if these lanes contain
            // a child of own tasks!
            // write contains method in lane using a set to have contains O(1)
            addTaskAtEndIntern(task);
        }
        // thrower.tasks.clear();
        // thrower.children.clear();
        // thrower.parents.clear();
        invalidateRsw();
        validateDependencies();
    }

    public void reassignToStartFrom(Lane thrower) {

        Debug.INSTANCE.println(4, "migrate ", statics.util.outputproxy.Proxy.collectionToString(thrower.tasks), " from ", thrower, " to start of ", this);

        if (thrower.getInstanceSize() != getInstanceSize()) {
            throw new RuntimeException("Lane: different instance size");
        }

        List<SchedulingTask> throwerTasks = new ArrayList<>(thrower.tasks);
        thrower.deconstruct();

        for (int i = throwerTasks.size() - 1; i >= 0; --i) {
            SchedulingTask task = throwerTasks.get(i);
            TaskStatus taskStatus = task.getStatus();
            if (taskStatus == TaskStatus.RUNNING || taskStatus == TaskStatus.COMPLETED) {
                throw new RuntimeException();
            }
            // XXX use own add method. calc parents / children different:
            // loop over all throwerParentLanes and check if these lanes contain
            // a child of own tasks!
            // write contains method in lane using a set to have contains O(1)
            addTaskAtStartIntern(task);
        }
        // thrower.tasks.clear();
        // thrower.children.clear();
        // thrower.parents.clear();

        invalidateRsw();
        validateDependencies();
    }

    private void removeChild(Lane child) {
        children.remove(child);
        child.parents.remove(this);
    }

    private void removeParent(Lane parent) {
        parents.remove(parent);
        parent.children.remove(this);
    }

    private void removeTaskIntern(SchedulingTask task) {
        int indexOf = tasks.lastIndexOf(task);
        if (indexOf < 0) {
            throw new RuntimeException("unable to remove task: " + task + " from " + this);
        }
        lastPathCache.clear();
        tasks.remove(indexOf);
        calcDependencies();
    }

    // run
    public void repairDependencies(SchedulingTask task) {
        final double tet = task.getEndTime();
        // same Resource

        SchedulingTask nextTask = getNextTask(task);
        if (nextTask != null) {
            double st = nextTask.getStartTime();
            double diff = tet - st;
            if (diff > Util.DOUBLE_THRESHOLD) {
                // only update to past!
                shiftRecursiveWithAllSuccessors(diff, nextTask);
            }
        }

        // child resources
        Set<SchedulingTask> taskChildren = task.getChildren();
        for (SchedulingTask child : taskChildren) {
            double st = child.getStartTime();
            double diff = tet - st;
            if (diff > Util.DOUBLE_THRESHOLD) {
                if (child == workflow.getExit()) {
                    break; // no other children possible
                } else {
                    Lane childLane = child.getLane();
                    if (childLane == this) {
                        // XXX is it possible that diff > 0 and childLane ==
                        // this?
                        continue;
                    }
                    // only update to past!
                    childLane.shiftRecursiveWithAllSuccessors(diff, child);
                }
            }

        }
        workflow.repairExit();
    }

    public boolean satisfyNewStartTimeFix(double startTime) {
        if (rswFix != Util.UNSET && isUseCache()) {
            if (getStartTime() + rswFix - startTime > -Util.DOUBLE_THRESHOLD) {
                return true;
            }
        }
        Set<SchedulingTask> tasksSet = new HashSet<>(tasks);
        double time = startTime;
        for (SchedulingTask task : tasks) {
            double st = task.getStartTime();
            double et = task.getEndTime();
            double reqSWfix = time - st;
            if (reqSWfix > Util.DOUBLE_THRESHOLD) {
                double avSWfix = task.getSWfix(tasksSet);
                if (reqSWfix - avSWfix > Util.DOUBLE_THRESHOLD) {
                    return false;
                }
            } else {
                reqSWfix = 0;
            }
            time = et + reqSWfix;
        }
        return true;
    }

    public boolean satisfyNewStartTimeFlex(double startTime) {
        if (rswFlex != Util.UNSET && isUseCache()) {
            if (getStartTime() + rswFlex - startTime > -Util.DOUBLE_THRESHOLD) {
                return true;
            }
        }

        double time = startTime;
        for (SchedulingTask task : tasks) {
            double st = task.getStartTime();
            double et = task.getEndTime();
            double reqSWflex = time - st;
            if (reqSWflex > Util.DOUBLE_THRESHOLD) {
                double avSWflex = task.getSWflex(Collections.emptySet()); // TODO
                // statics?
                if (reqSWflex - avSWflex > Util.DOUBLE_THRESHOLD) {
                    return false;
                }
            } else {
                reqSWflex = 0;
            }
            time = et + reqSWflex;
        }
        return true;

    }

    public void scale(ScaledPseudoLane scaledLane) {
        Debug.INSTANCE.println(1, "scale from ", getInstanceSize(), " to ", scaledLane.getInstanceSize(), " : ", this);

        if (scaledLane.getInstanceSize() == getInstanceSize()) {
            throw new RuntimeException("cannot scale to own size");
        }

        setInstanceSize(scaledLane.getInstanceSize());
        for (SchedulingTask ti : tasks) {
            TaskStatus taskStatus = ti.getStatus();
            if (taskStatus == TaskStatus.RUNNING || taskStatus == TaskStatus.COMPLETED) {
                throw new RuntimeException();
            }

            double est = scaledLane.getStartTimeOfTask(ti);
            ti.setStartTime(est);
            // actual end time
            double et = ti.getExecutionTime(getInstanceSize());
            double eft = est + et;
            ti.setEndTime(eft);
        }
        invalidateRsw();
        validateDependencies();
    }

    // run
    // public void scheduleAtEarliestTime() {
    //
    // SchedulingTask first = tasks.get(0);
    // double startTime = first.getStartTime();
    // double earliestStartTimeFix = first.getEarliestStartTimeFix();
    //
    // double now = Time.getInstance().getActualTime();
    // if (earliestStartTimeFix < now) {
    // earliestStartTimeFix = now;
    // }
    //
    // double shift = startTime - earliestStartTimeFix;
    //
    // if (shift > Util.DOUBLE_THRESHOLD) {
    // shiftInverseRecursiveWithAllSuccessor(shift, first);
    // }
    //
    // }
    private void setInstanceSize(InstanceSize instanceSize) {
        this.instanceSize = instanceSize;
        this.tasksView.setSize(instanceSize);
    }

    @Override
    public void setManager(RealResourceManager resourceManager) {
        if (this.resourceManager != null) {
            throw new RuntimeException();
        }
        this.resourceManager = resourceManager;
    }

    // planning
    public void shift(double time) {
        shift(time, tasks.get(0));
    }

    // planning and run
    public void shift(double time, SchedulingTask startTask) {
        if (Math.abs(time) < Util.DOUBLE_THRESHOLD) {
            return;
        } else if (time < 0) {
            throw new RuntimeException("lane shift time= " + time + " in " + this);
        }

        int startIndex = tasks.indexOf(startTask);

        if (startIndex < 0) {
            throw new RuntimeException("lane shift startTask < 0 startTask= " + startTask + " in " + this);
        }

        for (int i = startIndex; i < tasks.size(); ++i) {
            SchedulingTask task = tasks.get(i);
            TaskStatus taskStatus = task.getStatus();
            if (taskStatus == TaskStatus.RUNNING || taskStatus == TaskStatus.COMPLETED) {
                throw new RuntimeException();
            }

            shiftIntern(task, time);
            double eft = task.getEndTime();
            if (i + 1 < tasks.size()) {
                SchedulingTask nextTask = tasks.get(i + 1);
                time = eft - nextTask.getStartTime();
            }

            if (time < Util.DOUBLE_THRESHOLD) {
                break;
            }
        }
        invalidateRsw();
        validateDependencies();

    }

    // only used during planning because catch up!
    public Set<Lane> shiftEquallyRecursiveWithAllSuccessors(double equalShift) {
        if (equalShift < -Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException("no negative shifts: " + equalShift);
        } else if (equalShift < Util.DOUBLE_THRESHOLD) {
            return null;
        }
        Set<Lane> shifted = new HashSet<Lane>();
        shifted.add(this);

        for (int i = 0; i < tasks.size(); ++i) {
            SchedulingTask task = tasks.get(i);
            double oldEnd = task.getEndTime();
            double newEnd = oldEnd + equalShift;

            Collection<SchedulingTask> children = task.getChildren();
            for (SchedulingTask child : children) {
                if (child == workflow.getExit()) {
                    break; // no other children possible
                }
                Lane laneOfChild = child.getLane();
                if (this != laneOfChild) {
                    double childEst = child.getStartTime();
                    double shift = newEnd - childEst;
                    if (shift > Util.DOUBLE_THRESHOLD) {
                        Set<Lane> tmp = laneOfChild.shiftEquallyRecursiveWithAllSuccessors(shift);
                        shifted.addAll(tmp);
                    }
                }
            }
        }
        shift(equalShift);
        return shifted;
    }

    // only planning
    private void shiftInverse(double shiftTime) {
        shiftInversePlanning(shiftTime, tasks.get(0));

    }

    // run only dynamic
    private void shiftInverse(double shiftTimeForFirstTask, SchedulingTask start) {
        if (shiftTimeForFirstTask < Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException();
        }

        int index = tasks.indexOf(start);
        if (index < 0) {
            throw new RuntimeException();
        }

        double shiftTime = shiftTimeForFirstTask;
        for (int i = index; i < tasks.size(); ++i) {
            SchedulingTask task = tasks.get(i);
            TaskStatus taskStatus = task.getStatus();
            if (taskStatus == TaskStatus.RUNNING || taskStatus == TaskStatus.COMPLETED) {
                throw new RuntimeException();
            }
            shiftIntern(task, -shiftTime);

            if (i + 1 < tasks.size()) {
                SchedulingTask nextTask = tasks.get(i + 1);
                double newSt = nextTask.getEarliestStartTimeFixConsideringLanePredecessor();

                double oldSt = nextTask.getStartTime();
                shiftTime = oldSt - newSt;
            }
        }
        invalidateRsw();
        validateDependencies();
    }

    // only planning
    private void shiftInversePlanning(double shiftTime, SchedulingTask start) {
        if (shiftTime < Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException();
        }

        int index = tasks.indexOf(start);
        if (index < 0) {
            throw new RuntimeException();
        }

        for (int i = index; i < tasks.size(); ++i) {
            SchedulingTask task = tasks.get(i);
            TaskStatus taskStatus = task.getStatus();
            if (taskStatus == TaskStatus.RUNNING || taskStatus == TaskStatus.COMPLETED) {
                throw new RuntimeException();
            }
            shiftIntern(task, -shiftTime);
        }
        invalidateRsw();
        validateDependencies();
    }

    // run, enrich dependencies
    private void shiftInverseRecursiveWithAllSuccessor(double shiftTimeOfFirstTask, SchedulingTask start) {
        if (shiftTimeOfFirstTask < -Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException("no negative shifts: " + shiftTimeOfFirstTask);
        } else if (shiftTimeOfFirstTask < Util.DOUBLE_THRESHOLD) {
            return;
        }

        int index = tasks.indexOf(start);

        shiftInverse(shiftTimeOfFirstTask, start);
        for (int i = index; i < tasks.size(); ++i) {
            SchedulingTask task = tasks.get(i);
            Collection<SchedulingTask> children = task.getChildren();
            for (SchedulingTask child : children) {
                if (child == workflow.getExit()) {
                    break; // no other children possible
                }
                Lane laneOfChild = child.getLane();
                if (this != laneOfChild) {
                    double childEst = child.getStartTime();
                    double childEarliestSt = child.getEarliestStartTimeFixConsideringLanePredecessor();

                    double childNewStart = childEst - shiftTimeOfFirstTask;
                    double max = Math.max(childNewStart, childEarliestSt);
                    double shift = childEst - max;
                    if (shift > Util.DOUBLE_THRESHOLD) {
                        laneOfChild.shiftInverseRecursiveWithAllSuccessor(shift, child);
                    } else if (shift < -Util.DOUBLE_THRESHOLD) {
                        throw new RuntimeException(
                                child.toString() + " earliest: " + childEarliestSt + " shift= " + shift);
                    }
                }
            }
        }

    }

    // only during planning
    private boolean shiftInverseRecursiveWithAllSuccessorsMaxDist() {
        double shiftTime = getInverseRSWfix();
        if (shiftTime > Util.DOUBLE_THRESHOLD) {
            shiftInverse(shiftTime);
            for (Lane succ : children) {
                succ.shiftInverseRecursiveWithAllSuccessorsMaxDist();
            }
            return true;
        } else {
            return false;
        }
    }

    // run
    private void shiftRecursiveWithAllSuccessors(double ownShift, SchedulingTask start) {
        if (ownShift < -Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException("no negative shifts: " + ownShift);
        } else if (ownShift < Util.DOUBLE_THRESHOLD) {
            return;
        }

        int index = tasks.indexOf(start);

        for (int i = index; i < tasks.size(); ++i) {
            SchedulingTask task = tasks.get(i);
            double oldEnd = task.getEndTime();
            double newEnd = oldEnd + ownShift;

            Collection<SchedulingTask> children = task.getChildren();
            for (SchedulingTask child : children) {
                if (child == workflow.getExit()) {
                    break; // no other children possible
                }
                Lane laneOfChild = child.getLane();
                if (this != laneOfChild) {
                    double childEst = child.getStartTime();
                    double shift = newEnd - childEst;
                    if (shift > Util.DOUBLE_THRESHOLD) {
                        laneOfChild.shiftRecursiveWithAllSuccessors(shift, child);
                    }
                }
            }
        }
        shift(ownShift, start);
    }

    @Override
    // schedulingTaskImpl goCompleted()
    public void taskCompletedExecution(SchedulingTask task) {
        if (task != tasks.get(nextTaskToSchedule)) {
            throw new RuntimeException();
        }
        nextTaskToSchedule++;
        goIdling();
        SchedulingTask schedulingTask = getNextTaskToSchedule();
        if (schedulingTask == null) {
            // goTerminated(); // XXX?
        } else if (schedulingTask.getStatus() == TaskStatus.READY) {
            goReady();
        }
    }

    @Override
    // schedulingTaskImpl goReady()
    public void taskReadyForExecution(SchedulingTask task) {
        if (task.getStatus() != TaskStatus.READY) {
            throw new RuntimeException();
        }

        SchedulingTask nextTask = getNextTaskToSchedule();
        if (status == InstanceStatus.OFFLINE && nextTask == task) {
            goReadyToStart();
        }
        if (status == InstanceStatus.IDLING && nextTask == task) {
            goReady();
        }
    }

    public void refreshStatus() {
        if (status != InstanceStatus.READY) {
            throw new RuntimeException(this.toString());
        }
        SchedulingTask nextTask = getNextTaskToSchedule();
        if (nextTask == null) {
            goRunning();
            goIdling();
        } else if (nextTask.getStatus() == TaskStatus.RUNNING || nextTask.getStatus() == TaskStatus.COMPLETED) {
            throw new RuntimeException(this.toString() + " taskstatus=" + nextTask.getStatus());
        } else if (nextTask.getStatus() == TaskStatus.WAITING) {
            goRunning();
            goIdling();
        }
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
    // schedulingTaskImpl goRunning()
    public void taskStartExecution(SchedulingTask task) {
        SchedulingTask nextTask = getNextTaskToSchedule();
        if (nextTask != task) {
            throw new RuntimeException();
        }
        goRunning();
    }

    @Override
    public String toString() {

        if (Debug.INSTANCE.getDebug() < 0) {
            new RuntimeException("called to string of " + getClass()).printStackTrace(System.out);
            System.exit(0);
        }

        return String.format(
                "name= %4s size=%s status=%s ratu= %7.2f ex= %10.2f st= %10.2f et= %10.2f dl= %10.2f rsw= %10.2f tasks= %s preds= %s succs= %s",
                name, getInstanceSize().getName(), status.toString(),
                BillingUtil.getInstance().getUnusedCapacity(getExecutionTime()), getExecutionTime(),
                getStartTime(), getEndTime(), getIDLfix(), getRSWfix(), Visualizer.formatList(tasks),
                Arrays.toString(toNames(parents)), Arrays.toString(toNames(children)));

    }

    public ScaledPseudoLane tryScalingIdlFix(InstanceSize newInstanceSize) {
        return tryScalingIdlFix(newInstanceSize, tasks);
    }

    public ScaledPseudoLane tryScalingIdlFix(InstanceSize newInstanceSize, List<SchedulingTask> tasksToScale) {
        if (newInstanceSize == getInstanceSize() || tasksToScale.isEmpty()) {
            throw new RuntimeException("silly scaling: " + this);
        }

        Map<SchedulingTask, Double> localTaskStarttimes = new HashMap<>((int) (tasksToScale.size() * 1.5));
        double taskTime = tasksToScale.get(0).getStartTime();
        boolean gap = false;
        Set<SchedulingTask> tasksToScaleSet = new HashSet<>(tasksToScale);
        for (SchedulingTask ti : tasksToScale) {
            TaskStatus taskStatus = ti.getStatus();
            if (taskStatus == TaskStatus.RUNNING || taskStatus == TaskStatus.COMPLETED) {
                throw new RuntimeException();
            }

            double newTaskTime;
            // different performance classes?
            if (tasksToScale.size() < tasks.size()) {
                newTaskTime = Util.calcNewTaskEst(taskTime, tasksToScaleSet, ti);
            } else {
                newTaskTime = Util.calcNewTaskEstIgnoreTaskOnOwnInstance(taskTime, ti);
            }

            if (newTaskTime - taskTime > -Util.DOUBLE_THRESHOLD) {
                gap = true;
            }

            taskTime = newTaskTime;
            localTaskStarttimes.put(ti, taskTime);
            taskTime += ti.getExecutionTime(newInstanceSize);
            double lft = ti.getIDLfix(tasksToScaleSet);
            if (lft - taskTime < -Util.DOUBLE_THRESHOLD) {
                return null;
            }
        } // for tasks in path

        SchedulingTaskList list = new SchedulingTaskList(tasksToScale, newInstanceSize, workflow);
        ScaledPseudoLane result = new ScaledPseudoLane(list, localTaskStarttimes, gap);
        return result;
    }

    public ScaledPseudoLane tryScalingIdlFlex(InstanceSize newInstanceSize) {
        return tryScalingIdlFlex(newInstanceSize, tasks);
    }

    public ScaledPseudoLane tryScalingIdlFlex(InstanceSize newInstanceSize, List<SchedulingTask> tasksToScale) {
        if (newInstanceSize == getInstanceSize() || tasksToScale.isEmpty()) {
            throw new RuntimeException("silly global scaling: " + this);
        }
        Map<SchedulingTask, Double> localTaskStarttimes = new HashMap<>((int) (tasksToScale.size() * 1.5));
        double taskTime = tasksToScale.get(0).getStartTime();
        boolean gap = false;
        for (SchedulingTask ti : tasksToScale) {
            TaskStatus taskStatus = ti.getStatus();
            if (taskStatus == TaskStatus.RUNNING || taskStatus == TaskStatus.COMPLETED) {
                throw new RuntimeException();
            }

            double newTaskTime;
            // different performance classes?
            if (tasksToScale.size() < tasks.size()) {
                newTaskTime = Util.calcNewTaskEst(taskTime, tasksToScale, ti);
            } else {
                newTaskTime = Util.calcNewTaskEstIgnoreTaskOnOwnInstance(taskTime, ti);
            }
            if (newTaskTime - taskTime > -Util.DOUBLE_THRESHOLD) {
                gap = true;
            }

            localTaskStarttimes.put(ti, taskTime);
            taskTime += ti.getExecutionTime(newInstanceSize);
            double deadline = ti.getIDLflex();
            double diff = deadline - taskTime;
            if (diff < -Util.DOUBLE_THRESHOLD) {
                return null;
            }
        } // for tasks in path

        SchedulingTaskList list = new SchedulingTaskList(tasksToScale, newInstanceSize, workflow);
        ScaledPseudoLane result = new ScaledPseudoLane(list, localTaskStarttimes, gap);
        return result;

    }

    private void validateAddTasks(SchedulingTask task) {
        if (validate) {
            if (task == null) {
                throw new RuntimeException("task =n ull");
            }
            double start = task.getStartTime();
            double end = task.getEndTime();

            if (start > end) {
                System.out.println(start);
                System.out.println(end);
                throw new RuntimeException("lane addTask start after end newTask= " + task + " in " + this);
            }

            for (SchedulingTask exists : tasks) {
                double existsStart = exists.getStartTime();
                double existsEnd = exists.getEndTime();
                if ((start - existsStart > Util.DOUBLE_THRESHOLD && existsEnd - start > Util.DOUBLE_THRESHOLD)
                        || (end - existsStart > Util.DOUBLE_THRESHOLD && existsEnd - end > Util.DOUBLE_THRESHOLD)) {
                    System.out.println("newTaskstart= " + start);
                    System.out.println("newTaskend  = " + end);
                    System.out.println("oldTaskstart= " + existsStart);
                    System.out.println("oldTaskend  = " + existsEnd);
                    throw new RuntimeException("lane addTask concurrent tasks newTask= " + task + " existing task= "
                            + exists + " in " + this);
                }
            }
        }
    }

    private void validateDependencies() {
        if (validate) {
            double space = getRSWfix();
            if (space < -Util.DOUBLE_THRESHOLD) {
                throw new RuntimeException("lane negative shift space: space= " + space + " in " + this);
            }
        }
    }

    private void validateInputTasksEqTasks(List<SchedulingTask> tasksToShift) {
        if (validate) {
            if (tasksToShift.size() != tasks.size()) {
                throw new RuntimeException("problem in Lane with size");
            }

            boolean val = true;
            for (SchedulingTask task : tasksToShift) {
                if (!tasks.contains(task)) {
                    val = false;
                    break;
                }
            }
            if (!val) {
                throw new RuntimeException("problem in Lane");
            }

        }
    }

    private void validateRSWfixCache() {
        if (validate && isUseCache()) {
            double rsw = calcRSWfix(tasks);
            if (Math.abs(rswFix - rsw) > Util.DOUBLE_THRESHOLD) {
                throw new RuntimeException("stored= " + rswFix + " real=" + rsw);
            }
        }
    }

    private void validateRSWflexCache() {
        if (validate && isUseCache()) {
            double rsw = calcRSWflex(tasks, Collections.<Lane>emptyList());
            if (Math.abs(rswFlex - rsw) > Util.DOUBLE_THRESHOLD) {
                throw new RuntimeException("stored= " + rswFlex + " real=" + rsw);
            }
        }
    }

    @Override
    public WorkflowInstance getWorkflow() {
        return workflow;
    }

    @Override
    public void reset() {
        nextTaskToSchedule = 0;
        resourceManager = null;
        status = InstanceStatus.OFFLINE;
        terminatedTime = Util.UNSET;
    }

}
