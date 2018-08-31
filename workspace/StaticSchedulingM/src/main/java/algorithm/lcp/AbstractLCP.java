package algorithm.lcp;

import algorithm.StaticSchedulingAlgorithm;
import algorithm.comparator.LatestEndTimeTaskComparator;
import algorithm.misc.Gap;
import algorithm.misc.Slot;
import algorithm.misc.Solution;
import cloud.InstanceSize;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.CloudUtil;
import statics.util.Util;

/**
 *
 * @author mike
 */
public abstract class AbstractLCP implements StaticSchedulingAlgorithm {

    private WorkflowInstance workflow;
    protected List<SchedulingTask> postOrder;
    protected final BillingUtil billingUtil;

    protected final List<InstanceSize> sortedSizes = new ArrayList<>();
    private final Map<Lane, List<Gap>> gaps = new HashMap<>(10000);
    private String indent = "\t";
    private long getBestSolutionOnExistingResource = 0L;
    private long getBestSolutionOnExistingResource2 = 0L;
    private long getBestSolutionOnExistingResourceBefore = 0L;
    private long getBestSolutionOnExistingResourceAfter = 0L;
    private long getBestSolutionOnExistingResourceInBetween = 0L;

    private int getBestSolutionOnExistingResourceCount = 0;
    private int getBestSolutionOnExistingResource2Count = 0;

    private int afterExistingAcceptCount = 0;
    private int beforeExistingAcceptCount = 0;
    private int inBetweenAcceptCount = 0;
    private int newAcceptCount = 0;

    private int afterExistingTakeCount = 0;
    private int beforeExistingTakeCount = 0;
    private int inBetweenTakeCount = 0;
    private int newTakeCount = 0;

    private int afterExistingTryCount = 0;
    private int beforeExistingTryCount = 0;
    private int inBetweenTryCount = 0;
    private int newTryCount = 0;

    private long getBestSolutionOnNewResource = 0L;
    private long assignTaskToResource = 0L;
    private long getEarliestStartTime = 0L;
    private final Logger logger;
    protected Comparator<SchedulingTask> let = new LatestEndTimeTaskComparator();

    public AbstractLCP(Logger logger) {
        this.logger = logger;
        billingUtil = BillingUtil.getInstance();
        info("");
        info("created algorithm: " + getAlgorithmName());
    }

    private static double calcEST(SchedulingTask current) {
        // EST = earliest start time
        if (current == current.getWorkflow().getEntry()) {
            current.setStartTime(0);
            current.setEndTime(0);
        } else if (current.getStartTime() >= 0) {
            // nothing, value already exists.
        } else if (current.getStartTime() < 0) {
            Set<SchedulingTask> parentNodes = current.getParents();
            // cannot be scheduled before now, planning value is 0
            double maxValue = Time.getInstance().getActualTime();
            CloudUtil cloudUtil = CloudUtil.getInstance();
            for (SchedulingTask parent : parentNodes) {
                double estParent = calcEST(parent);
                double metParent;
                if (parent.getLane() == null) {
                    metParent = cloudUtil.getFastestExecutionTime(parent);
                } else {
                    metParent = parent.getExecutionTime(parent.getLane().getInstanceSize());
                }
                double value = estParent + metParent;
                if (value > maxValue) {
                    maxValue = value;
                }
            }
            current.setStartTime(maxValue);
        }
        return current.getStartTime();
    }

    protected final void info(String s) {
        logger.info(indent + s);
    }

    protected final void indent() {
        indent += "\t";
    }

    protected final void unindent() {
        if (indent.length() == 1) {
            throw new RuntimeException();
        }
        indent = indent.substring(0, indent.length() - 1);
    }

    @Override
    public final WorkflowInstance schedule(WorkflowInstance workflow) {

//        workflow.getTasks().forEach((task) -> {
//            String descs = setToString(task.getDescendants());
//            String ancs = setToString(task.getAnchestors());
//            String children = setToString(task.getChildren());
//            String parents = setToString(task.getParents());
//            logger.info(task.getId() + "\t" + parents + "\t" + children + "\t" + ancs + "\t" + descs);
//        }
//        );

        final long sGlobal = System.currentTimeMillis();
        this.workflow = workflow;
        sortedSizes.addAll(workflow.getInstanceSizes());
        Collections.sort(sortedSizes, (InstanceSize o1, InstanceSize o2) -> {
            if (o1.isFaster(o2)) {
                return -1;
            } else if (o2.isFaster(o1)) {
                return 1;
            } else {
                return 0;
            }
        });
        sortedSizes.remove(sortedSizes.size() - 1); // remove dummy size
        workflow.setAlgorithmName(getAlgorithmNameAbbreviation());
        info("start schedule:\t" + this.workflow.getWorkflowName());
        // queue based scheduling using breadth first search possible until top order!
        long s;
        long e;
        long d;
        s = System.currentTimeMillis();
        calcEST();
        e = System.currentTimeMillis();
        d = e - s;
        info("calcEST ms:\t" + d);

        s = System.currentTimeMillis();
        calcEFT();
        e = System.currentTimeMillis();
        d = e - s;
        info("calcEFT ms:\t" + d);

        s = System.currentTimeMillis();
        calcTopOrder();
        e = System.currentTimeMillis();
        d = e - s;
        info("calcTopOrder ms:\t" + d);

        s = System.currentTimeMillis();
        calcDeadlines();
        e = System.currentTimeMillis();
        d = e - s;
        info("calcDeadlines ms:\t" + d);

        s = System.currentTimeMillis();
        sortTasks();
        e = System.currentTimeMillis();
        d = e - s;
        info("sortTasks ms:\t" + d);

//        logger.info("schedorder start");
//        postOrder.stream().forEach((task) -> {
//            logger.info(task.getId());
//        });
//        logger.info("schedorder end");

//        for (SchedulingTask t : postOrder) {
//            logger.debug(String.format("%5d\t%5d\t%10.2f\t%10.2f\t%10.2f\t%10.2f", t.getId(), t.getType(), t.getStartTime(), t.getEndTime(), t.getLatestEndTime() - (t.getEndTime() - t.getStartTime()), t.getLatestEndTime()));
//        }
        s = System.currentTimeMillis();
        assignTasks();
        e = System.currentTimeMillis();
        d = e - s;
        info("assignTasks ms:\t" + d + "\tdistributed to:");
        indent();
        info("getEarliestStartTime ms:\t" + getEarliestStartTime);
        info("getBestSolutionOnExistingResource ms:\t" + getBestSolutionOnExistingResource + "\twith count\t" + getBestSolutionOnExistingResourceCount + "\tdistributed to:");
        info("getBestSolutionOnExistingResource2 ms:\t" + getBestSolutionOnExistingResource2 + "\twith count\t" + getBestSolutionOnExistingResource2Count + "\tdistributed to:");
        indent();
        info("getBestSolutionOnExistingResource ms:\t" + getBestSolutionOnExistingResourceBefore + "\tbefore trycount:\t" + beforeExistingTryCount + "\tbefore acceptcount:\t" + beforeExistingAcceptCount + "\tbefore takecount:\t" + beforeExistingTakeCount);
        info("getBestSolutionOnExistingResource ms:\t" + getBestSolutionOnExistingResourceAfter + "\tafter trycount:\t" + afterExistingTryCount + "\tafter acceptcount:\t" + afterExistingAcceptCount + "\tafter takecount:\t" + afterExistingTakeCount);
        info("getBestSolutionOnExistingResource ms:\t" + getBestSolutionOnExistingResourceInBetween + "\tbetween trycount:\t" + inBetweenTryCount + "\tbetween acceptcount:\t" + inBetweenAcceptCount + "\tbetween takecount:\t" + inBetweenTakeCount);
        unindent();
        info("getBestSolutionOnNewResource ms:\t\t" + getBestSolutionOnNewResource + "\tnew trycount:\t" + newTryCount + "\tnew acceptcount:\t" + newAcceptCount + "\tnew takecount:\t" + newTakeCount);
        info("assignTaskToResource ms:\t" + assignTaskToResource);
        unindent();
        long eGlobal = System.currentTimeMillis();
        long dGlobal = eGlobal - sGlobal;
        info("total ms:\t" + dGlobal);
        return workflow;
    }

//    private static String setToString(Set<SchedulingTask> tasks) {
//        final StringBuilder descs = new StringBuilder();
//        descs.append("[");
//        tasks.forEach((a) -> {
//            descs.append(a.getId()).append(" ");
//        });
//        descs.append("]");
//        return descs.toString();
//    }

    @Override
    public final WorkflowInstance getWorkflow() {
        return workflow;
    }

    private void calcEST() {
        calcEST(getWorkflow().getExit());
    }

    private void calcEFT() {
        CloudUtil cloudUtil = CloudUtil.getInstance();
        // eft = earliest finish time
        for (SchedulingTask task : getWorkflow().getTasks()) {
            // only for tasks without end-time
            if (task.getEndTime() < 0) {
                double starTime = task.getStartTime();
                double endTime = starTime + cloudUtil.getFastestExecutionTime(task);
                task.setEndTime(endTime);
                if (Time.getInstance().getActualTime() == 0 && endTime - getWorkflow().getDeadline() > -Util.DOUBLE_THRESHOLD) {
                    throw new RuntimeException("invalid deadline: " + task + " et=" + endTime + " st=" + starTime + " dl=" + getWorkflow().getDeadline());
                }
            }
        }
    }

    private void calcTopOrder() {
        postOrder = new ArrayList<>(getWorkflow().getTasks().size() + 1);
        Set<SchedulingTask> marked = new HashSet<>(getWorkflow().getTasks().size() + 1);
        dfs(getWorkflow().getEntry(), marked);

//        logger.info("toporder start");
//        postOrder.stream().forEach((task) -> {
//            logger.info(task.getId());
//        });
//        logger.info("toporder end");
    }

    private void dfs(SchedulingTask task, Set<SchedulingTask> marked) {
        marked.add(task);
        task.getChildren().stream().filter((child) -> (!marked.contains(child))).forEach((child) -> {
            dfs(child, marked);
        });
        postOrder.add(task);
    }

    protected abstract void sortTasks();

//    private void sortTasks() {
//        Collections.sort(postOrder, let);
//    }
    private Solution getBestSolution(Solution a, Solution b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if (a.betterThan(b)) {
            return a;
        } else {
            return b;
        }
    }

    protected abstract void assignTasks();

    protected final Solution getBestSolutionOnExistingResource(SchedulingTask task, double earliestStart, Solution best, Collection<Lane> resources) {
        double deadline = task.getLatestEndTime();
        final long s = System.currentTimeMillis();
        // Check each resource for a better (cheaper, earlier) solution
        lanes:
        for (Lane r : resources) {
            final long s2 = System.currentTimeMillis();
            final InstanceSize size = r.getInstanceSize();
            final double runtime = task.getExecutionTime(size);

            {
                // before start
                final double ls = System.currentTimeMillis();
                beforeExistingTryCount++;
                final double earliestEnd = earliestStart + runtime;
                if (deadline - earliestEnd > -Util.DOUBLE_THRESHOLD) {
                    double gap = r.getStartTime() - earliestEnd;
                    if (gap > -Util.DOUBLE_THRESHOLD) {
                        double billingStartTime = billingUtil.getBillingStartTime(r);
                        if (earliestEnd - billingStartTime > -Util.DOUBLE_THRESHOLD) {
                            double newLifeTime = r.getEndTime() - earliestStart;
                            int newAtus = billingUtil.getUsedATUs(newLifeTime);
                            double newCost = billingUtil.getCosts(newAtus, size);
                            double oldCost = r.getCost();
                            double diffCost = newCost - oldCost;
                            Slot slot = new Slot(task, earliestStart, runtime);
                            Solution soln = new Solution(r, slot, diffCost, false, size, workflow);
                            best = getBestSolution(best, soln);
                            beforeExistingAcceptCount++;
                            if (soln == best && best.cost == 0 && gap < Util.DOUBLE_THRESHOLD) {
                                break;
                            }
                        }
                    }
                }
                double le = System.currentTimeMillis();
                double ld = le - ls;
                getBestSolutionOnExistingResourceBefore += ld;
            }
            {
                // in a gap

                final double ls = System.currentTimeMillis();
                List<Gap> currentGaps = gaps.get(r);
                if (currentGaps != null) {
                    for (Gap gap : currentGaps) {
                        inBetweenTryCount++;
                        double begin = gap.start;
                        if (earliestStart - begin > Util.DOUBLE_THRESHOLD) {
                            begin = earliestStart;
                        }
                        double end = begin + runtime;
                        if (end - deadline > Util.DOUBLE_THRESHOLD) {
                            break; // no reason to go further
                        }
                        if (end - gap.end > Util.DOUBLE_THRESHOLD) {
                            continue; // gap size too small
                        }
                        inBetweenAcceptCount++;
                        double ast = begin;
                        double cost = 0.0; // free as in beer
                        Slot sl = new Slot(task, ast, runtime, gap);
                        Solution soln = new Solution(r, sl, cost, false, size, workflow);
                        if (soln.betterThan(best)) {
                            best = soln;
                            break lanes;
                        }
                        // We won't find a better solution by looking at later
                        // gaps, so we can stop looking here
                        break;
                    }
                }
                final double le = System.currentTimeMillis();
                final double ld = le - ls;
                getBestSolutionOnExistingResourceInBetween += ld;
            }

            {
                // Try to placing it at the end of the schedule
                final double ls = System.currentTimeMillis();
                afterExistingTryCount++;
                double ast = Math.max(earliestStart, r.getEndTime());
                double aet = ast + runtime;
                if (deadline - aet > -Util.DOUBLE_THRESHOLD) {
                    double billingEndTime = billingUtil.getBillingEndTime(r);
                    if (billingEndTime - ast > -Util.DOUBLE_THRESHOLD) {
                        double newDuration = aet - r.getStartTime();
                        int newAtus = billingUtil.getUsedATUs(newDuration);
                        double newCost = billingUtil.getCosts(newAtus, size);
                        double oldCost = r.getCost();
                        double diffCost = newCost - oldCost;
                        Slot slot = new Slot(task, ast, runtime);
                        Solution soln = new Solution(r, slot, diffCost, false, size, workflow);
                        best = getBestSolution(best, soln);
                        afterExistingAcceptCount++;
                        double gap = ast - r.getEndTime();
                        if (soln == best && best.cost == 0 && gap < Util.DOUBLE_THRESHOLD) {
                            break;
                        }
                    }
                }
                double le = System.currentTimeMillis();
                double ld = le - ls;
                getBestSolutionOnExistingResourceAfter += ld;
            }

            final long e2 = System.currentTimeMillis();
            final long d2 = e2 - s2;
            getBestSolutionOnExistingResource2 += d2;
        } // for lanes
        final long e = System.currentTimeMillis();
        final long d = e - s;
        getBestSolutionOnExistingResource += d;
        getBestSolutionOnExistingResourceCount++;
        getBestSolutionOnExistingResource2Count++;
        return best;
    }

    protected final Solution getBestSolutionOnNewResource(SchedulingTask task, double earliestStart, Solution best) {
        long s = System.currentTimeMillis();
        double deadline = task.getLatestEndTime();
        for (InstanceSize size : sortedSizes) {
            newTryCount++;
            // fastest first
            // Default is to allocate a new resource
            double runtime = task.getExecutionTime(size);
            if (deadline - (earliestStart + runtime) > -Util.DOUBLE_THRESHOLD) {
                int usedATUs = billingUtil.getUsedATUs(runtime);
                double cost = billingUtil.getCosts(usedATUs, size);
                Slot sl = new Slot(task, earliestStart, runtime);
                Solution newR = new Solution(null, sl, cost, true, size, workflow);
                newAcceptCount++;
                best = getBestSolution(best, newR);
            } else {
                // fastest first, so no more possible
                break;
            }
        }
        long e = System.currentTimeMillis();
        long d = e - s;
        getBestSolutionOnNewResource += d;
        return best;
    }

    protected final void assignEntry() {
        SchedulingTask task = workflow.getEntry();
        task.setStartTime(0);
        task.setEndTime(0);
    }

    protected final void assingExit() {
        double et = Math.max(getWorkflow().getDeadline(), Time.getInstance().getActualTime());
        SchedulingTask task = workflow.getExit();
        task.setStartTime(et);
        task.setEndTime(et);
    }

    protected final void assignTaskToResource(SchedulingTask task, Solution best) throws RuntimeException {
//        logger.info(String.format("assign task %3d to %s",task.getId(), best.toString()));
        long s = System.currentTimeMillis();
        task.setStartTime(best.slot.start);
        task.setEndTime(best.slot.start + best.slot.duration);
        if (best.newresource) {
            newTakeCount++;
            Lane l = getWorkflow().instantiate(best.size);
            l.addTaskAtEnd(task);
        } else {
            Lane l = best.resource;
            if (task.getStartTime() - l.getEndTime() >= -Util.DOUBLE_THRESHOLD) {
                afterExistingTakeCount++;
                double st = l.getEndTime();
                double et = task.getStartTime();
                double gap = et - st;
                if (gap > Util.DOUBLE_THRESHOLD) {
                    List<Gap> currentGaps = gaps.get(l);
                    if (currentGaps == null) {
                        currentGaps = new LinkedList<>();
                        gaps.put(l, currentGaps);
                    }
                    Gap g = new Gap(l, st, et, l.getUmodTasks().get(l.getTasksCount() - 1), task);
                    currentGaps.add(g);
                    Collections.sort(currentGaps);
                }
                l.addTaskAtEnd(task);
            } else if (l.getStartTime() - task.getEndTime() >= -Util.DOUBLE_THRESHOLD) {
                beforeExistingTakeCount++;
                double st = task.getEndTime();
                double et = l.getStartTime();
                double gap = et - st;
                if (gap > Util.DOUBLE_THRESHOLD) {
                    List<Gap> currentGaps = gaps.get(l);
                    if (currentGaps == null) {
                        currentGaps = new LinkedList<>();
                        gaps.put(l, currentGaps);
                    }
                    Gap g = new Gap(l, st, et, task, l.getUmodTasks().get(0));
                    currentGaps.add(g);
                    Collections.sort(currentGaps);
                }
                l.addTaskAtStart(task);
            } else {
                inBetweenTakeCount++;
                Gap gap = best.slot.gap;
                SchedulingTask pred = gap.pred;
                SchedulingTask succ = gap.succ;
                double st = pred.getEndTime();
                double et = succ.getStartTime();
                l.addTaskAfterTask(task, pred);
                double newEnd = task.getStartTime();
                double newStart = task.getEndTime();
                double gapFirst = newEnd - st;
                double gapSecond = et - newStart;
                List<Gap> currentGaps = gaps.get(l);
                boolean found = currentGaps.remove(gap);
                if (!found) {
                    throw new RuntimeException("not found gap: " + gap);
                }
                if (gapFirst > Util.DOUBLE_THRESHOLD) {
                    Gap g = new Gap(l, st, newEnd, pred, task);
                    currentGaps.add(g);
                }
                if (gapSecond > Util.DOUBLE_THRESHOLD) {
                    Gap g = new Gap(l, newStart, et, task, succ);
                    currentGaps.add(g);
                }
                Collections.sort(currentGaps);
            }
        }
        long e = System.currentTimeMillis();
        long d = e - s;
        assignTaskToResource += d;
    }

    protected final double getEarliestStartTime(SchedulingTask task, Set<Lane> parentLanes) {
        long s = System.currentTimeMillis();
        // TODO log combined time
        // Compute earliest start time of task
        double earliestStart = 0.0;
        SchedulingTask entry = task.getWorkflow().getEntry();
        for (SchedulingTask parent : task.getParents()) {
            if (parent != entry) {
                Lane l = parent.getLane();
                parentLanes.add(l);
            }
            earliestStart = Math.max(earliestStart, parent.getEndTime());
        }
        long e = System.currentTimeMillis();
        long d = e - s;
        getEarliestStartTime += d;
        return earliestStart;
    }

    protected abstract void calcDeadlines();

    @Override
    public final Logger getLogger() {
        return logger;
    }

}
