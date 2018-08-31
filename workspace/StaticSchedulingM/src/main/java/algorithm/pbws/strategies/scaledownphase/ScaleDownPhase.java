package algorithm.pbws.strategies.scaledownphase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import algorithm.Visualizer;
import algorithm.misc.ScaledPseudoLane;
import algorithm.misc.TryForwardResult;
import algorithm.misc.UnscaledParentsAndPath;
import algorithm.pbws.PBWSInit;
import cloud.InstanceSize;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.initialization.impl.SchedulingTaskList;
import statics.util.BillingUtil;
import statics.util.Debug;
import statics.util.Util;
import statics.util.outputproxy.Proxy;

public class ScaleDownPhase {

    private final PBWSInit algorithm;
    private final Map<SchedulingTask, UnscaledParentsAndPath> unscaledParents;

    public static void scaleDown(PBWSInit algorithm) {
        ScaleDownPhase scaling = new ScaleDownPhase(algorithm);
        scaling.scaleDown();
    }

    private ScaleDownPhase(PBWSInit algorithm) {
        this.algorithm = algorithm;
        unscaledParents = new HashMap<>(algorithm.getWorkflow().getTasks().size());
    }

    private boolean isScaled(SchedulingTask task) {
        if (task == algorithm.getWorkflow().getExit()
                || task == algorithm.getWorkflow().getEntry()) {
            return true;
        }

        for (SchedulingTask child : task.getChildren()) {
            Set<SchedulingTask> unscaledParentsOfChild = getUnscaledParents(child);
            return !unscaledParentsOfChild.contains(task);
        }
        throw new RuntimeException();
    }

    private void scaleDown() {
        SchedulingTask entry = algorithm.getWorkflow().getEntry();
        setScaled(entry);
        scaleDown(algorithm.getWorkflow().getExit());
    }

    private Set<SchedulingTask> getUnscaledParents(SchedulingTask child) {
        UnscaledParentsAndPath unscaledParentsAndPath = getUnscaledParentsAndPath(child);
        Set<SchedulingTask> parents = unscaledParentsAndPath.unscaledParents;
        if (parents == null) {
            parents = new HashSet<SchedulingTask>(child.getParents());
            unscaledParentsAndPath.unscaledParents = parents;
        }
        return parents;
    }

    private UnscaledParentsAndPath getUnscaledParentsAndPath(
            SchedulingTask child) {
        UnscaledParentsAndPath unscaledParentsAndPath = unscaledParents
                .get(child);
        if (unscaledParentsAndPath == null) {
            unscaledParentsAndPath = new UnscaledParentsAndPath();
            unscaledParents.put(child, unscaledParentsAndPath);
        }
        return unscaledParentsAndPath;
    }

    private void addPath(SchedulingTask task, List<SchedulingTask> path) {
        UnscaledParentsAndPath unscaledParentsAndPath = getUnscaledParentsAndPath(task);
        unscaledParentsAndPath.path = path;
    }

    private void scaleDown(SchedulingTask current) {
        // long s, e, d;
        while (hasUnscaledParent(current)) {
            List<SchedulingTask> pcp = getUnscaledCriticalPath(current);
            TryForwardResult bestResult = determineBestInstanceSize(pcp);
            applyScaleIgnoreOthers(bestResult.size, pcp,
                    bestResult.localTaskStarttimes);

            // XXX backwards?
            // for(int i = pcp.size()-1; i >= 0; --i) {
            // SchedulingTask tii = pcp.get(i);
            for (SchedulingTask tii : pcp) {
                scaleDown(tii);
            }
        }
    }

    private List<SchedulingTask> getUnscaledCriticalPath(SchedulingTask current) {
        List<SchedulingTask> pcp = new LinkedList<SchedulingTask>();
        SchedulingTask ti = current;
        SchedulingTask criticalParent;
        while ((criticalParent = getUnscaledCriticalParent(ti)) != null) {
            pcp.add(0, criticalParent);
            ti = criticalParent;
            addPath(criticalParent, pcp);
        }
        return pcp;
    }

    private SchedulingTask getUnscaledCriticalParent(SchedulingTask current) {
        Set<SchedulingTask> parents = getUnscaledParents(current);

        SchedulingTask criticalParent = null;
        double maxValue = -1;
        for (SchedulingTask parent : parents) {
            double value = parent.getEndTime();
            if (value > maxValue) {
                maxValue = value;
                criticalParent = parent;
                if (Math.abs(maxValue - current.getStartTime()) < Util.DOUBLE_THRESHOLD) {
                    // maximum found
                    return criticalParent;
                }
            }
        }
        return criticalParent;
    }

    private TryForwardResult determineBestInstanceSize(List<SchedulingTask> pcp) {
        TryForwardResult bestResult = null;
        for (InstanceSize instanceSize : algorithm.getWorkflow()
                .getInstanceSizes()) {
            TryForwardResult tryForward = tryForward(pcp, instanceSize);
            if (tryForward != null
                    && (bestResult == null || tryForward.costs < bestResult.costs)) {
                bestResult = tryForward;
            }
        }
        if (bestResult == null) {
            algorithm.getVisualizer();
            throw new RuntimeException("unable to schedule on new instance!!! "
                    + Visualizer.formatList(pcp));
        }

        return bestResult;
    }

    private TryForwardResult tryForward(List<SchedulingTask> pcp,
            InstanceSize instanceSize) {
        double taskTime = pcp.get(0).getStartTime();
        Map<SchedulingTask, Double> localTaskStarttimes = new HashMap<SchedulingTask, Double>();
        for (SchedulingTask ti : pcp) {
            taskTime = Util.calcNewTaskEst(taskTime, pcp, ti);
            localTaskStarttimes.put(ti, taskTime);
            taskTime += ti.getExecutionTime(instanceSize);
            // Lane tiLane = ti.getLane();
            double deadline = ti.getIDLflex();
            if (deadline - taskTime < -Util.DOUBLE_THRESHOLD) {
                return null;
            }
        } // for tasks in path

        double duration = localTaskStarttimes.get(pcp.get(pcp.size() - 1))
                + pcp.get(pcp.size() - 1).getExecutionTime(instanceSize)
                - localTaskStarttimes.get(pcp.get(0));

        BillingUtil bu = BillingUtil.getInstance();

        int atus = bu.getUsedATUs(duration);
        double costs = bu.getCosts(atus, instanceSize);
        TryForwardResult result = new TryForwardResult();
        result.costs = costs;
        result.localTaskStarttimes = localTaskStarttimes;
        result.size = instanceSize;
        return result;

    }

    private void applyScaleIgnoreOthers(InstanceSize instanceSize,
            List<SchedulingTask> scheduleTasks,
            Map<SchedulingTask, Double> bestLocalTaskStarttimes) {

        if (Debug.INSTANCE.getDebug() >= 1) {
            Debug.INSTANCE.println(1, "pcp= ", Proxy.collectionToString(scheduleTasks));
            List<String> lanes = new ArrayList<>(scheduleTasks.size());
            for (SchedulingTask ti : scheduleTasks) {
                lanes.add(ti.getLane().getId().toString());
            }
            Debug.INSTANCE.println(1, "lanes ", Proxy.collectionToString(lanes), "scaled to ", instanceSize);
        }

        for (SchedulingTask ti : scheduleTasks) {
            if (!isScaled(ti)) {
                setScaled(ti);
            }

            Lane laneOfTask = ti.getLane();
            InstanceSize oldSize = laneOfTask.getInstanceSize();

            if (oldSize != instanceSize) {
                SchedulingTaskList list = new SchedulingTaskList(
                        laneOfTask.getUmodTasks(), instanceSize,
                        algorithm.getWorkflow());
                ScaledPseudoLane lane = new ScaledPseudoLane(list,
                        bestLocalTaskStarttimes, false);
                laneOfTask.prepareSuccessorsForOwnVertScale(lane);
                laneOfTask.scale(lane);
            }
        }
    }

    private void setScaled(SchedulingTask ti) {
        for (SchedulingTask child : ti.getChildren()) {
            Set<SchedulingTask> set = getUnscaledParents(child);
            set.remove(ti);
        }
    }

    private boolean hasUnscaledParent(SchedulingTask current) {
        Set<SchedulingTask> set = getUnscaledParents(current);
        return !set.isEmpty();
    }

}
