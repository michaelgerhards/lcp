package dynamic.scheduling.engine;

import java.util.List;

import reality.RealResource;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.initialization.impl.Lane;
import statics.util.Debug;
import statics.util.Util;
import statics.util.outputproxy.Proxy;

public class PlanModifyer {

    private final WorkflowEngineImpl engine;

    public PlanModifyer(WorkflowEngineImpl engine) {
        this.engine = engine;

    }

    public Lane scaleOut(SchedulingTask first) {
        if (engine.isAdapt()) {
            Lane firstLane = first.getLane();
            Lane newLane = firstLane.extractLane(first);

            // TODO update own start times
            // TODO update start times of successors
            Debug.INSTANCE.printf(WorkflowEngineImpl.DEBUG_REMAP_CHECK, "ADAPT task end: reassign from %s to %s: %s%n", firstLane.getName(), first.getLane().getName(), Proxy.collectionToString(newLane.getUmodTasks()));

            // TODO scale in
            if (first.getStatus() == TaskStatus.READY) {
                first.getLane().taskReadyForExecution(first);
            }
            engine.getPlan().repairExit();
            return newLane;
        }
        return null;
    }

    public Lane scaleIn(Lane newLane) {
        // scales in using first fit ignoring cost!

        List<RealResource> instanceIdling = engine.getCloudManager().getResourceManager().getInstanceIdling();
        // List<RealResource> instanceIdling = new ArrayList<RealResource>(
        // engine.getResourceManager().getInstanceIdling());
        for (RealResource targetRes : instanceIdling) {
            if (!(targetRes.getInstance() instanceof Lane)) {
                continue;
            }
            Lane targetLane = (Lane) targetRes.getInstance();
            // Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_REMAP_CHECK,
            // String.format("idling: " + targetLane.getName()));
            if (newLane.getInstanceSize() == targetLane.getInstanceSize()) {
                SchedulingTask nextTaskToSchedule = targetLane.getNextTaskToSchedule();

                if (nextTaskToSchedule != null) {
                    double startTime = nextTaskToSchedule.getStartTime();
                    if (startTime - newLane.getEndTime() > -Util.DOUBLE_THRESHOLD) {
                        // enough space!
                        Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_REMAP_CHECK, "extract lane starting with ", nextTaskToSchedule, " from ", targetLane);
                        Lane tmp = targetLane.extractLane(nextTaskToSchedule);
                        Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_REMAP_CHECK, "new Lane = ", tmp);

                        Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_REMAP_CHECK, "reassign to end from ", newLane, " to ", targetLane);
                        targetLane.reassignToEndFrom(newLane);
                        Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_REMAP_CHECK, "aggregated lane: ", targetLane);

                        Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_REMAP_CHECK, "reassign to end from ", tmp, " to ", targetLane);
                        targetLane.reassignToEndFrom(tmp);
                        Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_REMAP_CHECK, "aggregated lane: ", targetLane);
                        return targetLane;
                    }
                } else {
                    // add at end
                    engine.getLsPrinter().printLanes();

                    targetLane.toString();
                    newLane.toString();

                    targetLane.reassignToEndFrom(newLane);
                    return targetLane;
                }
            } else {
                // TODO different instance sizes
            }
        } // for
        return null;
    }

}
