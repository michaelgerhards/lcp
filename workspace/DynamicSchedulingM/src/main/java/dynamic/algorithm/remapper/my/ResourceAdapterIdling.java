package dynamic.algorithm.remapper.my;

import java.util.ArrayList;
import java.util.List;

import cloud.Instance;
import cloud.InstanceStatus;
import dynamic.scheduling.engine.WorkflowEngineImpl;
import reality.EventType;
import dynamic.reality.QueueEventImpl;
import dynamic.reality.RealResourceImpl;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Debug;
import statics.util.Util;

 class ResourceAdapterIdling {

	private final WorkflowEngineImpl engine;
	private final BillingUtil billingUtil;

	public ResourceAdapterIdling(WorkflowEngineImpl engine) {
		this.engine = engine;
		billingUtil = BillingUtil.getInstance(engine.getPlan().getAtuLength());
		
	}

	public void instanceIdlingAdoptTask(double actualTime) {
		if (!engine.isAdapt()) {
			return;
		}
//		List<RealResource> idlingInstances = new ArrayList<RealResource>(
//				engine.getResourceManager().getInstanceIdling());
//		for (RealResource idlingIntance : idlingInstances) {
//			Instance plannedInstance = idlingIntance.getInstance();
//			if (!(plannedInstance instanceof Lane)) {
//				continue;
//			}
//			Lane plannedLane = (Lane) plannedInstance;
//			// two cases:
//			// 1) no further tasks on plannedLane -> substitute unused capacity
//			// 2) further tasks on plannedLane -> substitute gap
//
//			List<SchedulingTask> readyTasks = engine.getTaskManager().getReady();
//			for (SchedulingTask readyTask : readyTasks) {
//				if (readyTask == engine.getPlan().getExit()) {
//					continue;
//				}
//
//				Lane laneOfReadyTask = readyTask.getLane();
//				boolean tryTake = false;
//				if (laneOfReadyTask.getStatus() == InstanceStatus.IDLING
//						|| laneOfReadyTask.getStatus() == InstanceStatus.OFFLINE
//						|| laneOfReadyTask.getStatus() == InstanceStatus.RUNNING) {
//					// take
//					tryTake = true;
//				} else if (laneOfReadyTask.getStatus() == InstanceStatus.READY
//						|| laneOfReadyTask.getStatus() == InstanceStatus.READY_TO_START) {
//					// take if not next task
//					// is next task? steal? no
//
//					SchedulingTask nextTaskToScheduleOnLaneOfReadyTask = laneOfReadyTask
//							.getNextTaskToSchedule();
//					if (readyTask != nextTaskToScheduleOnLaneOfReadyTask) {
//						tryTake = true;
//					}
//				} else if (laneOfReadyTask.getStatus() == InstanceStatus.TERMINATED) {
//					throw new RuntimeException(
//							"resource having tasks is terminated: "
//									+ laneOfReadyTask);
//				} else {
//					throw new RuntimeException("unknown status: "
//							+ laneOfReadyTask);
//				}
//
//				if (tryTake) {
//					// take task
//
//					double stNew = actualTime;
//					double exNew = readyTask.getExecutionTime(plannedInstance
//							.getInstanceSize());
//					double etNew = stNew + exNew;
//
//					double st = readyTask.getStartTime();
//					double et = readyTask.getEndTime();
//
//					if (st < actualTime) {
//						throw new RuntimeException(readyTask.toString());
//					}
//					if (et - etNew > -Util.DOUBLE_THRESHOLD) {
//						// new et smaller or equal than old et. No deadline
//						// violations
//						SchedulingTask nextTaskToSchedule = plannedLane
//								.getNextTaskToSchedule();
//						if (nextTaskToSchedule == null) {
//							// XXX performance optimization: uc must be large
//							// enough
//							double uc = billingUtil
//									.getUnusedCapacity(plannedLane
//											.getExecutionTime());
//							if (exNew < uc) {
//								// readyTask fits into own unused capacity
//
//								// XXX test
//								Debug.INSTANCE
//										.printf(WorkflowEngineImpl.DEBUG_REMAP_CHECK,
//												"substitute unused capacity: reassign %s from %s to %s%n",
//												readyTask,
//												laneOfReadyTask.toString(),
//												plannedLane.toString());
//
//								SchedulingTask prevTaskOfReadyTask = laneOfReadyTask
//										.getPrevTask(readyTask);
//
//								readyTask.setStartTime(stNew);
//								readyTask.setEndTime(etNew);
//								laneOfReadyTask.reassignTaskToEndOf(readyTask,
//										plannedLane);
//								plannedLane.taskReadyForExecution(readyTask);
//								plannedLane.enrichDependenciees(readyTask);
//								laneOfReadyTask
//										.enrichDependenciees(prevTaskOfReadyTask);
//
//								break;
//							}
//						} else {
//							double goOnWithNextTask = nextTaskToSchedule
//									.getStartTime();
//							if (etNew < goOnWithNextTask) {
//								// adopted task will end before next task will
//								// start
//								// XXX test
//								Debug.INSTANCE
//										.printf(WorkflowEngineImpl.DEBUG_REMAP_CHECK,
//												"substitute gap:  reassign %s from %s to %s%n",
//												readyTask,
//												laneOfReadyTask.toString(),
//												plannedLane.toString());
//								SchedulingTask prevTaskOfReadyTask = laneOfReadyTask
//										.getPrevTask(readyTask);
//
//								readyTask.setStartTime(stNew);
//								readyTask.setEndTime(etNew);
//
//								SchedulingTask lastCompletedTask = plannedLane
//										.getPrevTask(nextTaskToSchedule);
//
//								laneOfReadyTask.reassignTaskAfterTaskOf(
//										readyTask, lastCompletedTask,
//										plannedLane);
//
//								plannedLane.taskReadyForExecution(readyTask);
//								plannedLane.enrichDependenciees(readyTask);
//								laneOfReadyTask
//										.enrichDependenciees(prevTaskOfReadyTask);
//
//								break;
//							}
//						}
//					} else {
//						// TODO check if enough RSW is available to schedule
//						// task
//					}
//				} // try take
//			} // for
//		} // for idling resources
	}

}
