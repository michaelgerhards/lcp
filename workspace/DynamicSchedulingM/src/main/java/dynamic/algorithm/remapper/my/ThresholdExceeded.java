package dynamic.algorithm.remapper.my;

import java.util.List;

import dynamic.scheduling.engine.EngineUtil;
import dynamic.scheduling.engine.WorkflowEngineImpl;
import reality.EventType;
import reality.QueueEvent;
import dynamic.reality.QueueEventImpl;
import dynamic.reality.RealJobImpl;
import dynamic.reality.RealResourceImpl;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.initialization.impl.Lane;
import statics.util.Debug;
import statics.util.Util;
import statics.util.outputproxy.Proxy;

 class ThresholdExceeded {

	private final WorkflowEngineImpl engine;

	public ThresholdExceeded(WorkflowEngineImpl engine) {
		this.engine = engine;
	}

	public void adaptPlan_ThresholdExceeded(QueueEvent event) {
		if (engine.isAdapt()) {
//			double eventTime = event.getTime();
//			RealJob job = event.getJob();
//
//			Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_ALERT,
//					"EVENT\tTask exceeded threshold: " + job.getId() + " "
//							+ job.getType() + " on " + job.getResource()
//							+ " at " + eventTime);
//			Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_ALERT,
//					"s " + job.getStartTime());
//			Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_ALERT, "p "
//					+ job.getTask().getPredictedEndtime());
//			Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_ALERT, "t "
//					+ eventTime);
//			Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_ALERT,
//					"Task exceeded threshold end");
//
//			final String methodName = "adaptPlan_ThresholdExceeded";
//			SchedulingTask task = event.getJob().getTask();
//			
//			task.setEndTime(event.getTime());
//			engine.updateEndtimesForDelayedRunningTasks();
//			engine.updateStartTimeOfFollowersToRepairDependencies(task);
//			List<SchedulingTask> list = EngineUtil.printLongestPath(task);
//			
//			if (list.size() > 1) {
//				SchedulingTask first = null;
//				for (SchedulingTask ltask : list) {
//					if (ltask.getStatus() == TaskStatus.READY) {
//						first = ltask;
//						break;
//					}
//				}
//
//				if (first != null) {
//					engine.showGui(task, methodName);
//					// scale out
//					Lane firstLane = first.getLane();
//
//					
//					Lane newLane = engine.getPlanModifyer().scaleOut(first);
//					
////					Debug.INSTANCE.println(
////							WorkflowEngineImpl.DEBUG_REMAP_CHECK,
////							"extract from task ", first, " from ", firstLane);
////					Lane newLane = firstLane.extractLane(first);
////					Debug.INSTANCE.println(
////							WorkflowEngineImpl.DEBUG_REMAP_CHECK, "oldLane ",
////							firstLane, " and new Lane ", newLane);
//
//					newLane.scheduleAtEarliestTime();
//
//					// update start time of first and successors
//					// SchedulingTask firstParent = first.getParents().first();
//					// // randomly chosen parent
//					// engine.updateStartTimeOfFollowersToShiftBackward(firstParent);
//
//					// scale in if possible
//					Lane scaleIn = engine.getPlanModifyer().scaleIn(newLane);
//					
//					Lane targetLane = scaleIn == null? newLane : scaleIn;
//
//					// TODO optimize performance
//					Debug.INSTANCE
//							.printf(WorkflowEngineImpl.DEBUG_REMAP_CHECK,
//									"ADAPT threshold exceeded: reassign from %s to %s: %s%n",
//									firstLane.getName(), first.getLane()
//											.getName(), Proxy
//											.collectionToString(targetLane
//													.getUmodTasks()));
//
//					// print result
//					EngineUtil.printLongestPath(task);
//
//					// is enough? XXX if not, threshold should be now!
//
//					first.getLane().taskReadyForExecution(first);
//
//					// set new threshold
//					double threshold = task.calcThreshold();
//					Time.getInstance()
//							.addEvent(
//									new QueueEvent(
//											event.getJob(),
//											threshold,
//											EventType.JOB_COMPLETION_THRESHOLD_EXCEEDED));
//
//					Debug.INSTANCE.println(
//							WorkflowEngineImpl.DEBUG_REMAP_CHECK,
//							"old threshold= " + event.getTime()
//									+ " new threshold= " + threshold);
//					// System.out.println("######");
//					engine.showGui(task, methodName + " end");
//					engine.getPlan().repairExit();
//				}
//
//			}

		}

	}

	
}
