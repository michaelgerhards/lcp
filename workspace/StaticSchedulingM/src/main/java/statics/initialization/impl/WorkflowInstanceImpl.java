package statics.initialization.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cloud.Instance;
import cloud.InstanceSize;
import statics.initialization.DependencyGraph;
import statics.initialization.Planner;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.BillingUtil;
import statics.util.Util;

public final class WorkflowInstanceImpl implements WorkflowInstance, Serializable {

    private static final long serialVersionUID = -3673530108288921011L;

    private double deadline = -1;
    private Set<SchedulingTaskImpl> tasks;
    private final String workflowName;
    private final double billingTimeInterval;
    private final List<InstanceSize> instanceSizes;
    private final Set<Lane> lanes = new HashSet<>(10005);

    private Instance dummy;
    private String algorithmName;
    private Planner etPlanner;
    private int nextLaneId = 1;

    private transient Map<SchedulingTask, Double> TSWflexCache;
    private transient Map<SchedulingTask, Double> TSDflexCache;
    private SchedulingTask entry;
    private SchedulingTask exit;

    WorkflowInstanceImpl(String name, double billingTimeInterval, List<InstanceSize> instanceSizes) {
        this.workflowName = name;
        this.billingTimeInterval = billingTimeInterval;
        this.instanceSizes = instanceSizes;

        InstanceSize dummySize = null;
        for (InstanceSize size : instanceSizes) {
            if (size.isDummy()) {
                dummySize = size;
                break;
            }
        }
        if (dummySize == null) {
            throw new RuntimeException();
        }

        dummy = new DummyInstance(this, dummySize);
    }

    private WorkflowInstanceImpl deserialize(byte[] ba) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(ba));
            WorkflowInstanceImpl s = (WorkflowInstanceImpl) ois.readObject();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            // Some serious error :< //
            throw new RuntimeException(e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void setTasksOnce(Map<Integer, SchedulingTaskImpl> tasks) {
        if (this.tasks != null) {
            throw new RuntimeException();
        }
        this.tasks = new HashSet<>(tasks.values());
        entry = tasks.get(DependencyGraph.ENTRY_ID);
        exit = tasks.get(DependencyGraph.EXIT_ID);
        dummy.addTaskAtEnd(getEntry());
        dummy.addTaskAtEnd(getExit());
    }

    @Override
    public double getAtuLength() {
        return billingTimeInterval;
    }

    @Override
    public double getDeadline() {
        return deadline;
    }

    void setDeadline(double deadline) {
        this.deadline = deadline;
    }

    @Override
    public String getWorkflowName() {
        return workflowName;
    }

    @Override
    public List<InstanceSize> getInstanceSizes() {
        return instanceSizes;
    }

    @Override
    public Set<SchedulingTask> getTasks() {
        @SuppressWarnings("rawtypes")
        Set t = (Set) tasks;
        @SuppressWarnings("unchecked")
        Set<SchedulingTask> t2 = (Set<SchedulingTask>) t;
        return t2;
    }

    @Override
    public SchedulingTask getEntry() {
        return entry;
    }

    @Override
    public SchedulingTask getExit() {
        return exit;
    }

    @Override
    public Lane instantiate(InstanceSize instanceSize) {
        Lane inst = new Lane(this, instanceSize, nextLaneId++);
        lanes.add(inst);
        return inst;
    }

    // Lane deconstruct
    void removeLane(Lane inst) {
        boolean remove = lanes.remove(inst);
        if (!remove) {
            throw new RuntimeException(inst.toString());
        }
    }

    @Override
    public boolean existsLane(Lane lane) {
        return lanes.contains(lane);// TODO get rid of this
    }

    @Override
    public Map<String, Instance> getInstances() {
        // TODO performance, only for visualization
        Map<String, Instance> instances = new HashMap<>(lanes.size() + 1);
        instances.put(dummy.getName(), dummy);
        for (Lane l : lanes) {
            instances.put(l.getName(), l);
        }
        return Collections.unmodifiableMap(instances);
    }

    @Override
    public Set<Lane> getLanes() {
        return performanceMode ? lanes : Collections.unmodifiableSet(lanes);
    }

    @Override
    public Instance getDummyInstance() {
        return dummy;
    }

    @Override
    public double getMakespan() {
        double endtime = 0.;
        for (Lane instance : lanes) {
            if (instance.getEndTime() > endtime) {
                endtime = instance.getEndTime();
            }
        }
        return endtime;
    }

    @Override
    public double getTotalCost() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        double tc = BillingUtil.getInstance().getTotalCosts((Collection) getLanes());
        return tc;
    }

    @Override
    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    @Override
    public String getAlgorithmName() {
        return algorithmName;
    }

    // SchedulingTask getExecutionTime
    Planner getEtPlanner() {
        return etPlanner;
    }

    // WorkflowTemplateImpl createWorkflowInstance
    void setEtPlanner(Planner etPlanner) {
        this.etPlanner = etPlanner;
    }

    @Override
    // dynamic
    public void repairExit() {
        SchedulingTask exit = getExit();
        double max = getDeadline();
        for (SchedulingTask parent : exit.getParents()) {
            max = Math.max(max, parent.getEndTime());
        }

        if (Math.abs(max - exit.getStartTime()) > Util.DOUBLE_THRESHOLD) {
            exit.setStartTime(max);
            exit.setEndTime(max);
        }
    }

    @Override
    public WorkflowInstance clone() {
        byte[] schedulingGraph = serialize();
        WorkflowInstanceImpl result = deserialize(schedulingGraph);
        return result;
    }

    private byte[] serialize() {
        ObjectOutputStream oos = null;
        try {
            // serialize
            ByteArrayOutputStream bOs = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bOs);
            oos.writeObject(this);
            byte[] ba = bOs.toByteArray();
            return ba;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // called by SchedulingTaskImpl
    Map<SchedulingTask, Double> getTSWflexCache() {
        if (TSWflexCache == null) {
            TSWflexCache = new HashMap<>(100000);
        }
        return TSWflexCache;
    }

    // called by SchedulingTaskImpl
    Map<SchedulingTask, Double> getTSDflexCache() {
        if (TSDflexCache == null) {
            TSDflexCache = new HashMap<>(10000);
        }
        return TSDflexCache;
    }

    @Override
    public void reset() {
        if (TSWflexCache != null) { // really necessary?
            TSWflexCache.clear();
        }
        if (TSDflexCache != null) { // really necessary?
            TSDflexCache.clear();
        }

        for(SchedulingTask task:tasks) {
            task.reset();
        }

        for (Lane lane : lanes) {
            lane.reset();
        }

        dummy.reset();
    }

}
