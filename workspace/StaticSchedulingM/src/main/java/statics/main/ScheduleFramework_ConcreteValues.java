package statics.main;

import java.util.Locale;

import algorithm.StaticSchedulingAlgorithm;
import algorithm.lcp.LCP;
import algorithm.pbws.BreakDownCostPrinter;
import algorithm.pcp.CriticalPathAlgorithm;
import algorithm.spss.SPSS;
import java.io.File;
import java.io.PrintWriter;
import org.apache.log4j.Logger;
import reality.Time;
import statics.initialization.Planner;
import statics.initialization.WorkflowInstance;
import statics.initialization.WorkflowTemplate;
import statics.initialization.impl.Lane;
import statics.initialization.impl.MeanAlphaSigmaPlanner;
import statics.initialization.impl.WorkflowTemplateFactory;
import statics.initialization.impl.scaler.ConstantDaxPerWFScaler;
import statics.initialization.impl.scaler.DaxScaler;
import statics.result.CheckResourceAllocationPlan;
import statics.result.MeasureUtilization;
import statics.util.BillingUtil;
import statics.util.CloudUtil;
import statics.util.CompareAlgorithm;
import statics.util.Debug;
import statics.util.Util;

public class ScheduleFramework_ConcreteValues {

    public static void main(String[] args) throws Throwable {
        Locale.setDefault(Locale.ENGLISH);
        long runid = System.currentTimeMillis();
        System.out.println(runid + ScheduleFramework_ConcreteValues.class.getSimpleName());
        Logger logger = Logger.getLogger(ScheduleFramework_ConcreteValues.class);
        logger.info("start new run with runid:\t" + runid + ScheduleFramework_ConcreteValues.class.getSimpleName());

//        String dependencyGraphPath = "C:\\SVNneu\\development\\scheduling\\data\\trainingset\\10000\\";
        String dependencyGraphPath = "../../data/basic workflow graphs/";
        String cloudPath = "../../data/clouds/";
        String cloudFile = "amazon_co_cloud_60.xml";
        boolean measurePerformance = true;
        boolean measureRatio = false;
        Lane.validate = false;
        Lane.setUseCache(false, ScheduleFramework_ConcreteValues.class.getSimpleName());

        DaxScaler scalerDax = new ConstantDaxPerWFScaler(1., 4., 2., 4., 10.);

        int debug = Integer.MIN_VALUE;

//        double[] deadlineFactors = new double[]{1.01, 1.1, 1.2, 1.3, 1.4, 1.5, 1.7, 2, 2.5, 3, 3.5, 4};
        double[] deadlineFactors = new double[]{1.5, 2.5};

//                String[] files = new String[]{"Epigenomics_24.xml"};
//        String[] files = new String[]{"Epigenomics_997.xml", "Inspiral_1000.xml", "Sipht_1000.xml", "Cybershake_1000.xml", "Montage_1000.xml"};
        String[] files = new String[]{"Epigenomics_9970.xml", "Inspiral_10000.xml", "Sipht_10000.xml", "CyberShake_10000.xml", "Montage_10000.xml"};
//        String[] files = new String[]{"CyberShake_10000.xml"};
//        String[] files = new String[]{"CyberShake_10000_01.xml","CyberShake_10000_02.xml","CyberShake_10000_04.xml","CyberShake_10000_05.xml","CyberShake_10000_08.xml"};
//        String[] files = new String[]{"CyberShake_10000_00.xml","CyberShake_10000_03.xml","CyberShake_10000_05.xml","CyberShake_10000_07.xml","CyberShake_10000_09.xml"};

        BreakDownCostPrinter.pw = new PrintWriter(new File("details.txt"));
        // config end \\
        Time.startInstance();
        Debug.INSTANCE.setDebug(debug);
        Planner p = new MeanAlphaSigmaPlanner(0);
        boolean totalCheck = true;
        System.out.println("Start Program");
        System.out.println("Cloud=\t" + cloudFile);
        System.out.println();
        for (int i = 0; i < files.length; ++i) {
            String file = files[i];

            WorkflowTemplateFactory factory = new WorkflowTemplateFactory();
            factory.readCloud(cloudPath + cloudFile);
            factory.readGraph(dependencyGraphPath + file);
            factory.readHomogenRuntimeProfilePerIdFromDAX_Use_Concrete(dependencyGraphPath + file, scalerDax);

            WorkflowTemplate template = factory.create();
            factory = null; // deallocate memory

            WorkflowInstance wf = template.createWorkflowInstance(Double.MAX_VALUE, p);

            BillingUtil.getInstance(wf.getAtuLength());
            CloudUtil.getInstance(wf.getInstanceSizes());
            int taskCount = wf.getTasks().size();
            int edgeCount = wf.getEdgeCount();
            System.out.println(file + "\ttasks=\t" + taskCount + "\tedges=\t" + edgeCount);

            double minMakespan = CompareAlgorithm.getFastest(wf);
            System.out.printf("fastestTime  :\t%.3f%n", minMakespan);

            double cheapestCosts = CompareAlgorithm.getCheapest(wf);

            System.out.printf("cheapestCosts:\t%.3f%n", cheapestCosts / 1000.);
            wf = null; // deallocate memory
            if (measurePerformance) {
                System.out.printf("%10s\t%10s\t%10s\t%10s\t%10s\t|||\t%10s\t%10s\t%10s\t|||\t%10s\t%10s\t%10s\t|||\t%10s\t%n", "factor", "deadline", "PCP", "PCP", "PCP", "SPSS", "SPSS", "SPSS", "LCP", "LCP", "LCP", "CHEAPEST");
            } else {
                System.out.printf("%10s\t%10s\t%10s\t%10s\t|||\t%10s\t%10s\t%n", "factor", "deadline", "costs", "res", "costs", "res");
            }
            for (double deadlineFactor : deadlineFactors) {
                double oldCosts = -1;
                double oldTime = -1;
                double oldResources = -1;
                final double deadline = deadlineFactor * minMakespan;

                System.out.printf("%10.1f\t", deadlineFactor);
                System.out.printf("%10.2f\t", deadline / Util.SECONDS_IN_HOURS);

                StaticSchedulingAlgorithm[] algorithms = {new CriticalPathAlgorithm(), new SPSS(), new LCP()};

                for (StaticSchedulingAlgorithm algorithm : algorithms) {
                    try {
                        // algorithm.printPerformance = true; // TODO
                        WorkflowInstance instance = template.createWorkflowInstance(deadline, p);
                        System.gc();
                        long start = System.currentTimeMillis();
                        instance = algorithm.schedule(instance);
                        long end = System.currentTimeMillis();
                        long duration = end - start;

                        instance.logStatus(algorithm.getLogger());
//                        instance.logGaps(algorithm.getLogger());

                        double makespan = instance.getMakespan();
                        double cost = instance.getTotalCost();
                        printPerformance(measurePerformance, duration);
                        totalCheck &= checkDeadline(file, deadline, algorithm, makespan);

                        double normalizedCosts = cost / cheapestCosts;
                        System.out.printf("%10.3f\t", cost / 1000.);

                        MeasureUtilization mu = new MeasureUtilization();
                        mu.measure(instance.getLanes());
                        int usedInstances = mu.calcMax();
                        System.out.printf("%10d\t", usedInstances);

                        printCostRatio(measureRatio, oldCosts, normalizedCosts);
                        oldCosts = normalizedCosts;

                        printTimeRatio(measurePerformance, measureRatio, oldTime, duration);
                        oldTime = duration;

                        printResourceRatio(measureRatio, oldResources, usedInstances);
                        oldResources = usedInstances;

                        boolean check = checkResults(instance);
                        totalCheck &= check;
                    } catch (RuntimeException ex) {
                        totalCheck = false;
                        System.out.println();
                        System.out.println("EXCEPTION\t" + ex.getMessage() + "\t \t");
                        ex.printStackTrace(System.out);
                    }
                    System.out.print("|||\t");
                } // for algorithm
                System.out.printf("%10.3f", cheapestCosts / 1000);
                System.out.println();
            } // for deadline factor
            System.out.println();
            System.out.println("##################################");
        } // for file
        System.out.println("total check=" + totalCheck);
        BreakDownCostPrinter.pw.close();
    }

    private static boolean checkResults(WorkflowInstance schedulingResult) {
        CheckResourceAllocationPlan cr = new CheckResourceAllocationPlan(schedulingResult);
        boolean check = cr.check();
        if (!check) {
            System.out.print("chec= " + check);
        }
        return check;
    }

    private static void printResourceRatio(boolean measureRatio, double oldResources, int usedInstances) {
        if (oldResources > 0 && measureRatio) {
            double ratio = usedInstances / oldResources;
            System.out.printf("resratio=%8.2f\t", ratio);
        }
    }

    private static void printTimeRatio(boolean measurePerformance, boolean measureRatio, double oldTime, long duration) {
        if (oldTime >= 0 && measurePerformance && measureRatio) {
            double ratio = (double) duration / (double) oldTime;
            System.out.printf("timeratio=%8.2f\t", ratio);
        }
    }

    private static void printCostRatio(boolean measureRatio, double oldCosts, double normalizedCosts) {
        if (oldCosts > 0 && measureRatio) {
            double ratio = normalizedCosts / oldCosts;
            System.out.printf("costratio=%8.2f\t", ratio);
        }
    }

    private static void printPerformance(boolean measurePerformance, long duration) {
        if (measurePerformance) {
            System.out.print(String.format("%10d\t", duration));
        }
    }

    private static boolean checkDeadline(String file, double deadline, StaticSchedulingAlgorithm algorithm, double time) {
        if (time - deadline > Util.DOUBLE_THRESHOLD) {
            System.out.println("time over deadline");
            System.out.println("file= " + file);
            System.out.println("algo= " + algorithm.getAlgorithmName());
            System.out.println("dead= " + deadline);
            return false;
        }
        return true;
    }

}
