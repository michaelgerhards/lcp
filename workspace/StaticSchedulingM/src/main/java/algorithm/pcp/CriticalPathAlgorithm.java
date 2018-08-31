package algorithm.pcp;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import algorithm.AbstractAlgorithm;
import algorithm.PathAlgorithm;
import algorithm.Visualizer;
import algorithm.comparator.path.NewStrategyResultComparator;
import algorithm.comparator.path.StrategyResultComparator;
import algorithm.pcp.strategy.Strategy;
import algorithm.pcp.strategy.StrategyFastestNewShifterForRuntime;
import algorithm.pcp.strategy.StrategyResult;
import algorithm.pcp.strategy.StrategyTryAfterParentShifter;
import algorithm.pcp.strategy.StrategyTryBeforeChildShifter;
import algorithm.pcp.strategy.StrategyTryBetween;
import algorithm.pcp.strategy.StrategyTryAfterEnd;
import algorithm.pcp.strategy.StrategyTryBeforeStart;
import algorithm.pcp.strategy.StrategyTryNew;
import algorithm.pcp.strategy.StrategyTryNewShifter;
import cloud.BasicInstance;
import cloud.InstanceSize;
import org.apache.log4j.Logger;
import reality.Time;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.util.Debug;
import statics.util.outputproxy.Proxy;

public class CriticalPathAlgorithm extends PathAlgorithm {

    private static final Logger pcpLogger = Logger.getLogger("pcp");
    private static final Logger pcpTimeLogger = Logger.getLogger("pcpTime");

    private final Strategy strategyNew = new StrategyTryNew(this);
    private final Strategy strategyNewShifter = new StrategyTryNewShifter(this);
    private final Strategy strategyFastest = new StrategyFastestNewShifterForRuntime(this); // only dynamic run!!!
    private final Strategy[] strategies = {new StrategyTryAfterEnd(this), new StrategyTryBeforeStart(this),
        new StrategyTryBetween(this), new StrategyTryAfterParentShifter(this), new StrategyTryBeforeChildShifter(this)};

    private final Comparator<StrategyResult> comparatorExisting = new StrategyResultComparator();
    private final Comparator<StrategyResult> comparatorNew = new NewStrategyResultComparator();

    // output times
    private long getCriticalPathTime = 0L;
    private long assignPathTime = 0L;
    private long updateDependentTasksTime = 0L;
    private long applyStrategyTime = 0L;
    private long assignPathInternTime = 0L;

    private long applyStrategyTimeInternal = 0L;
    private long applyStrategyTimeExternal = 0L;

//    private long schedulePathOnInstance
    private int getCriticalPathCount = 0;
    private int assignPathCount = 0;
    private int updateDependentTasksCount = 0;
    private int applyStrategyCount = 0;

    private final long[] strategiesTime = new long[strategies.length + 1];
    private final int[] acceptIndividualCount = new int[strategies.length + 1];
    private long strategiesTimeCombined = 0L;

    public CriticalPathAlgorithm() {
        super(pcpLogger);
    }

    @Override
    public String getAlgorithmName() {
        return "Critical Path Algorithm";
    }

    @Override
    public void scheduleIntern() {
        info("calcLFT start");
        long s = System.currentTimeMillis();
        AbstractAlgorithm.calcLFT(getWorkflow().getEntry());
        long e = System.currentTimeMillis();
        long d = e - s;
        info("calcLFT took ms:\t" + d);

        getVisualizer().printSchedulingTable();

        info("assignParents from exit start");
        s = System.currentTimeMillis();
        assignParents(getWorkflow().getExit());
        e = System.currentTimeMillis();
        d = e - s;

        info("assignParents from exit took ms:\t" + d + "\t distributed to:");
        indent();
        info("getCriticalPath cumulative took ms:\t" + getCriticalPathTime + "\twith count\t" + getCriticalPathCount);
        info("assignPath cumulative took ms:\t" + assignPathTime + "\twith count\t" + assignPathCount + "\t distributed to:");
        indent();

//        info("strategies time combined ms:\t" + strategiesTimeCombined);
        for (int i = 0; i < strategiesTime.length; ++i) {
            long strategyTime = strategiesTime[i];
            Strategy strategy = i < strategies.length ? strategies[i] : strategyNew;
            String strategyName = strategy.getName();
            int loopCount = strategy.getLoopCount();
            int acceptCount = strategy.getAccepted(); // success try (tryStrategy() != null!)
            int takeCount = strategy.getTaken(); // apply
            int acceptIndividual = acceptIndividualCount[i];
            info("strategy " + strategyName + " cumulative took ms:\t" + strategyTime + "\twith loopcount:\t" + loopCount + "\tacceptCount:\t" + acceptCount + "\tacceptIndividCount:\t" + acceptIndividual + "\ttakecount:\t" + takeCount);
        }
        info("applyStrategyTime cumulative took ms:\t" + applyStrategyTime + "\twith count\t" + applyStrategyCount + "\t distributed to:");
//        indent();
//        info("applyStrategyTimeInternal cumulative took ms:\t" + applyStrategyTimeInternal);
//        info("applyStrategyTimeExternal cumulative took ms:\t" + applyStrategyTimeExternal);
//        unindent();

        unindent();

        info("updateDependentTasks cumulative took ms:\t" + updateDependentTasksTime + "\twith count\t" + updateDependentTasksCount);
        unindent();

        double et = Math.max(getWorkflow().getDeadline(), Time.getInstance().getActualTime());
        getWorkflow().getExit().setStartTime(et);
        getWorkflow().getExit().setEndTime(et);

    }

    private int pathId = 0; // debug output

    private void assignParents(SchedulingTask current) {
        while (hasUnassignedParent(current)) {
            long s = System.currentTimeMillis();
            List<SchedulingTask> pcp = getCriticalPath(current);
            long e = System.currentTimeMillis();
            long d = e - s;
            getCriticalPathTime += d;
            getCriticalPathCount++;

            // System.out.println("pcp found: "+Arrays.toString(pcp.toArray()));
            getVisualizer().printSchedulingTableWithoutInstances();
            pathId++;
            Debug.INSTANCE.printf(1, "to schedule tasks=%-5d     %s\t%4d", pcp.size(), Proxy.collectionToString(pcp), pathId);

            s = System.currentTimeMillis();
            List<SchedulingTask> assignPath = assignPath(pcp);
            e = System.currentTimeMillis();
            d = e - s;
            assignPathTime += d;
            assignPathCount++;

            s = System.currentTimeMillis();
            updateDependentTasks(assignPath);
            e = System.currentTimeMillis();
            d = e - s;
            updateDependentTasksTime += d;
            updateDependentTasksCount++;

            getVisualizer().printSchedulingTable();
            for (SchedulingTask tii : pcp) {
                assignParents(tii);
            }
        }

    }

    @Override
    public String getAlgorithmNameAbbreviation() {
        return "PCP";
    }

    protected List<SchedulingTask> assignPath(List<SchedulingTask> pcp) {
        return assignPath(pcp, true, true);
    }

    /**
     *
     * @param pcp critical path
     * @param tryNew try a new resource
     * @param tryExisting try existing resources
     * @return list of (re)scheduled tasks. Includes at least all tasks of pcp
     */
    protected List<SchedulingTask> assignPath(List<SchedulingTask> pcp, boolean tryNew, boolean tryExisting) {
        long sGlobal = System.currentTimeMillis();
        StrategyResult globalData = null;
        boolean[] acceptStrategy = new boolean[strategiesTime.length];
        if (tryExisting) {
            for (Lane instance : getWorkflow().getLanes()) {
                int i = 0;
                for (Strategy strategy : strategies) {
                    long s = System.currentTimeMillis();
                    StrategyResult data = strategy.tryStrategy(instance, pcp);
                    getVisualizer().debugAssignPath(pcp, instance, data, strategy.getName());
                    globalData = calcBetterData(data, globalData, comparatorExisting);
                    long e = System.currentTimeMillis();
                    long d = e - s;
                    strategiesTimeCombined += d;
                    strategiesTime[i] += d;
                    acceptStrategy[i] = acceptStrategy[i] || data != null;
                    ++i;
                }
            }
        }

        if (tryNew) {
            long s = System.currentTimeMillis();

            for (InstanceSize instanceSize : getWorkflow().getInstanceSizes()) {
                BasicInstance<SchedulingTask> instance = new TestInstance(instanceSize, getWorkflow());
                StrategyResult data = strategyNew.tryStrategy(instance, pcp);
                globalData = calcBetterData(data, globalData, comparatorNew);
                acceptStrategy[acceptStrategy.length - 1] = acceptStrategy[acceptStrategy.length - 1] || data != null;
            }

            if (globalData == null && strategyNewShifter != null) {
                InstanceSize fastestInstanceSize = getCloudUtil().getFastestSize(pcp.get(0));
                BasicInstance<SchedulingTask> instance = new TestInstance(fastestInstanceSize, getWorkflow());
                StrategyResult data = strategyNewShifter.tryStrategy(instance, pcp);
                acceptStrategy[acceptStrategy.length - 1] = acceptStrategy[acceptStrategy.length - 1] || data != null;
                if (data != null) {
                    globalData = data;
                } else if (Time.getInstance().getActualTime() == 0) {
                    getVisualizer();
                    throw new RuntimeException("unable to schedule on new instance!!! " + Visualizer.formatList(pcp));
                } else {
                    // dynamic run, not just planning -> fastest
                    instance = new TestInstance(fastestInstanceSize, getWorkflow());
                    globalData = strategyFastest.tryStrategy(instance, pcp);
                }
            }
            long e = System.currentTimeMillis();
            long d = e - s;
            strategiesTimeCombined += d;
            strategiesTime[strategiesTime.length - 1] += d;

        }

        // schedule
        if (globalData == null) {
            getVisualizer();
            throw new RuntimeException("unable to schedule: " + Visualizer.formatList(pcp));
        }

        long s = System.currentTimeMillis();
        List<SchedulingTask> result = applyStrategy(globalData);
        long e = System.currentTimeMillis();
        long d = e - s;
        applyStrategyTime += d;
        applyStrategyCount++;

        long eGlobal = System.currentTimeMillis();
        long dGlobal = eGlobal - sGlobal;
        assignPathInternTime += dGlobal;

        for (int i = 0; i < acceptStrategy.length; ++i) {
            if (acceptStrategy[i]) {
                acceptIndividualCount[i]++;
            }
        }

        return result;
    }

    /**
     *
     * @param globalData
     * @return returns all (re)scheduled Tasks
     */
    private List<SchedulingTask> applyStrategy(StrategyResult globalData) {
        long s = System.currentTimeMillis();
        List<SchedulingTask> result = globalData.apply();
        long e = System.currentTimeMillis();
        long d = e - s;
        applyStrategyTimeInternal += d;
        s = System.currentTimeMillis();
        // apply calls schedulePathOnInstance
        List<SchedulingTask> scheduledTasks2 = globalData.getScheduledTasks();
        Lane instance = (Lane) globalData.getInstance();
        int index = globalData.getStartIndex();

        if (instance.getUmodTasks().isEmpty()) {
            if (!globalData.getName().equals(StrategyTryNew.NAME) && !globalData.getName().equals(StrategyTryNewShifter.NAME) && !globalData.getName().equals(StrategyFastestNewShifterForRuntime.NAME)) {
                throw new RuntimeException(globalData.getName());
            }

            // new resource
            for (SchedulingTask ti : scheduledTasks2) {
                instance.addTaskAtEnd(ti);
            }
        } else if (index == instance.getUmodTasks().size()) {
            if (!globalData.getName().equals(StrategyTryAfterEnd.NAME)) {
                throw new RuntimeException(globalData.getName());
            }
            // add at end, no dublicated tasks possible
            for (SchedulingTask ti : scheduledTasks2) {
                instance.addTaskAtEnd(ti);
            }
        } else if (index == 0) {
            if (!globalData.getName().equals(StrategyTryBeforeStart.NAME)) {
                throw new RuntimeException(globalData.getName());
            }
            // add at start but old tasks exist
//            Lane l = getWorkflow().instantiate(instance.getInstanceSize());
//            SchedulingTask firstOnOldLane = instance.getUmodTasks().get(0);

            for (int i = scheduledTasks2.size() - 1; i >= 0; --i) {
                SchedulingTask ti = scheduledTasks2.get(i);
//                if (ti == firstOnOldLane) {
//                    throw new RuntimeException("should not be reachable");
//                    break;
//                } else {
//                    l.addTaskAtEnd(ti);
//                }

                instance.addTaskAtStart(ti);
            }
//            instance.reassignToStartFrom(l);
        } else {

            // index in between
//            Lane l = getWorkflow().instantiate(instance.getInstanceSize());
            SchedulingTask pred = instance.getUmodTasks().get(index - 1);
            SchedulingTask firstOnOld = instance.getUmodTasks().get(index);
            for (SchedulingTask ti : scheduledTasks2) {
                if (ti == firstOnOld) {
                    break;
                }
                instance.addTaskAfterTask(ti, pred);
                pred = ti;
            }
//            Lane last2 = instance.extractLane(index);
//            instance.reassignToEndFrom(l);
//            instance.reassignToEndFrom(last2);

        }
        Debug.INSTANCE.println(2, globalData);
        e = System.currentTimeMillis();
        d = e - s;
        applyStrategyTimeExternal += d;
        return result;
    }

    protected StrategyResult calcBetterData(StrategyResult data, StrategyResult globalData, Comparator<StrategyResult> comparator) {
        int compare = comparator.compare(data, globalData);
        if (compare < 0) {
            return data;
        } else {
            return globalData;
        }
    }

    public void schedulePathOnInstance(List<SchedulingTask> scheduleTasks, BasicInstance<SchedulingTask> roInstance, Map<SchedulingTask, Double> taskStarttimes) {

        // update!
        for (SchedulingTask ti : scheduleTasks) {
            // actual start time
            double est = taskStarttimes.get(ti);
            ti.setStartTime(est);
            // actual end time
            double et = ti.getExecutionTime(roInstance.getInstanceSize());
            double eft = est + et;
            ti.setEndTime(eft);
        }

        // TODO performance!!!
//        for (Lane l : getWorkflow().getLanes().values()) {
//            if (l != null && getWorkflow().existsLane(l)) {
//                l.invalidateRsw();
//            }
//        }
        for (int i = scheduleTasks.size() - 2; i >= 0; --i) {
            SchedulingTask ti = scheduleTasks.get(i);
            SchedulingTask childTi = scheduleTasks.get(i + 1);
            ti.setLatestEndTime(childTi.getLatestEndTime() - childTi.getExecutionTime(roInstance.getInstanceSize()));
        }
    }

    private int getIndexInsert(List<SchedulingTask> scheduleTasks, Map<SchedulingTask, Double> taskStarttimes, Lane instance) {
        int indexInsert = instance.getUmodTasks().size();

        // duplicates: before child or after parent
        for (SchedulingTask ti : scheduleTasks) {
            int index = instance.getUmodTasks().indexOf(ti);
            if (index >= 0) {
                // print = true;
                indexInsert = index;
                break;
            }
        }

        // insert before start
        if (!instance.getUmodTasks().isEmpty()) {
            SchedulingTask lastScheduleTask = scheduleTasks.get(scheduleTasks.size() - 1);
            Double lastScheduleTaskST = taskStarttimes.get(lastScheduleTask);

            SchedulingTask firstInstanceTask = instance.getUmodTasks().get(0);
            Double firstInstanceTaskST = taskStarttimes.get(firstInstanceTask);
            if (firstInstanceTaskST == null) {
                firstInstanceTaskST = firstInstanceTask.getStartTime();
            }

            if (lastScheduleTaskST < firstInstanceTaskST) {
                indexInsert = 0;
            }
        }
        return indexInsert;
    }

}
