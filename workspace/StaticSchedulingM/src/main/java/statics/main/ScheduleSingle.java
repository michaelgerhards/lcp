package statics.main;

import java.util.Locale;

import algorithm.StaticSchedulingAlgorithm;
import algorithm.lcp.LCP;
import statics.initialization.Planner;
import statics.initialization.WorkflowInstance;
import statics.initialization.WorkflowTemplate;
import statics.initialization.impl.Lane;
import statics.initialization.impl.MeanAlphaSigmaPlanner;
import statics.initialization.impl.WorkflowTemplateFactory;
import statics.util.BillingUtil;
import statics.util.CloudUtil;
import statics.util.Debug;
import statics.util.GUIVisualizer;

public class ScheduleSingle {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        // BarChartPrinter.noTitle = true;
        long runid = System.currentTimeMillis();
        System.out.println("start processing: " + runid);

        try {

            String cloudPath = "../../data/clouds/";
            String cloudFile = "amazon_co_cloud_60.xml";
            String executionprofilePath = "../../data/executionprofiles/";
            String executionprofileFile = "generated_profile_10000_quantile_65.xml";

            String dependencyGraphPath = "../../data/basic workflow graphs/";

//            String cloudFile = "amazon_co_cloud.xml";
            // String cloudFile = "homogen_cloud.xml";
            // String profileFile =
            // "../../data/executionprofiles/generated_profile.xml";
            final String file;
//            file = "Epigenomics_24.xml"; // 8
//            file = "Epigenomics_46.xml"; // 4
//             file = "Epigenomics_100.xml"; // 9 // 16
//             file = "Epigenomics_997.xml"; // 10
//			file = "Epigenomics_3450.xml";
//			file = "Inspiral_30.xml"; // 1
            file = "Inspiral_50.xml"; // 2
//             file = "Inspiral_100.xml"; // 4
            // file = "Inspiral_1000.xml"; // 10 //
//			file = "Sipht_30.xml"; // 1.5
            // file = "Sipht_60.xml"; // 1.3
            // file = "Sipht_100.xml"; // 1.3
            // file = "Sipht_1000.xml"; // 1.5
            // file = "Cybershake_30.xml"; // 1
            // file = "Cybershake_50.xml"; // 1
            // file = "Cybershake_100.xml"; // 1.5
            // String file = "Cybershake_1000.xml"; // 3
            //
//			file = "Montage_25.xml"; // 1/6
//            file = "Montage_50.xml"; // 1/4
//             file = "Montage_100.xml"; // 1/3
            // file = "Montage_1000.xml"; // 1/3

            // double[] deadline = new double[] { 3, 4, 9, 10, 1, 2, 4, 10, 1,
            // 1,
            // 1.5, 3 , 1.5, 1.3, 1.3, 1.5, 1./6., 1./4., 1./3., 1./3.};
            // PrintStream out = new PrintStream(new File("output_" + runid
            // + ".txt"));
            // Debug.INSTANCE.setPrintStream(out);
            GUIVisualizer.guiVis = true;
            Lane.validate = true;
            final double myDeadline = 4380.690 * 1.5;
            // myDeadline = 720;
            // myDeadline = 3600* 3;
            // myDeadline = 17899.88;
            int debug = Integer.MAX_VALUE - 2;
            // debug = Integer.MIN_VALUE;

            Debug.INSTANCE.setDebug(3);

//			Scaler scaler = new ConstantScaler(0);
            // end of configuration
            WorkflowTemplateFactory factory = new WorkflowTemplateFactory();
            factory.readCloud(cloudPath + cloudFile);
            factory.readGraph(dependencyGraphPath + file);
            factory.readHomogenRuntimeProfilePerTypeFromRuntimeProfiles(executionprofilePath + executionprofileFile);
            WorkflowTemplate template = factory.create();
            Debug.INSTANCE.aPrintln("Start processing: ", dependencyGraphPath, file);
            Planner p = new MeanAlphaSigmaPlanner(0);
            WorkflowInstance workflowInstance = template.createWorkflowInstance(myDeadline, p);
            BillingUtil.getInstance(workflowInstance.getAtuLength());
            CloudUtil.getInstance(workflowInstance.getInstanceSizes());

            StaticSchedulingAlgorithm helper = new LCP();
//            StaticSchedulingAlgorithm helper = new CriticalPathAlgorithm();
//            StaticSchedulingAlgorithm helper = new PBWS();

            reality.Time.startInstance();
            Debug.INSTANCE.aPrintln("Start Scheduling");
            long start = System.currentTimeMillis();
            workflowInstance = helper.schedule(workflowInstance);
            long end = System.currentTimeMillis();
            long duration = end - start;
            Debug.INSTANCE.aPrintln("duration: " + duration);
            Debug.INSTANCE.aPrintln("End Scheduling");

            GUIVisualizer.printResult(workflowInstance);

            // Visualizer.printWorkflowExTimes(workflow);
            // Visualizer.printWorkflowDependencies(workflow);
        } catch (Exception ex) {
            // System.out.flush();
            // ex.printStackTrace();
            ex.printStackTrace(Debug.INSTANCE.getPrintStream());
            // System.err.flush();
        }
        Debug.INSTANCE.getPrintStream().close();
        System.out.println("End program");
    }

}
