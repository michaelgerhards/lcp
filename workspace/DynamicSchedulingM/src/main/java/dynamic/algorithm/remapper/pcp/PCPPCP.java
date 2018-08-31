package dynamic.algorithm.remapper.pcp;

import static dynamic.scheduling.engine.WorkflowEngineImpl.DEBUG_COMPLETE;
import static dynamic.scheduling.engine.WorkflowEngineImpl.DEBUG_SCHEDULE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import algorithm.pcp.CriticalPathAlgorithm;
import cloud.Instance;
import cloud.InstanceStatus;

import static cloud.InstanceStatus.*;
import dynamic.algorithm.remapper.Remapper;
import dynamic.scheduling.engine.WorkflowEngineImpl;
import reality.QueueEvent;
import reality.RealResource;
import reality.RealResourceManager;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;

import static statics.initialization.TaskStatus.*;
import statics.initialization.WorkflowInstance;
import statics.initialization.impl.Lane;
import statics.util.Debug;
import statics.util.Util;

public class PCPPCP implements Remapper {

    private WorkflowEngineImpl workflowEngineImpl;

    @Override
    public void initialize(WorkflowEngineImpl workflowEngineImpl) {
        this.workflowEngineImpl = workflowEngineImpl;
        WorkflowInstance workflow = workflowEngineImpl.getWorkflow();
        SchedulingTask entryTask = workflow.getEntry();
        double entryDelayBuffer = entryTask.updateDelayBuffer();
        Debug.INSTANCE.println(DEBUG_COMPLETE, "entryDelayBuffer= " + entryDelayBuffer);
    }

    @Override
    public void notifyIdlingResouceAtBillingRaster(QueueEvent event) {
        // nothing
    }

    @Override
    public void notifyThatDeadlineIsViolated(QueueEvent event) {
        // nothing

    }

    @Override
    public void notifyThatTaskEnded(QueueEvent event, Set<SchedulingTask> newRdyTasks) {
        boolean checkAdaptationDemand = checkAdaptationDemand(event);
        if (checkAdaptationDemand) {
            // System.out.println("PCPdyn adaptate for " +
            // event.getJob().getTask());
            reschedule(event);
        }
    }

    private void reschedule(QueueEvent event) {
        // System.out.println("reschedule start "+event);
        WorkflowInstance workflow = event.getJob().getTask().getWorkflow();

        // double now = Time.getInstance().getActualTime();
        // List<SchedulingTask> running =
        // workflowEngineImpl.getTaskManager().getRunning();
        // for (SchedulingTask t : running) {
        // if (Time.getInstance().isInPast(t.getEndTime())) {
        // // update delayed tasks
        // t.setEndTime(now);
        // }
        // }
        List<SchedulingTask> ready = workflowEngineImpl.getTaskManager().getReady();
        List<SchedulingTask> waiting = workflowEngineImpl.getTaskManager().getWaiting();
        resetTasks(ready);
        resetTasks(waiting);

        // release all lanes
        // TODO optimization
        Collection<Lane> lanes = new ArrayList<>(workflow.getLanes());
        for (Lane l : lanes) {
            if (l.getStatus() == OFFLINE || l.getStatus() == READY_TO_START) {
                throw new RuntimeException(l.toString());
                // l.deconstruct();
            } else if (l.getStatus() == InstanceStatus.READY) {
                l.refreshStatus();
            }
        }

        // start new schedule
        CriticalPathAlgorithm cp = new CriticalPathAlgorithm();
        cp.schedule(workflow);

        for (Lane l : workflow.getLanes()) {
            if (l.getStatus() == OFFLINE || l.getStatus() == IDLING) {
                SchedulingTask nextTask = l.getNextTaskToSchedule();
                if (nextTask != null && nextTask.getStatus() == TaskStatus.READY) {
                    l.taskReadyForExecution(nextTask);
                }
            }
        }

        SchedulingTask entryTask = workflow.getEntry();
        double entryDelayBuffer = entryTask.updateDelayBuffer();
        Debug.INSTANCE.println(DEBUG_COMPLETE, "entryDelayBuffer= " + entryDelayBuffer);
        // System.out.println("reschedule end "+event);
    }

    private void resetTasks(List<SchedulingTask> tasks) {
        for (SchedulingTask t : tasks) {
            Lane lane = t.getLane();
            if (lane != null) {
                // if(lane.getId().toString().equals("261")) {
                // int blu = 0;
                // int b = blu;
                // }

                if (lane.getUmodTasks().get(0) == t) {
                    lane.deconstruct();
                } else {

                    Lane extractLane = lane.extractLane(t);

                    // if(extractLane.getId().toString().equals("261")) {
                    // int blu = 0;
                    // int b = blu;
                    // }
                    extractLane.deconstruct();
                }
            }
            t.setStartTime(-1);
            t.setEndTime(-1);
            t.setLatestEndTime(-1);
        }
    }

    private boolean checkAdaptationDemand(QueueEvent event) {
        SchedulingTask task = event.getJob().getTask();
        double pet = task.getPredictedEndtime();
        double et = task.getEndTime();
        double delay = et - pet;
        double buf = task.getDelayBuffer();
        boolean adaptate;
        if (delay > Util.DOUBLE_THRESHOLD) {
            if (delay - buf > Util.DOUBLE_THRESHOLD) {
                Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_COMPLETE,
                        "task underperforming with " + delay + " buffer violated " + buf + " task= " + task);
                adaptate = true;
            } else {
                Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_COMPLETE,
                        "task underperforming with " + delay + " buffer= " + buf + " task= " + task);
                adaptate = false;
            }
        } else {
            double gain = -delay;
            if (buf + gain < -Util.DOUBLE_THRESHOLD) {
                // still negative buffer -> still not in time
                Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_COMPLETE,
                        "task overperforming with " + gain + " but not in time with buffer= " + buf + " task= " + task);
                adaptate = true;
            } else {
                adaptate = false;
            }
        }
        return adaptate;
    }

    @Override
    public void notifyThatTaskWillBeScheduled(SchedulingTask nextTaskToSchedule, RealResource resource) {
        // nothing
        double originalStartTime = nextTaskToSchedule.getStartTime();
        double diff = Time.getInstance().getActualTime() - originalStartTime;

        double buffer = nextTaskToSchedule.getDelayBuffer();
        buffer -= diff;
        nextTaskToSchedule.setDelayBuffer(buffer);

        double startTime = Time.getInstance().getActualTime();
        double sExTime = nextTaskToSchedule.getExecutionTime(resource.getInstanceSize());
        double predictedEndtime = startTime + sExTime;
        String s = String.format("schedule %10s on %5s at %10.3f pet=%10.3f buffer=%10.3f bufferinc=%10.3f",
                nextTaskToSchedule.getId(), resource.getName(), startTime, predictedEndtime, buffer, -diff);
        Debug.INSTANCE.println(DEBUG_SCHEDULE, s);

    }

}
