package algorithm.pbws2;

import algorithm.pbws2.strategies.scaledownphase.ScaleDownPhase2;
import statics.util.Debug;
import statics.util.GUIVisualizer;

public class PBWS2 extends PBWSInit2 {

    public int debugIterationPL = 0; // combineparallellanes

    public int debugIterationJoins = 0; // shift join

    @Override
    protected void doIt() {
        long s, e, t, z; // performance measurement
        final double cInit = getWorkflow().getTotalCost();
        Debug.INSTANCE.println(1, "Start doIt");

        GUIVisualizer.guiVisualize("FASTEST Scaling for " + getWorkflow().getWorkflowName(), getWorkflow());
        printer.printResourceDistribution();
        costPrinter.println("Workflow");
        costPrinter.println(getWorkflow().getWorkflowName());
        costPrinter.println("deadline");
        costPrinter.println(Double.toString(getWorkflow().getDeadline()));
        costPrinter.println("before scaling");
        costPrinter.printTotalCostsAndResources();

        s = System.currentTimeMillis();
        ScaleDownPhase2.scaleDown(this);
        e = System.currentTimeMillis();
        final long tDownscale = e - s; // downscale
        final double cDownscale = getWorkflow().getTotalCost();

        printer.printResourceDistribution();

        printer.printLanes();
        Debug.INSTANCE.println(1, "init scaling lanes complete");
        Debug.INSTANCE.println(1, "start algorithm");

		// int shiftJoin = 0;
        // int combine = 0;
        costPrinter.println("before chunky breadth reduction");
        costPrinter.printTotalCostsAndResources();

        boolean found;
        // printJoins(getJoinBraches());
        GUIVisualizer.guiVisualize("after Initial Down-Scaling\nbefore Breadth reduction", getWorkflow());
        long loop1AggregateNeighborsFix = 0L; // performance measurement
        long loop1AggregateNeighborsFlex = 0L; // performance measurement
        // long fp = 0L;
        // long lp = 0L;
        Debug.INSTANCE.println(1, "Start loop 1");
        int loop1Cycles = 0;
        do {
            ++loop1Cycles;
            if (printPerformance) {
                // TODO remove
                System.out.println("loop 1 iteration= " + loop1Cycles);
            }
            s = System.currentTimeMillis();
            fpfix.aggregateNeighborsConsideringRSWfix(); // l1anfix
            e = System.currentTimeMillis();
            t = e - s;
            loop1AggregateNeighborsFix += t;

            s = System.currentTimeMillis();
            found = fpflex.aggregateNeighborsConsideringRSWflex();
            e = System.currentTimeMillis();
            t = e - s;
            loop1AggregateNeighborsFlex += t;
        } while (found);

        final double cLoop1 = getWorkflow().getTotalCost();

        long loop2DistributeTasksToNeighborsFix = 0L; // performance measurement
        long loop2DistributeTasksToNeighborsFlex = 0L; // performance
        // measurement
        long loop2AggregateNeighborsFix = 0L;
        costPrinter.println("before fine breadth reduction");
        costPrinter.printTotalCostsAndResources();

        Debug.INSTANCE.println(1, "Start 2nd loop: distributeTasksToNeighborsConsideringRSWflex");
        int loop2Cycles = 0;
        do {
            ++loop2Cycles;
            if (printPerformance) {
                // TODO remove
                System.out.println("loop 2 iteration= " + loop2Cycles);
            }
            s = System.currentTimeMillis();
            found = lpflex.distributeTasksToNeighborsConsideringRSWflex();
            e = System.currentTimeMillis();
            t = e - s;
            loop2DistributeTasksToNeighborsFlex += t;

            s = System.currentTimeMillis();
            fpfix.aggregateNeighborsConsideringRSWfix(); // l2anfix
            z = System.currentTimeMillis();
            lpfix.distributeTasksToNeighborsConsideringRSWfix(); // l2dnflex
            e = System.currentTimeMillis();

            t = z - s;
            loop2AggregateNeighborsFix += t;
            t = e - z;
            loop2DistributeTasksToNeighborsFix += t;
        } while (found);

        final double cLoop2 = getWorkflow().getTotalCost();

        costPrinter.println("before depth reduction");
        costPrinter.printTotalCostsAndResources();

		// getVisualizer().println(3, "start combine sequential lanes");
        printer.printLanes();

        GUIVisualizer.guiVisualize("after Breadth reduction \nbefore Depth reduction 1-to-1 relation", getWorkflow());
		// // System.out.println("after loop before 1to1");
        //

        s = System.currentTimeMillis();
        oto.combineOneToOneRelationNoGaps();
        // activate
        e = System.currentTimeMillis();
        final long t1to1 = e - s;
        final double c1to1 = getWorkflow().getTotalCost();

        printer.printLanes();
        // drawLanesUntilDeadline();
        GUIVisualizer.guiVisualize("after Depth reduction 1-to-M relation \nbefore Depth reduction M-to-1 relation",
                getWorkflow());
        //
        s = System.currentTimeMillis();
        mto.combineManyToOneRelation(); // TODO activate
        // activate
        e = System.currentTimeMillis();
        final long tMto1 = e - s;
        final double cMto1 = getWorkflow().getTotalCost();
        //
        printer.printLanes();
        // drawLanesUntilDeadline();
        GUIVisualizer.guiVisualize("after Depth reduction 1-to-1 relation \nbefore Depth reduction 1-to-M relation",                getWorkflow());
        //
        s = System.currentTimeMillis();
        otm.combineOneToManyRelation();
        e = System.currentTimeMillis();
        final long t1toM = e - s;
        final double c1toM = getWorkflow().getTotalCost();

        printer.printLanes();
        // drawLanesUntilDeadline();
        GUIVisualizer.guiVisualize(
                "after Depth reduction M-to-1 relation \nbefore Depth reduction N-to-M relation", getWorkflow());

		// CheckResourceAllocationPlan.checkPlan(getWorkflow());
        s = System.currentTimeMillis();
        mtmO.combineManyToManyOutgoingRelation();
        e = System.currentTimeMillis();
        final long tMtoNO = e - s;
        final double cMtoMO = getWorkflow().getTotalCost();

        printer.printLanes();
        // drawLanesUntilDeadline();
        GUIVisualizer.guiVisualize(
                "after Depth reduction � N-to-M O relation \nbefore Depth reduction � N-to-M I relation",
                getWorkflow());

        s = System.currentTimeMillis();
        mtmI.combineManyToManyIncomingRelation();
        e = System.currentTimeMillis();
        final long tMtoNI = e - s;
        final double cMtoMI = getWorkflow().getTotalCost();

        // gap tolerant start
        otoGapsAll.combineOneToOneRelationGapsAll();
        mtoGapsAll.combineManyToOneRelation();
        otmGapsAll.combineOneToManyRelation();
        mtmOGapsAll.combineManyToManyOutgoingRelation();
        mtmIGapsAll.combineManyToManyIncomingRelation();

		// gap tolerant end
        printer.printLanes();
        // drawLanesUntilDeadline();
        GUIVisualizer.guiVisualize("after Depth reduction � N-to-M I relation \nbefore Depth reduction � best fit",
                getWorkflow());
        // // System.out.println("after MtoM before ffa");
        s = System.currentTimeMillis();
        ffa.reviseAllocationPlanForNotDirectlyRelatedTasks();
        e = System.currentTimeMillis();
        final long tffa = e - s;
        final double cffa = getWorkflow().getTotalCost();

        printer.printLanes();
        // drawLanesUntilDeadline();
        GUIVisualizer.guiVisualize("after Depth reduction � best fit", getWorkflow());

		// costPrinter.println("before postprocessing");
        // costPrinter.printTotalCostsAndResources();
        //
        // Debug.INSTANCE.println(2, "Start postprocess");
        // s = System.currentTimeMillis();
        // boolean anyResult1;
        // boolean anyResult2;
        // do {
        // anyResult1 = false;
        // anyResult2 = false;
        // // needs much time with every input
        // anyResult1 = ppp.postprocessAtEndOfResource();
        // } while (anyResult1 || anyResult2);
        //
        // e = System.currentTimeMillis();
        // final long tpp = e - s;
        final long tpp = 0L;
        final double cpp = getWorkflow().getTotalCost();
        costPrinter.println("final");
        costPrinter.printTotalCostsAndResources();
        costPrinter.println("-----------------------------");

        Debug.INSTANCE.println(2, "end postprocess");
        Debug.INSTANCE.println(0, "loop 1 cycles= \t" + loop1Cycles);
        Debug.INSTANCE.println(0, "loop 2 cycles= \t" + loop2Cycles);
        printer.printExecutionTimes(tDownscale, loop1AggregateNeighborsFix, loop1AggregateNeighborsFlex,
                loop2AggregateNeighborsFix, loop2DistributeTasksToNeighborsFix, loop2DistributeTasksToNeighborsFlex,
                t1to1, t1toM, tMto1, tMtoNO, tMtoNI, tffa, tpp);
        printer.printCosts(cInit, cDownscale, cLoop1, cLoop2, c1to1, cMto1, c1toM, cMtoMO, cMtoMI, cffa, cpp);
        Debug.INSTANCE.println(3, "start map lanes to resources");
		// printLanes();

        // mapLanesToResources();
    }

}
