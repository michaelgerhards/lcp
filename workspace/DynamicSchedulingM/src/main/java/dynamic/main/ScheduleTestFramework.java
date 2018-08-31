package dynamic.main;

import static dynamic.main.config.StartParameters.copyOperation;
import static dynamic.main.config.StartParameters.moveOperation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXB;

import reality.executiontimes.DAXExecutionTimes;
import reality.executiontimes.JobExecutionTimeManager;
import statics.initialization.DependencyGraph;
import statics.initialization.Planner;
import statics.initialization.WorkflowInstance;
import statics.initialization.WorkflowTemplate;
import statics.initialization.impl.HomogenRuntimeProfilePerType;
import statics.initialization.impl.MeanAlphaSigmaPlanner;
import statics.initialization.impl.WorkflowTemplateFactory;
import statics.util.CompareAlgorithm;
import statics.util.Debug;
import statics.util.Util;
import algorithm.StaticSchedulingAlgorithm;
import cloud.Cloud;
import dynamic.algorithm.remapper.NoRemapper;
import dynamic.algorithm.remapper.Remapper;
import dynamic.generated.Trace;
import dynamic.generated.Trace.Categories.Predictioncategory;
import dynamic.generated.Trace.Categories.Predictioncategory.Deadlinecategories;
import dynamic.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory;
import dynamic.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Plan;
import dynamic.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs;
import dynamic.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run;
import dynamic.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Dynamic;
import dynamic.main.config.StartParameters;
import dynamic.scheduling.engine.WorkflowEngineImpl;
import java.io.PrintWriter;
import java.util.Map;
import reality.executiontimes.AllNormalDistributedExecutionTimes;
import reality.executiontimes.ExecutionTimes;
import statics.initialization.SchedulingInformation;
import statics.initialization.impl.scaler.ConstantProfileScaler;
import statics.util.BillingUtil;
import statics.util.CloudUtil;
import statics.util.GetFastestByMeanTime;

public class ScheduleTestFramework {
    
    private PrintStream out;
    private PrintWriter csvout;
    private long ID;
    private StartParameters startParameters;
    
    public void doIt() {
        
        final boolean adapt = false;
        final Long randomSeedStart = 0L;
//        final Long randomSeedStart = null;
//        double sigmaPercentage = 0.1;

        Trace output = configure();
        csvout.println("algorithm;makespan;cost;planningtime");
        String cloudFile = startParameters.getCloudFile();
        String profileFile = startParameters.getExecutionProfileFile();
        List<Double> deadlineFactors = startParameters.getDeadlineFactors();
        List<Double> alphas = startParameters.getAlphas();
        String dependencyGraphFile = startParameters.getDependencyGraphFile();
        List<String> executionTimesFiles = startParameters.getExecutionTimesFiles();
        String algorithmName = startParameters.getAlgorithmName();
        Map<String, String> parameters = startParameters.getParameters();
        String suffix = startParameters.getSuffix();

        // configuration ends here
        Debug.INSTANCE.setDebug(Integer.MIN_VALUE);
        DAXExecutionTimes.clearCache();
        
        WorkflowTemplateFactory factory = new WorkflowTemplateFactory();
        
        Cloud cloud = factory.readCloud(cloudFile);
        DependencyGraph graph = factory.readGraph(dependencyGraphFile);
        DAXExecutionTimes.setReferenzGraph(graph);
        
        double scale = startParameters.getScale();
        HomogenRuntimeProfilePerType profile = factory.readHomogenRuntimeProfilePerTypeFromRuntimeProfiles(profileFile, new ConstantProfileScaler(scale));
        SchedulingInformation information = factory.getInformation();

        // XXX also possible per Id
        WorkflowTemplate template = factory.create();

//        out.println(factory.toString());
        output.setCloud(cloud.toString());
        output.setDependencies(graph.toString());
        output.setHistoricaldata(information.toString());
        
        output.setWorkflow(dependencyGraphFile);
        output.setAlgorithm(algorithmName);
        
        factory = null;
        CloudUtil.getInstance(cloud.getSizes());
        BillingUtil.getInstance(cloud.getAtuLength());
        Debug.INSTANCE.setDebug(0);
        
        boolean totalCheck = true;
        
        final int runsCount = alphas.size() * deadlineFactors.size() * executionTimesFiles.size();
        double pCent = 100. / (double) runsCount;
        int runCount = 0;
        System.out.println("total Number of runs: " + runsCount);
        for (double alpha : alphas) {
            try {
                out.println("########################");
                out.println("#        Alpha         #");
                out.println("########################");
                out.println("alpha and meanalphasigmaplanner");
                out.println(alpha);
                if (output.getCategories() == null) {
                    output.setCategories(new dynamic.generated.Trace.Categories());
                }
                Predictioncategory predictionCategory = new Predictioncategory();
                output.getCategories().getPredictioncategory().add(predictionCategory);
                predictionCategory.setMethod("alpha");
                predictionCategory.setValue(alpha);
                
                Planner p = new MeanAlphaSigmaPlanner(alpha);
                
                WorkflowInstance cheapFast = template.createWorkflowInstance(Double.MAX_VALUE, p);
                
                double shortestPlannedMakespan = GetFastestByMeanTime.getFastest(cheapFast); // using mean time
//                double shortestPlannedMakespan = CompareAlgorithm.getFastest(cheapFast); // using planner
                double shortestPlannedMakespanMinutes = shortestPlannedMakespan / Util.SECONDS_IN_MINUTES;
                
                out.printf("shortest makespan     : %10.4f    minutes%n", shortestPlannedMakespanMinutes);
                
                double cheapestPlannedCost = CompareAlgorithm.getCheapest(cheapFast);
                out.printf("cheapest cost         : %10.0f    $%n", cheapestPlannedCost);
                
                predictionCategory.setShortestmakespan(shortestPlannedMakespanMinutes);
                predictionCategory.setCheapestcost(cheapestPlannedCost);
                
                for (double dlFactor : deadlineFactors) {
                    try {
                        out.println("########   DEADLINE  ############");
                        out.println("dl factor");
                        out.println(dlFactor);
                        final double dl = dlFactor * shortestPlannedMakespan;
                        final double dlMinutes = dl / Util.SECONDS_IN_MINUTES;
                        out.println("plan:");
                        out.printf("deadline  : %10.4f    minutes%n", dlMinutes);
                        
                        long s, e, d;
                        WorkflowInstance planMaster = template.createWorkflowInstance(dl, p);
                        StaticSchedulingAlgorithm algorithm = MainHelper.createStaticAlgorithm(algorithmName, parameters);
                        s = System.currentTimeMillis();
                        planMaster = MainHelper.createPlan(planMaster, algorithm);
                        e = System.currentTimeMillis();
                        long planningTime = e - s;
                        double ptotalCost = planMaster.getTotalCost();
                        double pMakespanMinutes = planMaster.getMakespan() / Util.SECONDS_IN_MINUTES;

//                        csvout.println(dlFactor + "deadline;" + dl + ";" + cheapestPlannedCost); // XXX maybe set 0 here and use cheapest real costs per run
                        csvout.println("deadline;" + dl / 3600. + ";0;0");
                        csvout.println(algorithmName + suffix + "_a;" + planMaster.getMakespan() / 3600. + ";" + planMaster.getTotalCost() / 1000. + ";" + planningTime);
                        
                        out.printf("p makespan: %10.4f    minutes%n", pMakespanMinutes);
                        out.printf("p cost    : %10.0f    $%n", ptotalCost);
//                        out.printf("cheap.cost: %10.0f    $%n", cheapestPlannedCost);
                        out.printf("p time    : %10d    ms%n", planningTime);
                        Deadlinecategory dlcategory = new Deadlinecategory();
                        dlcategory.setDeadlinefactor(dlFactor);
                        if (predictionCategory.getDeadlinecategories() == null) {
                            predictionCategory.setDeadlinecategories(new Deadlinecategories());
                        }
                        predictionCategory.getDeadlinecategories().getDeadlinecategory().add(dlcategory);
                        if (dlcategory.getPlan() == null) {
                            dlcategory.setPlan(new Plan());
                        }
                        Plan outplan = dlcategory.getPlan();
                        outplan.setDeadline(dlMinutes);
                        outplan.setPlannedmakespan(pMakespanMinutes);
                        outplan.setPlannedcost(ptotalCost);
                        outplan.setCheapestplannedcost(cheapestPlannedCost);
                        dlcategory.setDeadline(dlMinutes);
                        out.println();
                        long loopExS = System.currentTimeMillis();
                        for (int exTimesIndex = 0; exTimesIndex < executionTimesFiles.size(); ++exTimesIndex) {
                            
                            String exTimes = executionTimesFiles.get(exTimesIndex);
                            runCount++;
                            System.out.printf("%6.2f %% done", runCount * pCent);
                            
                            if (dlcategory.getRuns() == null) {
                                dlcategory.setRuns(new Runs());
                            }
                            Run run = new Run();
                            dlcategory.getRuns().getRun().add(run);
                            run.setExTimes(exTimes);
                            Long randomSeed = randomSeedStart == null ? null : randomSeedStart + exTimesIndex;
                            run.setSeed(String.valueOf(randomSeed));
                            
                            try {
                                out.println("---------- FILE ----------");
                                out.println("exTimes");
                                out.println(exTimes);
                                out.println();

                                // XXX choose wisely
//                                ExecutionTimes daxExTimes = DAXExecutionTimes.getInstance(new File(exTimes), cloud);
                                ExecutionTimes daxExTimes = new AllNormalDistributedExecutionTimes(planMaster.getTasks(), planMaster.getInstanceSizes(), randomSeed);
//                                double cheapestRealTime = CompareAlgorithm.getCheapestRealTime(planMaster.getTasks(), cloud, daxExTimes);
                                run.setExecutionTimes(daxExTimes.toString());
//                                out.println(daxExTimes);
                                JobExecutionTimeManager.setInstance(daxExTimes);
                                
                                double totalCost, deadlineM, makespanM;
                                {
                                    out.println("start clone");
//                                    WorkflowInstance plan = planMaster.clone(); // XXX check clone method! does not work for size >= 10000
                                    WorkflowInstance plan = planMaster;
                                    s = System.currentTimeMillis();
                                    plan.reset(); // just if setAdapt = false!!!
                                    e = System.currentTimeMillis();
                                    d = e - s;
                                    out.println("complete clone after " + d);
                                    
                                    s = System.currentTimeMillis();
                                    WorkflowEngineImpl engine = new WorkflowEngineImpl(plan);
                                    engine.setAdapt(adapt); // XXX activate if clone is fixed and remove plan.reset than
                                    engine.setGuiVis(false);
                                    engine.setGuiStop(false);
                                    
                                    Remapper remapper;
                                    if (adapt) {
                                        remapper = MainHelper.createRemapperAlgorithm(algorithmName, parameters);
                                    } else {
                                        remapper = new NoRemapper();
                                    }
                                    
                                    e = System.currentTimeMillis();
                                    d = e - s;
                                    out.println("start work after " + d);
                                    s = System.currentTimeMillis();
                                    engine.work(remapper);
                                    e = System.currentTimeMillis();
                                    long etime = e - s;
                                    
                                    if (adapt) {
                                        s = System.currentTimeMillis();
                                        MainHelper.checkPlan(plan, false);
                                        e = System.currentTimeMillis();
                                        d = e - s;
                                        out.println("checkPlan after " + d);
                                    }
                                    
                                    totalCost = engine.getTotalCost();
                                    deadlineM = engine.getDeadline() / Util.SECONDS_IN_MINUTES;
                                    makespanM = engine.getMakespan() / Util.SECONDS_IN_MINUTES;
                                    
                                    csvout.println(algorithmName + suffix + "_e;" + engine.getMakespan() / 3600. + ";" + engine.getTotalCost() / 1000. + ";" + d);
//                                    csvout.println("cheapest;0;" + cheapestRealTime);

                                    out.println("dyn run:");
                                    out.printf("deadline   : %10.4f    minutes%n", deadlineM);
                                    out.printf("e makespan : %10.4f    minutes%n", makespanM);
                                    out.printf("e cost     : %10.0f    $%n", totalCost);
                                    out.printf("e time     : %10d    ms%n", etime);
                                    out.println();
                                    
                                    Dynamic outDynamic = new Dynamic();
                                    run.setDynamic(outDynamic);
                                    outDynamic.setDeadline(deadlineM);
                                    outDynamic.setMakespan(makespanM);
                                    outDynamic.setCost(totalCost);
                                    outDynamic.setDuration(d);
                                }
                                Debug.INSTANCE.setDebug(Integer.MIN_VALUE);
                                out.println();
                            } catch (Exception ex) {
                                totalCheck = handleException(ex);
                            } // try catch
                            System.out.flush();
                        } // for ex times
                        long loopExE = System.currentTimeMillis();
                        long loopExD = loopExE - loopExS;
                        out.println("looptime: " + loopExD);
                    } catch (Exception ex) {
                        totalCheck = handleException(ex);
                    } // try catch
                } // for dl factors
            } catch (Exception ex) {
                totalCheck = handleException(ex);
            }
        } // for alphas
        out.println("totalcheck= " + totalCheck);
        
        String storageDirectory = startParameters.getStorageDirectory();
        String resultOutFileName = startParameters.getResultOutFileName();
        
        JAXB.marshal(output, storageDirectory + "/" + resultOutFileName);
        csvout.close();
        out.println("THE END");
        getStartParameters().closeStdOut(); // out.close()
    }
    
    private boolean handleException(Exception ex) {
        boolean totalCheck;
        out.println("Exception");
        ex.printStackTrace(out);
        out.println("Exception");
        System.exit(0);
        totalCheck = false;
        return totalCheck;
    }
    
    private Trace configure() {
        Locale.setDefault(Locale.ENGLISH);
        
        Trace output = new Trace();
        
        String dirPath = startParameters.getStorageDirectory();
        File outputFile = new File(dirPath);
        if (!outputFile.exists()) {
            outputFile.mkdir();
        }
        
        String cloudFile = startParameters.getCloudFile();
        String profileFile = startParameters.getExecutionProfileFile();
        String dependencyGraphFile = startParameters.getDependencyGraphFile();
        List<String> executionTimesFiles = startParameters.getExecutionTimesFiles();
        
        try {
            out = startParameters.getStdOut();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        Debug.INSTANCE.setPrintStream(out);
        
        try {
            String storageDirectory = startParameters.getStorageDirectory();
            String resultOutFileName = startParameters.getResultOutFileName();
            
            csvout = new PrintWriter(new File(storageDirectory + "/" + resultOutFileName + "_experiment.csv"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        ID = System.currentTimeMillis();
        out.println("runid");
        out.println(ID);
        output.setRunid(ID);
        
        String configString = startParameters.toString();
        out.println(configString);
        
        output.setParameter(configString);

        // outputs
        String path = outputFile.getAbsolutePath();
        try {
            String operation = startParameters.getCloudFileTransfer();
            if (operation != null) {
                if (operation.equals(copyOperation)) {
                    Files.copy(Paths.get(cloudFile), Paths.get(path + "\\CloudfileCopy.xml"), StandardCopyOption.COPY_ATTRIBUTES);
                } else if (operation.equals(moveOperation)) {
                    Files.move(Paths.get(cloudFile), Paths.get(path + "\\CloudfileCopy.xml"), StandardCopyOption.ATOMIC_MOVE);
                } else {
                    // nothing
                }
            }
        } catch (IOException e) {
            System.err.println("Couldn't copy/move CloudFile:" + startParameters.getCloudFile());
        }
        
        try {
            String operation = startParameters.getExecutionProfileFileTransfer();
            if (operation != null) {
                if (operation.equals(copyOperation)) {
                    Files.copy(Paths.get(profileFile), Paths.get(path + "\\ExecutionProfileCopy.xml"), StandardCopyOption.COPY_ATTRIBUTES);
                } else if (operation.equals(moveOperation)) {
                    Files.move(Paths.get(profileFile), Paths.get(path + "\\ExecutionProfileCopy.xml"), StandardCopyOption.ATOMIC_MOVE);
                } else {
                    // nothing
                }
            }
        } catch (IOException i) {
            System.err.println("Couldn't copy/move Excecution Profile File" + startParameters.getExecutionProfileFile());
        }
        
        try {
            String operation = startParameters.getDependencyGraphFileTransfer();
            if (operation != null) {
                if (operation.equals(copyOperation)) {
                    Files.copy(Paths.get(dependencyGraphFile), Paths.get(path + "\\DependencyGraphCopy.xml"), StandardCopyOption.COPY_ATTRIBUTES);
                } else if (operation.equals(moveOperation)) {
                    Files.move(Paths.get(dependencyGraphFile), Paths.get(path + "\\DependencyGraphCopy.xml"), StandardCopyOption.ATOMIC_MOVE);
                } else {
                    // nothing
                }
            }
        } catch (IOException o) {
            System.err.println("Couldn't copy/move Dependency Graph File" + startParameters.getDependencyGraphFile());
        }
        
        try {
            String operation = startParameters.getExecutionTimesFilesTransfer();
            for (int i = 0; i < executionTimesFiles.size(); i++) {
                if (operation != null) {
                    if (operation.equals(copyOperation)) {
                        Files.copy(Paths.get(executionTimesFiles.get(i)), Paths.get(path + "\\ExecutionTime" + (i + 1) + "Copy.xml"), StandardCopyOption.COPY_ATTRIBUTES);
                    } else if (operation.equals(moveOperation)) {
                        Files.move(Paths.get(executionTimesFiles.get(i)), Paths.get(path + "\\ExecutionTime" + (i + 1) + "Copy.xml"), StandardCopyOption.ATOMIC_MOVE);
                    } else {
                        // nothing
                    }
                }
            }
        } catch (IOException f) {
            System.err.println("Couldn't copy/move Execution Times Files");
        }
        return output;
    }
    
    public StartParameters getStartParameters() {
        return startParameters;
    }
    
    public void setStartParameters(StartParameters start) {
        this.startParameters = start;
    }
}
