package dynamic.main;

import java.io.PrintStream;

import algorithm.StaticSchedulingAlgorithm;
import algorithm.lcp.LCP;
import algorithm.spss.SPSS;
import algorithm.pbws.PBWS;
import algorithm.pcp.CriticalPathAlgorithm;
import cloud.Cloud;
import dynamic.algorithm.remapper.NoRemapper;
import dynamic.algorithm.remapper.Remapper;
import dynamic.algorithm.remapper.heftdyn.HEFTdyn;
import dynamic.algorithm.remapper.heftdyn.HEFTinit;
import dynamic.algorithm.remapper.my.MyRemapper;
import dynamic.algorithm.remapper.my.RestartRemapper;
import dynamic.algorithm.remapper.pcp.PCPPCP;
import java.util.HashMap;
import java.util.Map;
import reality.Time;
import statics.initialization.DependencyGraph;
import statics.initialization.WorkflowInstance;
import statics.initialization.WorkflowTemplate;
import statics.initialization.impl.WorkflowTemplateFactory;
import statics.result.CheckResourceAllocationPlan;

public class MainHelper {

    private MainHelper() {
        // static class
    }

    public static StaticSchedulingAlgorithm createStaticAlgorithm(String algName) {
        return createStaticAlgorithm(algName, new HashMap<>());
    }

    public static StaticSchedulingAlgorithm createStaticAlgorithm(String algName, Map<String, String> parameters) {
        StaticSchedulingAlgorithm alg;
        if (algName.equals("PBWS") || algName.equals("PBWSdyn")) {
            alg = new PBWS();
            ((PBWS) alg).printPerformance = false;
        } else if (algName.equals("PCP") || algName.equals("PCPdyn") || algName.equals("PCPPCP")) {
            alg = new CriticalPathAlgorithm();
        } else if (algName.equals("HEFTdyn")) {
            alg = new HEFTinit();
        } else if (algName.equalsIgnoreCase("LCP")) {
            alg = new LCP(parameters);
        } else if (algName.equalsIgnoreCase("SPSS")) {
            alg = new SPSS(parameters);
        } else {
            throw new RuntimeException("Unsupported Algorithmname: " + algName + " Supported names are (case sensitive): TODO");
        }
        return alg;
    }

    public static Remapper createRemapperAlgorithm(String algName) {
        return createRemapperAlgorithm(algName, new HashMap<>());
    }

    public static Remapper createRemapperAlgorithm(String algName, Map<String, String> parameters) {
        Remapper alg;
        if (algName.equals("PBWSdyn")) {
            alg = new MyRemapper();
        } else if (algName.equals("PCPdyn")) {
            alg = new MyRemapper();
        } else if (algName.equals("PCP")) {
            alg = new NoRemapper();
//            alg = new RestartRemapper();
        } else if (algName.equals("PCP2")) {
            alg = new NoRemapper();
        } else if (algName.equals("PBWS")) {
            alg = new NoRemapper();
        } else if (algName.equals("HEFTdyn")) {
            alg = new HEFTdyn();
        } else if (algName.equals("LCP")) {
            alg = new NoRemapper();
//            alg = new RestartRemapper();
        } else if (algName.equals("LCPdyn")) {
            alg = new MyRemapper();
        } else if (algName.equals("PCPPCP")) {
            return new PCPPCP();
        } else if (algName.equalsIgnoreCase("SPSS")) {
            alg = new NoRemapper();
//            alg = new RestartRemapper();
        } else {
            throw new RuntimeException("Unsupported Algorithmname: " + algName + " Supported names are (case sensitive): TODO");
        }
        return alg;
    }

    public static WorkflowTemplate createTemplate(Cloud cloud, String depFile, String profFile, PrintStream out) {
        WorkflowTemplateFactory factory = new WorkflowTemplateFactory();
        factory.setCloud(cloud);
        DependencyGraph graph = factory.readGraph(depFile);

        factory.readHomogenRuntimeProfilePerTypeFromRuntimeProfiles(profFile);

        out.println(factory);
        WorkflowTemplate template = factory.create();
        return template;
    }

    public static void checkPlan(WorkflowInstance plan) {
        MainHelper.checkPlan(plan, true);
    }

    public static void checkPlan(WorkflowInstance plan, boolean checkDeadline) {
        CheckResourceAllocationPlan checker = new CheckResourceAllocationPlan(plan);
        checker.setCheckDeadline(checkDeadline);
        boolean check = checker.check();
        if (!check) {
            throw new RuntimeException("invalid resource allocation plan!!!");
        }
    }

    public static WorkflowInstance createPlan(WorkflowInstance workflowinstancew, StaticSchedulingAlgorithm alg) {
        Time.stopInstance();
        Time.startInstance();
        workflowinstancew = alg.schedule(workflowinstancew);
        Time.stopInstance();
        // not possible using pure dynamic scheduling
        // checkPlan(workflowinstancew, out);
        return workflowinstancew;
    }

}
