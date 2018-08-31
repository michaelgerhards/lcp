package generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import executionprofile.generated.Executionprofile;
import executionprofile.generated.Executionprofile.Workflows;
import executionprofile.generated.Executionprofile.Workflows.Workflow;
import executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs;
import executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs.Job.Inputs;
import executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs.Job.Outputs;
import executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs.Job.Runtime;
import executionprofile.generated.Adag;
import executionprofile.generated.Adag.Job;
import executionprofile.generated.Adag.Job.Uses;

public class ExecutionProfileGenerator_Quantile {

    private final Map<String, List<Job>> jobsTotal = new HashMap<>();
    private final Map<Job, String> fileOfJob = new HashMap<>();
    private FileWriter pw;
    private final boolean printValuesOnStdOut = false;

    private Map<String, Double> meanRtMap;
    private Map<String, Double> meanInMap;
    private Map<String, Double> meanOuMapt;
    private Map<String, Integer> countMap;
    private final double quantile;

    /**
     *
     * @param quantile 0 < quantile < 1
     */
    public ExecutionProfileGenerator_Quantile(double quantile) {
        this.quantile = quantile;
    }

    public void readAllJobsFromDirectory(File dir) {
        if (!dir.isDirectory()) {
            throw new RuntimeException();
        }
        System.out.println("Read jobs from " + dir.getAbsolutePath());
        String[] files = dir.list();
        // jobsTotal
        // fileOfJob
        for (String filename : files) {
            String filepath = dir.getAbsolutePath() + "\\" + filename;
            File current = new File(filepath);
            if (current.isFile()) {
                readAllJobsFromFile(current);
            }
        }
    }

    public void readAllJobsFromFile(File current) {
        System.out.println("Found file " + current.getName());
        Adag adag = JAXB.unmarshal(current, Adag.class);
        for (Job job : adag.getJob()) {
            if (!jobsTotal.containsKey(job.getName())) {
                jobsTotal.put(job.getName(), new ArrayList<>());
            }
            // mapy tasktype to task
            jobsTotal.get(job.getName()).add(job);
            fileOfJob.put(job, current.getName());
        }
    }

    public void calcValues(File csvFile) throws IOException {
        if (csvFile != null) {
            System.out.println("create csvData at " + csvFile.getAbsolutePath());
            pw = new FileWriter(csvFile, false);
        } else {
            pw = null;
        }

        countMap = new HashMap<>(jobsTotal.size());

        meanRtMap = new HashMap<>(jobsTotal.size());
        meanInMap = new HashMap<>(jobsTotal.size());
        meanOuMapt = new HashMap<>(jobsTotal.size());

        // loop for different jobs
        for (Map.Entry<String, List<Job>> entry : jobsTotal.entrySet()) {
            String name = entry.getKey();
            List<Job> value = entry.getValue();

            List<Double> runtime = new ArrayList<>(value.size());
            List<Double> inputByteJ = new ArrayList<>(value.size());
            List<Double> outputByteJ = new ArrayList<>(value.size());

            // loop for jobs with the same name
            for (int k = 0; k < value.size(); k++) {
                runtime.add(value.get(k).getRuntime().doubleValue());
                inputByteJ.add(0.);
                outputByteJ.add(0.);

                // sum up input and outputs
                List<Uses> uses = value.get(k).getUses();
                // loop for the uses of each job
                for (int i = 0; i < uses.size(); i++) {
                    switch (uses.get(i).getLink().name()) {
                        case "INPUT": {
                            double datasize = uses.get(i).getSize().doubleValue();
                            double ink = inputByteJ.get(k);
                            datasize = datasize + ink;
                            inputByteJ.set(k, datasize);
                            break;
                        }
                        case "OUTPUT": {
                            double datasize = uses.get(i).getSize().doubleValue();
                            double ink = outputByteJ.get(k);
                            datasize = datasize + ink;
                            outputByteJ.set(k, datasize);
                            break;
                        }
                        default:
                            throw new RuntimeException();
                    }
                }
            }

            if (pw != null) {
                printAllDataOfOneTaskType(value, runtime, inputByteJ, outputByteJ);
            }

            Collections.sort(runtime);
            Collections.sort(inputByteJ);
            Collections.sort(outputByteJ);

            int ind = ((int) Math.ceil(runtime.size() * quantile)) - 1;

            double runTimeMean = runtime.get(ind);
            double inputMean = inputByteJ.get(ind);
            double outputMean = outputByteJ.get(ind);
            System.out.printf("take entry " + ind + " of " + (runtime.size() - 1) + " for quantile " + quantile + " with runtime values %.2f %.2f %.2f%n", runtime.get(0), runTimeMean, runtime.get(runtime.size() - 1));

            meanRtMap.put(name, runTimeMean);
            meanInMap.put(name, inputMean);
            meanOuMapt.put(name, outputMean);

            countMap.put(name, value.size());
            if (pw != null) {
                printStatistics(name);

                String s = String.format("%n###################%n%n");
                write(s);
            }

        }
        if (pw != null) {
            pw.close();
        }
    }

    private void printAllDataOfOneTaskType(List<Job> jobs, List<Double> runtime, List<Double> inputByteJ, List<Double> outputByteJ) throws IOException {
        String s = String.format("%s;%s;%s;%s;%s;%s%n", "file", "name", "input", "runtime", "input", "output");
        write(s);
        for (int i = 0; i < runtime.size(); i++) {
            s = String.format("%s;%s;%f;%f;%f;%f%n", fileOfJob.get(jobs.get(i)), jobs.get(i).getName(), inputByteJ.get(i), runtime.get(i), inputByteJ.get(i), outputByteJ.get(i));
            write(s);
        }
    }

    private void printStatistics(String name) throws IOException {
        String s;
        s = String.format("%n%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s%n", "quantile", "name", "count", "iMean", "iVar", "iMin", "iMax", "rMean", "rVar", "rMin", "rMax", "oMean", "oVar", "oMin", "oMax");
        write(s);

        double runTimeMean = meanRtMap.get(name);
        double inputMean = meanInMap.get(name);
        double outputMean = meanOuMapt.get(name);

        int count = countMap.get(name);

        double rtMin = runTimeMean;
        double rtMax = runTimeMean;

        double inMin = inputMean;
        double inMax = inputMean;

        double outMin = outputMean;
        double outMax = outputMean;

        double inVar = 0;
        double outVar = 0;
        double rtVar = 0;

        s = String.format("%f;%s;%d;%f;%f;%f;%f;%f;%f;%f;%f;%f;%f;%f;%f%n", quantile, name, count, inputMean, inVar, inMin, inMax, runTimeMean, rtVar, rtMin, rtMax, outputMean, outVar, outMin, outMax);
        write(s);
    }

    private void write(String s) throws IOException {
        pw.append(s);
        if (printValuesOnStdOut) {
            s = s.replace(';', '\t');
            System.out.print(s);
        }
    }

    public void printExecutionProfile(File f) {
        if (f == null) {
            return;
        }
        System.out.println("create exProfile at " + f.getAbsolutePath());
        Executionprofile profile = new Executionprofile();
        profile.setName("Generated");
        profile.setDescription("Generated Profile");

        Workflows wfs = new Workflows();
        profile.setWorkflows(wfs);

        Workflow wf = new Workflow();
        wf.setName("all");

        wfs.getWorkflow().add(wf);

        Jobs jobs = new Jobs();
        wf.setJobs(jobs);

        for (String name : meanRtMap.keySet()) {
            executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs.Job job = new executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs.Job();
            jobs.getJob().add(job);
            job.setName(name);

            Runtime runtime = new Runtime();
            Inputs inputs = new Inputs();
            Outputs outputs = new Outputs();
            job.setRuntime(runtime);
            job.setInputs(inputs);
            job.setOutputs(outputs);

            double rt = meanRtMap.get(name);
            double in = meanInMap.get(name);
            double ou = meanOuMapt.get(name);
            int c = countMap.get(name);
            double maxRt = rt;
            double minRt = rt;

            double maxIn = in;
            double minIn = in;

            double maxOut = ou;
            double minOut = ou;

            double rtVar = 0;
            double inVar = 0;
            double outVar = 0;

            job.setCount(c);
            runtime.setMean(rt);
            inputs.setMean(in);
            outputs.setMean(ou);

            runtime.setVar(rtVar);
            inputs.setVar(inVar);
            outputs.setVar(outVar);

            runtime.setMax(maxRt);
            runtime.setMin(minRt);

            inputs.setMax(maxIn);
            inputs.setMin(minIn);

            outputs.setMax(maxOut);
            outputs.setMin(minOut);

        }
        JAXB.marshal(profile, f);
    }

}
