package algorithm.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import algorithm.WorkflowContainer;
import algorithm.misc.aggregation.AggregationResult;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.result.MeasureUtilization;
import statics.util.Debug;
import statics.util.Duration;
import statics.util.Tupel;
import statics.util.Util;
import statics.util.outputproxy.Proxy;

public class LaneSchedulingPrinter {

    private final WorkflowContainer algorithm;

    public LaneSchedulingPrinter(WorkflowContainer algorithm) {
        this.algorithm = algorithm;
    }

    public void printGaps() {
        if (Debug.INSTANCE.getDebug() > 2) {
            double eps = 1;
            Debug.INSTANCE.aPrintln("start gaps: ");
            for (Lane lane : algorithm.getWorkflow().getLanes()) {
                Collection<Tupel<SchedulingTask, SchedulingTask>> gaps = Util
                        .getGaps(lane.getUmodTasks(), eps);
                if (gaps.size() > 0) {
                    Debug.INSTANCE.aPrint("lane= " + lane.getId() + ": ");
                    for (Tupel<SchedulingTask, SchedulingTask> t : gaps) {
                        Debug.INSTANCE.aPrint(t + " ");
                    }
                    Debug.INSTANCE.aPrintln();
                }
            }
            Debug.INSTANCE.aPrintln("end gaps");
        }
    }

    public void printLanes() {
        if (Debug.INSTANCE.getDebug() >= 3) {
            Debug.INSTANCE.aPrintln("lanes:");
            // int i = 0;

            for (Lane lane : algorithm.getWorkflow().getLanes()) {
                if (lane != null) {
                    // Debug.INSTANCE.printf("%4d %s", i, lane.toString());
                    Debug.INSTANCE.aPrintf("%s", lane.toString());
                    Debug.INSTANCE.aPrintln();
                }
                // ++i;
            }
            Debug.INSTANCE.aPrintln("---");
        }
    }

    public void printJoins(List<Lane> joins) {
        int mindebug = 0;
        Debug.INSTANCE.print(mindebug, "jointask #preds: ");
        for (Lane join : joins) {
            Debug.INSTANCE.printf(mindebug, " %4d %3d |", join.getUmodTasks()
                    .get(0), join.getUmodParents().size());
        }
        Debug.INSTANCE.println(mindebug, "");
    }

    public void printResourceDistribution() {
        if (Debug.INSTANCE.getDebug() > 10) {
            MeasureUtilization m = new MeasureUtilization();
            m.clear();
            Collection<Duration> durations = new ArrayList<>(algorithm.getWorkflow().getLanes());
            m.measure(durations);
            m.setDeadline(algorithm.getWorkflow().getDeadline());
            m.printExcel();
        }
    }

    public void printJoinShift(AggregationResult result, double throwShift,
            double catchShift) {
        Debug.INSTANCE.println(4, "joinshift: throwshift for ", throwShift,
                " path= ",
                Proxy.collectionToString(result.getThrowerLastPath()),
                " thrower= ", result.getThrower(), "catchshift for ",
                catchShift, " catcher= ", result.getCatcher());
    }

    public void printExecutionTimes(long downscale,
            long loop1AggregateNeighborsFix, long loop1AggregateNeighborsFlex,
            long loop2AggregateNeighborsFix,
            long loop2DistributeTasksToNeighborsFix,
            long loop2DistributeTasksToNeighborsFlex, long t1to1, long t1toM,
            long tMto1, long tMtoNO, long tMtoNI, long tffa, long postprocess) {

        if (Debug.INSTANCE.getDebug() >= 0) {
            Debug.INSTANCE.aPrintln("downscale time= \t" + downscale);
            Debug.INSTANCE.aPrintln("l1an fix  time= \t"
                    + loop1AggregateNeighborsFix);
            Debug.INSTANCE.aPrintln("l1an flex time= \t"
                    + loop1AggregateNeighborsFlex);
            Debug.INSTANCE.aPrintln("l2an fix  time= \t"
                    + loop2AggregateNeighborsFix);
            Debug.INSTANCE.aPrintln("l2dn fix  time= \t"
                    + loop2DistributeTasksToNeighborsFix);
            Debug.INSTANCE.aPrintln("l2dn flex time= \t"
                    + loop2DistributeTasksToNeighborsFlex);

            Debug.INSTANCE.aPrintln("1t1       time= \t" + t1to1);
            Debug.INSTANCE.aPrintln("1tM       time= \t" + t1toM);
            Debug.INSTANCE.aPrintln("Mt1       time= \t" + tMto1);
            Debug.INSTANCE.aPrintln("MtN O     time= \t" + tMtoNO);
            Debug.INSTANCE.aPrintln("MtN I     time= \t" + tMtoNI);
            Debug.INSTANCE.aPrintln("ffa       time= \t" + tffa);
            Debug.INSTANCE.aPrintln("postproc  time= \t" + postprocess);
        }

    }

    public void printCosts(double cInit, double cDownscale, double cLoop1,
            double cLoop2, double c1to1, double cMto1, double c1toM,
            double cMtoMO, double cMtoMI, double cffa, double cpp) {

        if (Debug.INSTANCE.getDebug() >= 0) {
            Debug.INSTANCE.aPrintln("init      cost= \t" + cInit);
            Debug.INSTANCE.aPrintln("downscale cost= \t" + cDownscale);
            Debug.INSTANCE.aPrintln("l1        cost= \t" + cLoop1);
            Debug.INSTANCE.aPrintln("l2        cost= \t" + cLoop2);

            Debug.INSTANCE.aPrintln("1t1       cost= \t" + c1to1);
            Debug.INSTANCE.aPrintln("Mt1       cost= \t" + cMto1);
            Debug.INSTANCE.aPrintln("1tM       cost= \t" + c1toM);
            Debug.INSTANCE.aPrintln("MtN O     cost= \t" + cMtoMO);
            Debug.INSTANCE.aPrintln("MtN I     cost= \t" + cMtoMI);
            Debug.INSTANCE.aPrintln("ffa       cost= \t" + cffa);
            Debug.INSTANCE.aPrintln("postproc  cost= \t" + cpp);
        }

    }

}
