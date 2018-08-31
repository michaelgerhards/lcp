package dynamic.algorithm.remapper.my;

import reality.QueueEvent;
import dynamic.reality.QueueEventImpl;
import statics.initialization.SchedulingTask;
import statics.util.BillingUtil;
import statics.util.Debug;
import statics.util.Util;
import dynamic.scheduling.engine.WorkflowEngineImpl;

class DeadlineViolationPredicted {

    private final WorkflowEngineImpl engine;
    private final BillingUtil billingUtil;

    public DeadlineViolationPredicted(WorkflowEngineImpl engine) {
        this.engine = engine;
        billingUtil = BillingUtil.getInstance(engine.getPlan().getAtuLength());

    }

    public void adaptateIfDeadlineViolationIsPredicted(QueueEvent event) {

        boolean adaptate = checkAdaptationDemand(event);

        if (adaptate && engine.isAdapt()) {
            Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_COMPLETE, "TODO try to adaptate");
            // Debug.INSTANCE.aPrintln("adaptPossibleDeadlineViolation start");
            // Debug.INSTANCE.aPrintln(task.toString());
            // Debug.INSTANCE.aPrintln(task.getLane().toString());
            // String method = "adaptPossibleDeadlineViolation";
            //
            // engine.showGui(task, method);
            //
            // List<SchedulingTask> lastList = null;
            // do {
            //
            // List<SchedulingTask> list =
            // EngineUtil.printLongestPath(task);
            //
            // if (list.equals(lastList)) {
            //
            // // list equals previous run
            // Debug.INSTANCE.aPrintln("list equals previous run");
            // break;
            // } else if (list.size() > 1) {
            // boolean happened = false;
            // int toFind = 1;
            // do {
            //
            // int currentFind = 0;
            // SchedulingTask first = null; // ready task
            // // XXX performance
            // for (SchedulingTask ltask : list) {
            // if (ltask.getStatus() == TaskStatus.READY) {
            // currentFind++;
            //
            // if (currentFind == toFind) {
            // first = ltask;
            // Debug.INSTANCE.aPrintln("first Task: ", first);
            // break;
            // }
            // }
            // }
            //
            // if (first != null) {
            //
            // // does path violate dl?
            // double et = 0.;
            //
            // for (SchedulingTask t : list) {
            // et = Math.max(et, t.getEndTime());
            // }
            //
            // double diff = engine.getPlan().getDeadline() - et;
            // if (diff > Util.DOUBLE_THRESHOLD) {
            // // longest path not responsible for deadline
            // // violation
            // Debug.INSTANCE
            // .aPrintln("longest path not responsible for deadline violation");
            // break;
            // }
            //
            // // scale out
            // Lane firstLane = first.getLane();
            // Debug.INSTANCE.aPrintln("firstlane: ", firstLane);
            // SchedulingTask nextTaskToSchedule = firstLane
            // .getNextTaskToSchedule();
            // happened = false;
            // if (first != nextTaskToSchedule) {
            // if (firstLane.getStatus() == InstanceStatus.OFFLINE
            // || firstLane.getStatus() == InstanceStatus.TERMINATED) {
            // throw new RuntimeException(firstLane.toString());
            //
            // }
            // // TODO if idling, change order?
            // Debug.INSTANCE.aPrintln("first eq nextTask on ",
            // firstLane);
            // engine.scaleOut(first);
            //
            // EngineUtil.printLongestPath(task);
            //
            //
            // happened = true;
            // break;
            // } else {
            // // later task
            // if (firstLane.getStatus() == InstanceStatus.OFFLINE
            // || firstLane.getStatus() == InstanceStatus.TERMINATED
            // || firstLane.getStatus() == InstanceStatus.IDLING) {
            // throw new RuntimeException(firstLane.toString());
            // } else if (firstLane.getStatus() ==
            // InstanceStatus.READY_TO_START
            // || firstLane.getStatus() == InstanceStatus.READY) {
            // // nothing
            // } else if (firstLane.getStatus() == InstanceStatus.RUNNING) {
            // // scale out
            // engine.scaleOut(first);
            // EngineUtil.printLongestPath(task);
            // happened = true;
            // break;
            // } else {
            // throw new RuntimeException(firstLane.toString());
            // }
            //
            // }
            //
            // // if nothing happened
            // // take second, and so on
            // if (!happened) {
            // toFind++;
            // Debug.INSTANCE.aPrintln(
            // "nothing happened. increase toFin: ",
            // toFind);
            // } else {
            // break;
            // }
            // } else {
            // // no ready tasks in path
            // Debug.INSTANCE.aPrintln("no ready tasks in path: ",
            // Proxy.collectionToString(list));
            // break;
            // }
            // } while (true); // nothing happened
            //
            // // something already happened or nothing can happen
            //
            // if (!happened) {
            // // nothing happened, deadline still violated
            // Debug.INSTANCE
            // .aPrintln("nothing happened, deadline still violated");
            // double diff = engine.getPlan().getDeadline()
            // - engine.getPlan().getExit().getEndTime();
            // if (diff > Util.DOUBLE_THRESHOLD) {
            // throw new RuntimeException(
            // "Nothing happened but deadline hold ... not possible");
            // }
            // break;
            // }
            //
            // // something happened, check deadline
            // double diff = engine.getPlan().getDeadline()
            // - engine.getPlan().getExit().getEndTime();
            // if (diff < -Util.DOUBLE_THRESHOLD) {
            // // not enough, repeat
            // Debug.INSTANCE.aPrintln("not enough, repeat");
            // lastList = list;
            // } else {
            // // everything fine
            // Debug.INSTANCE.aPrintln("deadline hold");
            // break;
            // }
            //
            // } else {
            // // child of exit, no adaptations possible
            // Debug.INSTANCE
            // .aPrintln("child of exit, no adaptations possible");
            // break;
            // }
            // } while (true); // not enough
            //
            // double diff = engine.getPlan().getDeadline()
            // - engine.getPlan().getExit().getEndTime();
            // if (diff < -Util.DOUBLE_THRESHOLD) {
            // Debug.INSTANCE
            // .aPrintln("no adaptations possible to hold the makespan :(");
            // }
            // engine.showGui(task, method + " end");
            //
            // Debug.INSTANCE.aPrintln("adaptPossibleDeadlineViolation end");
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
                Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_COMPLETE, "task underperforming with ", delay, " buffer violated ", buf, " task= ", task);
                adaptate = true;
            } else {
                Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_COMPLETE, "task underperforming with ", delay, " buffer= ", buf, " task= ", task);
                adaptate = false;
            }
        } else {
            double gain = -delay;
            if (buf + gain < -Util.DOUBLE_THRESHOLD) {
                // still negative buffer -> still not in time
                Debug.INSTANCE.println(WorkflowEngineImpl.DEBUG_COMPLETE, "task overperforming with ", gain + " but not in time with buffer= ", buf, " task= ", task);
                adaptate = true;
            } else {
                adaptate = false;
            }
        }
        return adaptate;
    }

}
