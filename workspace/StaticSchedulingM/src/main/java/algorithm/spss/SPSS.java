package algorithm.spss;

import algorithm.lcp.AbstractLCP;
import algorithm.misc.Solution;
import cloud.InstanceSize;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.util.CloudUtil;
import statics.util.Util;

/**
 * Copyright by /
 *
 **
 * Gideon Juve <juve@usc.edu>
 * https://github.com/malawski/cloudworkflowsimulator/blob/master/src/cws/core/algorithms/SPSS.java
 *
 * @author mike
 */
public final class SPSS extends AbstractLCP {

    protected static final Logger logger = Logger.getLogger("SPSS");

    protected final double alpha;

    public SPSS() {
        this(0.7);
    }

    public SPSS(double alpha) {
        super(logger);
        this.alpha = alpha;
        info("alpha= " + alpha);
    }

    public SPSS(Map<String, String> parameters) {
        this(Double.parseDouble(parameters.getOrDefault("alpha", "0.7")));
    }

    @Override
    public String getAlgorithmName() {
        return "SPSS";
    }

    @Override
    public String getAlgorithmNameAbbreviation() {
        return "SPSS";
    }

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
            double runtimePart = (1 - alpha) * (totalRuntimesByLevel[i] / totalRuntime);
            shares[i] = (taskPart + runtimePart) * wfSpare;
            sum += shares[i];
        }

        if (Math.abs(sum - wfSpare) > Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException();
        }

        for (int i = postOrder.size() - 1; i >= 0; --i) {
            SchedulingTask task = postOrder.get(i);
            double latestParentDeadline = 0.0;
            for (SchedulingTask parent : task.getParents()) {
                double pdeadline = parent.getLatestEndTime();
                latestParentDeadline = Math.max(latestParentDeadline, pdeadline);
            }
            InstanceSize size = cloudUtil.getFastestSize(task);
            double runtime = task.getExecutionTime(size);
            int level = levels.get(task);
            double deadline = latestParentDeadline + runtime + shares[level];
            task.setLatestEndTime(deadline);
        }
    }

    @Override
    protected void assignTasks() {
        List<SchedulingTask> schedulingOrder = postOrder;
        for (SchedulingTask task : schedulingOrder) {
            if (task == getWorkflow().getEntry()) {
                assignEntry();
                continue;
            }
            if (task == getWorkflow().getExit()) {
                assingExit();
                continue;
            }
            double earliestStart = getEarliestStartTime(task, new HashSet<>());
            Solution best = null;
            best = getBestSolutionOnExistingResource(task, earliestStart, best, getWorkflow().getLanes());
            best = getBestSolutionOnNewResource(task, earliestStart, best);
            assignTaskToResource(task, best);
        } // for tasks
    }

    @Override
    protected void sortTasks() {
        Collections.sort(postOrder, let);
    }
}
