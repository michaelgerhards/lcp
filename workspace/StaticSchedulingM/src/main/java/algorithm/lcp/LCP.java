package algorithm.lcp;

import algorithm.comparator.LatestEndTimeTaskComparator;
import algorithm.misc.Solution;
import cloud.InstanceSize;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.util.CloudUtil;
import statics.util.Util;

/**
 *
 * @author mike
 */
public class LCP extends AbstractLCP {

    private static final Logger logger = Logger.getLogger("LCP");

    private final double alpha; // higher alpha means higher influence of tasks

    public LCP() {
        this(0.7);
    }

    public LCP(double alpha) {
        super(logger);
        this.alpha = alpha;
        info("alpha= " + alpha);
    }

    public LCP(Map<String, String> parameters) {
        this(Double.parseDouble(parameters.getOrDefault("alpha", "0.7")));
    }

    @Override
    public String getAlgorithmName() {
        return "Low Complexity Planning";
    }

    @Override
    public String getAlgorithmNameAbbreviation() {
        return "LCP";
    }

//    private void calcSpareTime() {
//        getWorkflow().getTasks().values().stream().forEach((task) -> {
//            calcSpareTime(task);
//        });
//    }
//    private double calcSpareTime(SchedulingTask task) {
//        if (task == getWorkflow().getExit()) {
//            task.setSpareTime(0);
//            return 0;
//        }
//
//        Set<SchedulingTask> children = task.getChildren();
//        double spare = Double.MAX_VALUE;
//        double et = task.getEndTime();
//        for (SchedulingTask child : children) {
//            double cst = child.getStartTime();
//            double diff = cst - et;
//            spare = Math.min(spare, diff);
//        }
//        task.setSpareTime(spare);
//        return spare;
//    }
    @Override
    protected void calcDeadlines() {
        // The level of each task is max[p in parents](p.level) + 1
        Map<SchedulingTask, Integer> levels = new HashMap<>(getWorkflow().getTasks().size() + 1);
        int numlevels = 0;

        for (int i = postOrder.size() - 1; i >= 0; --i) {
            SchedulingTask t = postOrder.get(i);
            int level = 0;
            for (SchedulingTask p : t.getParents()) {
                int plevel = levels.get(p);
                level = Math.max(level, plevel + 1);
            }
            levels.put(t, level);
            numlevels = Math.max(numlevels, level + 1);
        }

        final int totalTasks = getWorkflow().getTasks().size();
        int[] totalTasksByLevel = new int[numlevels]; // level of exit+1

        double totalRuntime = 0;
        double[] totalRuntimesByLevel = new double[numlevels];
        CloudUtil cloudUtil = CloudUtil.getInstance();

        for (SchedulingTask task : getWorkflow().getTasks()) {
            InstanceSize size = cloudUtil.getFastestSize(task);
            double runtime = task.getExecutionTime(size);
            int level = levels.get(task);
            totalRuntime += runtime;
            totalRuntimesByLevel[level] += runtime;
            totalTasksByLevel[level] += 1;
        }

        double[] shares = new double[numlevels];
        double criticalPathLength = getWorkflow().getExit().getEndTime();
        double wfSpare = getWorkflow().getDeadline() - criticalPathLength;
        double sum = 0.;
        for (int i = 1; i < numlevels - 1; i++) { // first and last level get nothing
            double taskPart = alpha * ((double) totalTasksByLevel[i] / (double) (totalTasks - 2));// - artificial tasks
//            System.out.printf("taskPart with alpha %.2f is %.10f%n", alpha, taskPart);
            double runtimePart = (1 - alpha) * (totalRuntimesByLevel[i] / totalRuntime);
//            System.out.printf("runtimePart with alpha %.2f is %.10f%n", alpha, runtimePart);
            shares[i] = (taskPart + runtimePart) * wfSpare;
            sum += shares[i];
        }

        if (Math.abs(sum - wfSpare) > Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException();
        }

        // from exit to entry
        for (SchedulingTask task : postOrder) {
            if (task.equals(getWorkflow().getExit())) {
                task.setLatestEndTime(getWorkflow().getDeadline());
                continue;
            }
            if (task.equals(getWorkflow().getEntry())) {
                task.setLatestEndTime(0);
                continue;
            }

            double deadline = Double.MAX_VALUE;
            for (SchedulingTask child : task.getChildren()) {
                double cdeadline = child.getLatestEndTime();
                InstanceSize csize = cloudUtil.getFastestSize(child);
                double cruntime = child.getExecutionTime(csize);
                int clevel = levels.get(child);
                double cshare = shares[clevel];
                double latestEndTime = cdeadline - cruntime - cshare;
                deadline = Math.min(deadline, latestEndTime);
            }
//            logger.info(String.format("set deadline to %.2f for task %d", deadline, task.getId()));
            task.setLatestEndTime(deadline);
        }

//        for (int i = postOrder.size() - 1; i >= 0; --i) {
//            SchedulingTask task = postOrder.get(i);
//            double latestParentDeadline = 0.0;
//            for (SchedulingTask parent : task.getParents()) {
//                double pdeadline = parent.getLatestEndTime();
//                latestParentDeadline = Math.max(latestParentDeadline, pdeadline);
//            }
//            InstanceSize size = cloudUtil.getFastestInstanceSize(task);
//            double runtime = task.getExecutionTime(size);
//            int level = levels.get(task);
//            double spare = calcSpareTime(task);
//            double deadline = latestParentDeadline + runtime + shares[level] + spare;
//            task.setLatestEndTime(deadline);
//        }
    }

    @Override
    protected void assignTasks() {
        List<SchedulingTask> schedulingOrder = postOrder;

//        logger.info("schedorder start");
//
//        postOrder.stream().forEach((task) -> {
//            logger.info(task.getId());
//        });
//        logger.info("schedorder end");
        for (SchedulingTask task : schedulingOrder) {
            if (task == getWorkflow().getEntry()) {
                assignEntry();
                continue;
            }
            if (task == getWorkflow().getExit()) {
                assingExit();
                continue;
            }
            Set<Lane> parentLanes = new HashSet<>();

            double earliestStart = getEarliestStartTime(task, parentLanes);

            Solution best = null;
            if (parentLanes.size() < 5) {
                best = getBestSolutionOnExistingResource(task, earliestStart, null, parentLanes);
            }

            if (best == null || best.cost > 0) {
                best = getBestSolutionOnExistingResource(task, earliestStart, best, getWorkflow().getLanes());
                best = getBestSolutionOnNewResource(task, earliestStart, best);
            }
            assignTaskToResource(task, best);
        } // for tasks
    }

    @Override
    protected void sortTasks() {
//        Collections.reverse(postOrder);
        postOrder = null;

        waiting.addAll(getWorkflow().getTasks());
        for (SchedulingTask task : getWorkflow().getTasks()) {
            waitingParents.put(task, new HashSet<>(task.getParents()));
        }

        goReady(getWorkflow().getEntry());
        goScheduled(getWorkflow().getEntry());
        SchedulingTask next = getWorkflow().getEntry();
        List<SchedulingTask> deciders = new ArrayList<>();
        while (!ready.isEmpty()) {
            deciders.add(next);
            for (int i = deciders.size() - 1; i >= 0; --i) {
                SchedulingTask decider = deciders.remove(i);
                next = getNextTaskToSchedule(decider);
                if (next != null) {
                    deciders.add(decider);
                    break;
                }
            }

//            next = getNextTaskToSchedule(next);
//            if (next == null) {
//                for (int i = scheduled.size() - 1; i >= 0 && next == null; --i) {
//                    SchedulingTask decider = scheduled.get(i);
//                    next = getNextTaskToSchedule(decider);
//                }
//            }
            if (next == null) {
                throw new RuntimeException();
            }
            goScheduled(next);
        }
        postOrder = scheduled;
    }

    private Set<SchedulingTask> waiting = new HashSet<>();
    private Set<SchedulingTask> ready = new HashSet<>();
    private List<SchedulingTask> scheduled = new ArrayList<>();
    private Map<SchedulingTask, Set<SchedulingTask>> waitingParents = new HashMap<>();
    protected Comparator<SchedulingTask> comp = new LatestEndTimeTaskComparator();

    private void goReady(SchedulingTask task) {
        waiting.remove(task);
        ready.add(task);
    }

    private void goScheduled(SchedulingTask task) {
//        logger.info("schedule\t" + task.getId());
        ready.remove(task);
        scheduled.add(task);
        for (SchedulingTask child : task.getChildren()) {
            Set<SchedulingTask> waitingParentsChild = waitingParents.get(child);
            waitingParentsChild.remove(task);
            if (waitingParentsChild.isEmpty()) {
                goReady(child);
            }
        }
    }

    private SchedulingTask getNextTaskToSchedule(SchedulingTask current) {
        SchedulingTask next = null;
        // TODO introduce readyChildren analog to waiting parents!
        for (SchedulingTask child : current.getChildren()) {
            if (ready.contains(child)) {
                next = betterNext(next, child);
            }
        }
//        if (next == null) {
//            // no child ready, ask neighbors including anchestors
//            for (SchedulingTask child : current.getChildren()) {
//                // XXX performance increase by ignoreing anchesotors of scheduled tasks
//                // XXX avoid dublicate checks!!!
//                for (SchedulingTask anchestor : child.getAnchestors()) {
//                    if (ready.contains(anchestor)) {
//                        next = betterNext(anchestor, child);
//                    }
//                }
//            }
//        }
        return next;
    }

    private SchedulingTask betterNext(SchedulingTask t1, SchedulingTask t2) {
        if (t1 == null) {
            return t2;
        }
        if (t2 == null) {
            return t1;
        }
        int result = comp.compare(t1, t2);
        if (result <= 0) {
            return t1;
        } else {
            return t2;
        }
    }

}
