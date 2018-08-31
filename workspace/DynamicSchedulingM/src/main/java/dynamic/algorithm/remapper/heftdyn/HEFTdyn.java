package dynamic.algorithm.remapper.heftdyn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cloud.InstanceSize;
import cloud.InstanceStatus;
import dynamic.algorithm.remapper.Remapper;
import dynamic.scheduling.engine.WorkflowEngineImpl;
import reality.QueueEvent;
import reality.RealResource;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.initialization.WorkflowInstance;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.CloudUtil;
import statics.util.Util;

public class HEFTdyn implements Remapper {

    private WorkflowEngineImpl workflowEngineImpl;

    @Override
    public void initialize(WorkflowEngineImpl workflowEngineImpl) {
        this.workflowEngineImpl = workflowEngineImpl;
    }

    @Override
    public void notifyIdlingResouceAtBillingRaster(QueueEvent event) {
        // nothing, will be shot down by workflow engine

    }

    @Override
    public void notifyThatDeadlineIsViolated(QueueEvent event) {
        // nothing

    }

    @Override
    public void notifyThatTaskEnded(QueueEvent event, Set<SchedulingTask> newRdyTasks) {
        WorkflowInstance workflow = workflowEngineImpl.getPlan();
        InstanceSize fastestSize = CloudUtil.getInstance(workflow.getInstanceSizes()).getFastestSize();
        CloudUtil cloudUtil = CloudUtil.getInstance(workflow.getInstanceSizes());
        BillingUtil billingUtil = BillingUtil.getInstance(workflow.getAtuLength());
        final double now = Time.getInstance().getActualTime();

        List<SchedulingTask> sortedTasks = new ArrayList<>(newRdyTasks);
        Collections.sort(sortedTasks, (SchedulingTask o1, SchedulingTask o2) -> {
            double e1 = o1.getExecutionTime(fastestSize);
            double e2 = o2.getExecutionTime(fastestSize);

            if (e1 == e2) {
                return 0;
            } else if (e1 > e2) {
                return -1;
            } else {
                return 1;
            }
        });
        Set<RealResource> availableLanes = new HashSet<>(workflowEngineImpl.getCloudManager().getResourceManager().getInstanceIdling());

        String dummyName = workflow.getDummyInstance().getName();
        RealResource dummyResource = workflowEngineImpl.getCloudManager().getResourceManager().getInstanceAll().get(dummyName);
        availableLanes.remove(dummyResource);

//		for(RealResource r: availableLanes) {
//			Lane l = (Lane)r.getInstance();
//		if(l.getUmodTasks().getLast().getStatus() ==TaskStatus.COMPLETED) {
//			System.out.println("complete "+l);
//		} else if(l.getUmodTasks().getLast().getStatus() ==TaskStatus.READY) {
//			System.out.println("ready "+l);
//		} else if(l.getUmodTasks().getLast().getStatus() ==TaskStatus.RUNNING) {
//			System.out.println("running "+l);
//		} else if(l.getUmodTasks().getLast().getStatus() ==TaskStatus.WAITING) {
//			System.out.println("waiting "+l);
//		}  else {
//			System.out.println();
//		}
//		}
        for (SchedulingTask rdyTask : sortedTasks) {
            rdyTask.setStartTime(now);
            double exTime = rdyTask.getExecutionTime(fastestSize);
            rdyTask.setEndTime(now + exTime);

            if (rdyTask.getResource() == null) {

                RealResource best = null;
                double bestRemaining = Double.MAX_VALUE;
                for (RealResource r : availableLanes) {
                    double upTime = now - r.getStartTime();

                    double unusedCapacity = billingUtil.getUnusedCapacity(upTime);
                    while (exTime - unusedCapacity > Util.DOUBLE_THRESHOLD) {
                        unusedCapacity += workflow.getAtuLength();
                    }

                    double remaining = unusedCapacity - exTime;
                    if (remaining < bestRemaining) {
                        bestRemaining = remaining;
                        best = r;
                    }

                }

                Lane lane;
                if (best == null) {
                    lane = workflow.instantiate(fastestSize);
                } else {
                    lane = (Lane) best.getInstance();
                    availableLanes.remove(best);
                }
                lane.addTaskAtEnd(rdyTask);
                lane.updateStatus();
            }
        }

//		for (SchedulingTask rdyTask : newRdyTasks) {
//			if (rdyTask.getResource() == null) {
//				Lane lane = workflow.instantiate(fastestSize);
//				lane.addTaskAtEnd(rdyTask);
//				lane.updateStatus();
//			}
//		}
    }

    @Override
    public void notifyThatTaskWillBeScheduled(SchedulingTask nextTaskToSchedule, RealResource resource) {
        // nothing
    }

}
