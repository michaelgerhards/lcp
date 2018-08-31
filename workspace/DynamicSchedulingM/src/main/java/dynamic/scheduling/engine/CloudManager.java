package dynamic.scheduling.engine;

import static dynamic.scheduling.engine.WorkflowEngineImpl.DEBUG_SCHEDULE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cloud.Instance;
import cloud.InstanceSize;
import dynamic.algorithm.remapper.Remapper;
import dynamic.scheduling.engine.eventhandler.JobCompletedHandler;
import dynamic.scheduling.engine.eventhandler.TerminateResourceIfIdleHandler;
import reality.EventType;
import dynamic.reality.QueueEventImpl;
import reality.RealJob;
import reality.RealResource;
import dynamic.reality.RealResourceManagerImpl;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.Debug;
import static dynamic.scheduling.engine.WorkflowEngineImpl.*;

public class CloudManager {

    private final RealResourceManagerImpl resourceManager;
    private final WorkflowEngineImpl engine;
    private final JobCompletedHandler jobCompletedHandler;
    private final TerminateResourceIfIdleHandler terminateResourceIfIdleHandler;
    private final Remapper remapper;

    public CloudManager(WorkflowEngineImpl engine, JobCompletedHandler jobCompletedHandler,
            TerminateResourceIfIdleHandler terminateResourceIfIdleHandler, Remapper remapper) {
        this.engine = engine;
        this.jobCompletedHandler = jobCompletedHandler;
        this.terminateResourceIfIdleHandler = terminateResourceIfIdleHandler;
        this.remapper = remapper;
        resourceManager = new RealResourceManagerImpl(engine.getWorkflow());

    }

    public RealResourceManagerImpl getResourceManager() {
        return resourceManager;
    }

    public void instanceReceiveScheduledTask() {
        Collection<RealResource> instanceReady = new ArrayList<>(getResourceManager().getInstanceReady());
        for (RealResource inst : instanceReady) {
            receiveNextTask(inst);
        }

        WorkflowInstance plan = engine.getPlan();
        Time time = Time.getInstance();
        Collection<RealResource> instanceReadyToBoot = new ArrayList<>(getResourceManager().getInstanceReadyToBoot());
        for (RealResource inst : instanceReadyToBoot) {
            receiveNextTask(inst);

            if (inst.getInstanceSize() != plan.getDummyInstance().getInstanceSize()) {
                double startTime = time.getActualTime();
                double billingPeriodEnd = startTime + plan.getAtuLength();

                QueueEventImpl termianteEvt = new QueueEventImpl(billingPeriodEnd,
                        EventType.TERMINATE_RESOURCE_IF_IDLING, inst, terminateResourceIfIdleHandler);
                time.addEvent(termianteEvt);
            }
        }

    }

    private void receiveNextTask(RealResource resource) {
        Instance plannedInstance = resource.getInstance();
        SchedulingTask nextTaskToSchedule = plannedInstance.getNextTaskToSchedule();
        remapper.notifyThatTaskWillBeScheduled(nextTaskToSchedule, resource);

        nextTaskToSchedule.setStartTime(Time.getInstance().getActualTime());
        double startTime = nextTaskToSchedule.getStartTime();
        double sExTime = nextTaskToSchedule.getExecutionTime(resource.getInstanceSize());
        double predictedEndtime = startTime + sExTime;

        nextTaskToSchedule.setEndTime(predictedEndtime);
        nextTaskToSchedule.setPredictedEndtime(predictedEndtime);

        RealJob job = resource.scheduleTask(nextTaskToSchedule, startTime, jobCompletedHandler);
        List<RealJob> allJobs = engine.getAllJobs();
        allJobs.add(job);
        nextTaskToSchedule.goRunning();
    }

    public void terminate(Instance instance) {
        Time time = Time.getInstance();
        Debug.INSTANCE.printf(DEBUG_ALERT, "terminate at %10.2f: %s%n", time.getActualTime(), instance);
        instance.goTerminated(time.getActualTime());
    }
}
