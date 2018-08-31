package statics.util;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import algorithm.Visualizer;
import barchartprinter.BarChartPrinter;
import cloud.Instance;
import cloud.InstanceSize;
import statics.initialization.Planner;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.initialization.WorkflowTemplate;
import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;
import statics.result.CheckResourceAllocationPlan;

// TODO different types of guiVisualize!!!
public class GUIVisualizer {

    private GUIVisualizer() {
        // nothing
    }

    public static boolean guiVis = true;
    private static JDialog parent = null;

    private static boolean goOn = false;
    private static int x = -10, y = -10, heigth, width;

    public static void showDialog(String title) {

        goOn = false;

        JFrame dialog = new JFrame("Click to go on");
        dialog.add(new JLabel(title));

        if (heigth < 10) {
            heigth = 100;
            x = parent.getLocation().x + parent.getWidth();
            y = parent.getLocation().y;
        }
        if (width < 400) {
            width = 400;
            x = parent.getLocation().x + parent.getWidth();
            y = parent.getLocation().y;
        }

        dialog.setLocation(x, y);
        dialog.setSize(new Dimension(width, heigth));

        dialog.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                perform();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                perform();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // nothing
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // nothing
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                perform();
            }

            private void perform() {
                goOn = true;
                x = dialog.getX();
                y = dialog.getY();
                heigth = dialog.getHeight();
                width = dialog.getWidth();
                // System.out.println(heigth);
                // System.out.println(width);
                // System.out.println(dialog.getX());
                // System.out.println(dialog.getY());
                dialog.dispose();
            }
        });

        dialog.setVisible(true);

        while (!goOn) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void guiVisualize(WorkflowInstance schedulingResult) {
        guiVisualize(schedulingResult, "Final Resource allocation "
                + schedulingResult.getAlgorithmName());
    }

    public static void guiVisualize(WorkflowInstance schedulingResult,
            String titel) {
        BarChartPrinter.labelLegendTitle = "Resource";
        Debug.INSTANCE.aPrintln("Start visualization");
        int tasks = schedulingResult.getTasks().size();
        // int serviceCount = schedulingResult.getExistingServicesCount();
        double[] est = new double[tasks];
        double[] eft = new double[est.length];
        double[] lft = new double[est.length];
        String[] labels = new String[est.length];

        est[0] = 0.;
        eft[0] = 0.;
        lft[0] = 0.;
        labels[0] = "D";

        SchedulingTask exit = schedulingResult.getExit();

        est[tasks - 1] = exit.getStartTime();
        eft[tasks - 1] = exit.getEndTime();
        lft[tasks - 1] = exit.getEndTime();
        labels[tasks - 1] = "D";

        // double max = 0.;
        int i = 1;
        for (SchedulingTask task : schedulingResult.getTasks()) {
            if (task == schedulingResult.getEntry() || task == schedulingResult.getExit()) {
                continue;
            }

            est[i] = task.getStartTime();
            eft[i] = task.getEndTime();

            Instance service = task.getResource();
            String str = service.getName() + "," + service.getInstanceSize().getName();
            labels[i] = str;

            if (service.getUmodTasks().get(service.getUmodTasks().size() - 1) == task) {
                // TODO activate to model payed time!

                lft[i] = Math.min(BillingUtil.getInstance().getBillingEndTime(service), schedulingResult.getDeadline());

                if (eft[i] > lft[i]) {
                    // required for runs exceeding deadline
                    lft[i] = BillingUtil.getInstance().getBillingEndTime(service);
                }
            } else {
                lft[i] = task.getEndTime();
            }

            // max = Math.max(max,lft[i]);
            ++i;
        }

        parent = BarChartPrinter.printBars(titel, est, eft, lft, labels);
    }

    public static void printResult(WorkflowInstance schedulingResult) {

        Visualizer visualizer = new Visualizer(schedulingResult);
        Debug.INSTANCE.setDebug(Integer.MAX_VALUE);

        WorkflowInstance workflow = schedulingResult;

        // helper.printDependenciesWithCosts();
        if (workflow.getTasks().size() < 120) {
            visualizer.printSchedulingTable();
            // visualizer.printInstancesInMin();
            visualizer.printInstancesInSec();
        }
        Debug.INSTANCE.aPrintln("-----------------------------------------");

        // visualizer.drawInstancesUntilEnd();
        // visualizer.drawInstancesUntilDeadline(); // DRAW TODO activate
        // visualizer.printDependenciesWithCosts();
        double totalCosts = schedulingResult.getTotalCost();
        double endTime = schedulingResult.getMakespan();
        int usedInstances = schedulingResult.getInstances().size();
        double deadline = schedulingResult.getDeadline();
        String algorithmName = schedulingResult.getAlgorithmName();
        Debug.INSTANCE.aPrintln("File          : " + workflow.getWorkflowName());
        Debug.INSTANCE.aPrintln("Algorithm name: " + algorithmName);
        Debug.INSTANCE.aPrintln("total costs   : " + totalCosts);
        // System.out.println("end time (m)  : " + endTime / 60);
        // System.out.println("deadline (m)  : " + deadline / 60);
        Debug.INSTANCE.aPrintln("end time (s)  : " + endTime);
        Debug.INSTANCE.aPrintln("deadline (s)  : " + deadline);

        Debug.INSTANCE.aPrintln("used instances: " + usedInstances);

        double cheapestCosts = CompareAlgorithm.getCheapest(workflow);

        // CheapestAlgorithm cheapest = new CheapestAlgorithm();
        // WorkflowInstance cheapestResult = cheapest.schedule(creator
        // .createWorkflowInstance(Double.MAX_VALUE, p));
        // double cheapestCosts = cheapestResult.getTotalCost();
        double normCosts = totalCosts / cheapestCosts;
        Debug.INSTANCE.aPrintln("cheapest costs: " + cheapestCosts);
        Debug.INSTANCE.aPrintln("norm costs    : " + normCosts);

        // algorithm.printDependenciesWithCosts();
        CheckResourceAllocationPlan cr = new CheckResourceAllocationPlan(
                schedulingResult);

        boolean check = cr.check();
        Debug.INSTANCE.aPrintln("check= " + check);

        // MeasureUtilization measure = new MeasureUtilization();
        // measure.measure(schedulingResult.getServices());
        // measure.setDeadline(deadline);
        // measure.printExcel();
//		if (workflow.getTasks().size() < 120) {
        guiVisualize(schedulingResult);
//		}
    }

    public static void guiVisualize(String titel, WorkflowInstance workflow) {
        if (Debug.INSTANCE.getDebug() > 0 && guiVis) {
            Debug.INSTANCE.aPrintln("Start GUI Visualization: "
                    + titel.replace('\n', '\t'));

            // true = localShift, false = globalShift
            boolean localShift = true;

            Map<SchedulingTask, Double> lastTasksShiftSpace = new HashMap<>();
            for (Lane lane : workflow.getLanes()) {
                double shiftSpace;
                if (localShift) {
                    shiftSpace = lane.getRSWfix();
                } else {
                    shiftSpace = lane.getRSWflex();
                }
                SchedulingTask lastTask = lane.getUmodTasks().get(
                        lane.getTasksCount() - 1);
                lastTasksShiftSpace.put(lastTask, shiftSpace);
            }

            // ignore artificial entry and exit nodes
            double[] est = new double[workflow.getTasks().size() - 2];
            double[] eft = new double[workflow.getTasks().size() - 2];
            double[] lft = new double[workflow.getTasks().size() - 2];
            String[] labels = new String[workflow.getTasks().size() - 2];
            // TODO add 1 entry for each billing period end!
            int i = 0;
            // for (int i = 0; i < est.length; ++i) {
            for (SchedulingTask task : workflow.getTasks()) {
                if (task == workflow.getEntry() || task == workflow.getExit()) {
                    continue;
                }

                double shift = 0;
                if (lastTasksShiftSpace.containsKey(task)) {
                    shift = lastTasksShiftSpace.get(task);
                }

                est[i] = task.getStartTime();
                eft[i] = task.getEndTime();
                lft[i] = task.getEndTime() + shift;
                Lane lane = task.getLane();
                LaneIndex name = lane.getId();
                InstanceSize size = lane.getInstanceSize();

                labels[i] = String.format("R%3s,%s", name.toString(),
                        size.getName());
                ++i;
            }

            printInterleavings(workflow);

            double eps = 0.001;
            BarChartPrinter.labelLegendTitle = "Resource";
            BarChartPrinter.printBars(titel, est, eft, lft, labels, eps);
            // Debug.INSTANCE.println("End GUI Visualization: " +
            // titel.replace('\n', '\t'));

        }
    }

    private static void printInterleavings(WorkflowInstance workflow) {
        boolean found = false;
        if (Debug.INSTANCE.getDebug() > 2) {
            // Debug.INSTANCE.println("start interleavings: ");
            for (Lane lane : workflow.getLanes()) {
                Collection<Tupel<SchedulingTask, SchedulingTask>> interleavings = Util
                        .getInterleavings(lane.getUmodTasks());
                if (interleavings.size() > 0) {
                    if (!found) {
                        Debug.INSTANCE.aPrintln("start interleavings: ");
                    }
                    found = true;
                    Debug.INSTANCE.aPrint("lane= " + lane.getId() + ": ");
                    for (Tupel<SchedulingTask, SchedulingTask> t : interleavings) {
                        Debug.INSTANCE.aPrint(t + " ");
                    }
                    Debug.INSTANCE.aPrintln();
                }
            }
            if (found) {
                Debug.INSTANCE.aPrintln("end interleavings");
            }
        }
    }

}
