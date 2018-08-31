package dynamic.algorithm.remapper.my;

import reality.QueueEvent;
import reality.RealResource;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import dynamic.scheduling.engine.WorkflowEngineImpl;

class TerminateIfIdling {

    private final WorkflowEngineImpl engine;

    public TerminateIfIdling(WorkflowEngineImpl engine) {
        this.engine = engine;
    }

    public void terminateIfIdling(QueueEvent event) {
        if (engine.isAdapt()) {
            RealResource resource = event.getResource();
            Lane instance = (Lane) resource.getInstance();

            SchedulingTask nextTaskToSchedule = instance.getNextTaskToSchedule();
            if (nextTaskToSchedule != null) {
                engine.getPlanModifyer().scaleOut(nextTaskToSchedule);
            }

        }

    }

}
