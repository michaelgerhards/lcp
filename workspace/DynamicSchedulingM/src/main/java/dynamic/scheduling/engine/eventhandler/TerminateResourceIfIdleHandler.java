package dynamic.scheduling.engine.eventhandler;

import cloud.InstanceStatus;
import dynamic.algorithm.remapper.Remapper;
import dynamic.scheduling.engine.WorkflowEngineImpl;
import reality.EventHandler;
import reality.EventType;
import reality.QueueEvent;
import dynamic.reality.QueueEventImpl;
import reality.RealResource;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;

public class TerminateResourceIfIdleHandler implements EventHandler {

    private final WorkflowEngineImpl engine;
    private final Remapper remapper;

    public TerminateResourceIfIdleHandler(WorkflowEngineImpl engine, Remapper remapper) {
        this.engine = engine;
        this.remapper = remapper;

    }

    @Override
    public void handleEvent(QueueEvent event) {
        RealResource resource = event.getResource();
        Lane instance = (Lane) resource.getInstance();

        if (instance.getStatus() == InstanceStatus.IDLING) {
            remapper.notifyIdlingResouceAtBillingRaster(event);

            // terminate resource or set notification
            SchedulingTask nextTaskToSchedule = instance.getNextTaskToSchedule();
            if (nextTaskToSchedule == null) {
                engine.getCloudManager().terminate(instance);
            } else {
                resetBillingRaster(resource);
            }
        } else if (instance.getStatus() == InstanceStatus.READY || instance.getStatus() == InstanceStatus.RUNNING) {
            resetBillingRaster(resource);
        } else if (instance.getStatus() == InstanceStatus.TERMINATED) {
            // ignore event
        } else {
            throw new RuntimeException(instance.toString());
        }

    }

    private void resetBillingRaster(RealResource resource) {
        double startTime = Time.getInstance().getActualTime();
        double billingPeriodEnd = startTime + engine.getPlan().getAtuLength();
        QueueEventImpl termianteEvt = new QueueEventImpl(billingPeriodEnd, EventType.TERMINATE_RESOURCE_IF_IDLING, resource, this);
        Time.getInstance().addEvent(termianteEvt);
    }

}
