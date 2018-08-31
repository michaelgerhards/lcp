package dynamic.main.result.analyzer;

import dynamic.generated.Trace;
import dynamic.generated.Trace.Categories;
import dynamic.generated.Trace.Categories.Predictioncategory;
import dynamic.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory;
import dynamic.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Plan;
import dynamic.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run;
import dynamic.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Dynamic;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.bind.JAXB;

public class ExcelResultAnalyzer {

    public static void main(String[] args) throws Throwable {
        String inFilePath = args.length >= 2 ? args[0] : null;
        String inFileName = args.length >= 2 ? args[1] : null;
        if (inFilePath == null || inFileName == null) {
            inFileName = "out_full_epi1000_60_pcppcp";
            inFilePath = "C:/SVNneu/development/scheduling/data/dynamicOutput/currentoutput/";
        }

        System.out.println("start program with file " + inFilePath + inFileName);
//        String fileName = "out_full_epi1000_60_pcppcp";
//        String inFilePath = "C:/SVNneu/development/scheduling/data/dynamicOutput/currentoutput/" + fileName + ".xml";
//        String outFilePath = "C:/SVNneu/development/scheduling/data/dynamicOutput/currentoutput/" + fileName + ".csv";
//        final double QUARTILE = 0; // ???

        Trace trace = JAXB.unmarshal(new File(inFilePath + inFileName), Trace.class);

        PrintWriter out = new PrintWriter(new File(inFilePath + inFileName + ".csv"));

        Categories categories = trace.getCategories();
        List<Predictioncategory> predictioncategories = categories.getPredictioncategory();

        String workflow = trace.getWorkflow();
        String algorithm = trace.getAlgorithm();

        String shortworkflow = new File(workflow).getName();

        out.println("Workflowname:;" + shortworkflow);
        out.println("algorithmname:;" + algorithm);
        out.println("runid;deadline;alpha;file;makespan;cost;duration;hold;dlfactor");
        int runId = 0;
        for (Predictioncategory predictioncategory : predictioncategories) {

            double alpha = predictioncategory.getValue();
            List<Deadlinecategory> deadlinecategories = predictioncategory.getDeadlinecategories().getDeadlinecategory();
            for (Deadlinecategory deadlinecategory : deadlinecategories) {

                Plan plan = deadlinecategory.getPlan();

                // TODO planning time!!!
                out.printf("%s;%f;%f;%s;%f;%f;%f;%s;%f%n", "plan", plan.getDeadline(), alpha, workflow, plan.getPlannedmakespan(), plan.getPlannedcost(), 0., hold(plan.getPlannedmakespan(), plan.getDeadline()), deadlinecategory.getDeadlinefactor());

                List<Run> runs = deadlinecategory.getRuns().getRun();
                for (Run run : runs) {
                    String fileName = run.getExTimes();
                    Dynamic dynamic = run.getDynamic();
                    

                    double cost = dynamic.getCost();
                    double deadline = dynamic.getDeadline();
                    double duration = dynamic.getDuration();
                    double makespan = dynamic.getMakespan();
                    out.printf("%d;%f;%f;%s;%f;%f;%f;%s%n", runId++, deadline, alpha, fileName, makespan, cost, duration, hold(makespan, deadline));
                }

                out.println();
                out.println();
                out.println();
                out.println();
                out.println();
            } // for deadline category

        }

        out.close();
        System.out.println("End program");

    }

    private static String hold(double makespan, double deadline) {
        if (makespan < deadline + 0.001) {
            return "yes";
        } else {
            return "no";
        }
    }

}
