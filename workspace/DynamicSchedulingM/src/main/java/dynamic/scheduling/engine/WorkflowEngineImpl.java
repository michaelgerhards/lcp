package dynamic.scheduling.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import algorithm.WorkflowContainer;
import algorithm.misc.LaneSchedulingPrinter;
import cloud.BasicInstance;
import dynamic.algorithm.remapper.Remapper;
import dynamic.scheduling.engine.eventhandler.JobCompletedHandler;
import dynamic.scheduling.engine.eventhandler.TerminateResourceIfIdleHandler;
import reality.EventHandler;
import reality.QueueEvent;
import reality.RealJob;
import reality.RealResource;
import reality.Time;
import statics.initialization.SchedTaskManager;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.BillingUtil;
import statics.util.GUIVisualizer;

public final class WorkflowEngineImpl implements WorkflowContainer {

    // TODO method that sets all tasks at their earliest ST. use lane.updateTask
    // ... also see if complete / running tasks are not scheduled at east ->
    // exception!
    // mark method with XXX for performance reasons
    // sysout corrections. test lane methods for enrich / repair dependencies!!!
    public static final int DEBUG_SCHEDULE = Integer.MAX_VALUE - 3;
    public static final int DEBUG_COMPLETE = Integer.MAX_VALUE - 5;
    public static final int DEBUG_ALERT = Integer.MAX_VALUE - 10;
    public static final int DEBUG_REMAP_CHECK = 30;
    private boolean adapt = true;
    private boolean guiVis = true;
    private boolean guiStop = true;

    private final SortedSet<RealJob> allJobs = new TreeSet<>();

    private final WorkflowInstance plan;
    private final WorkflowEnginePrinter printer = new WorkflowEnginePrinter(this);
    private final SchedTaskManager taskManager;

    private final BillingUtil billingUtil;

    private final LaneSchedulingPrinter lsPrinter;
    private final PlanModifyer planModifyer;
    private CloudManager cloudManager;

    public WorkflowEngineImpl(WorkflowInstance plan) {
        this.plan = plan;
        planModifyer = new PlanModifyer(this);
        lsPrinter = new LaneSchedulingPrinter(this);
        taskManager = new SchedTaskManager(getPlan().getTasks());
        Time.stopInstance();
        Time.startInstance();
        billingUtil = BillingUtil.getInstance(plan.getAtuLength());
    }

    public void work(Remapper remapper) {
        remapper.initialize(this);
        JobCompletedHandler jobCompletedHandler = new JobCompletedHandler(this, remapper);
        TerminateResourceIfIdleHandler terminateResourceIfIdleHandler = new TerminateResourceIfIdleHandler(this, remapper);

        cloudManager = new CloudManager(this, jobCompletedHandler, terminateResourceIfIdleHandler, remapper);
        lsPrinter.printLanes();

        Time time = Time.getInstance();

        SchedulingTask entryTask = plan.getEntry();

        entryTask.readyEntry(taskManager, getCloudManager().getResourceManager());

        cloudManager.instanceReceiveScheduledTask();
        while (time.hasFurtherEvents()) {
            QueueEvent event = time.pollNextEvent();
            EventHandler handler = event.getEventHandler();
            handler.handleEvent(event);
        }
    }

    public void showGui(SchedulingTask task, String method) {
        Time time = Time.getInstance();
        String label;
        if (isGuiVis()) {
            label = method + " " + task.getId() + " " + task.getResource().getName() + String.format("  %.2f", time.getActualTime());
            GUIVisualizer.guiVisualize(plan, label);
            if (isGuiStop()) {
                GUIVisualizer.showDialog(label);
            }
        }
    }

    public WorkflowInstance getPlan() {
        return plan;
    }

    public WorkflowEnginePrinter getPrinter() {
        return printer;
    }

    public SchedTaskManager getTaskManager() {
        return taskManager;
    }

    public double getTotalCost() {
        @SuppressWarnings("rawtypes")
        Collection col1 = (Collection) getCloudManager().getResourceManager().getInstanceAll().values();
        @SuppressWarnings("unchecked")
        Collection<BasicInstance<RealResource>> col = col1;
        double tc = billingUtil.getTotalCosts(col);
        return tc;
    }

    public double getDeadline() {
        return plan.getDeadline();
    }

    public double getMakespan() {
        SortedMap<String, RealResource> instanceAll = getCloudManager().getResourceManager().getInstanceAll();
        double makespan = -1;
        for (RealResource res : instanceAll.values()) {
            if (res.getInstanceSize().isDummy()) {
                makespan = res.getEndTime();
            }
        }
        return makespan;
    }

    public BillingUtil getBillingUtil() {
        return billingUtil;
    }

    public boolean isAdapt() {
        return adapt;
    }

    public void setAdapt(boolean adapt) {
        this.adapt = adapt;
    }

    public List<RealJob> getAllJobs() {
        return new ArrayList<>(allJobs);
    }

    public boolean isGuiVis() {
        return guiVis;
    }

    public void setGuiVis(boolean guiVis) {
        this.guiVis = guiVis;
    }

    public boolean isGuiStop() {
        return guiStop;
    }

    public void setGuiStop(boolean guiStop) {
        this.guiStop = guiStop;
    }

    public LaneSchedulingPrinter getLsPrinter() {
        return lsPrinter;
    }

    public PlanModifyer getPlanModifyer() {
        return planModifyer;
    }

    public CloudManager getCloudManager() {
        return cloudManager;
    }

    @Override
    public WorkflowInstance getWorkflow() {
        return plan;
    }

}
