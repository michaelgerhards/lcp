package generator;

import executionprofile.generated.Adag;
import executionprofile.generated.Adag.Job;
import executionprofile.generated.Adag.Job.Uses;
import executionprofile.generated.Executionprofile;
import executionprofile.generated.Executionprofile.Workflows;
import executionprofile.generated.Executionprofile.Workflows.Workflow;
import executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs;
import executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs.Job.Inputs;
import executionprofile.generated.Executionprofile.Workflows.Workflow.Jobs.Job.Outputs;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;


import java.util.Map.Entry;

public class ExecutionProfileGenerator_MeanVar {

    private final Map<String, List<Job>> jobsTotal = new HashMap<>();
    private final Map<Job, String> fileOfJob = new HashMap<>();
    private FileWriter pw;
    private final boolean printValuesOnStdOut = false;

    private Map<String, Double> meanRtMap;
    private Map<String, Double> meanInMap;
    private Map<String, Double> meanOuMapt;
    private Map<String, Integer> countMap;
    private Map<String, Double> maxRtMap;
    private Map<String, Double> minRtMap;
    private Map<String, Double> maxInMap;
    private Map<String, Double> minInMap;
    private Map<String, Double> maxOutMap;
    private Map<String, Double> minOutMap;
    private Map<String, Double> varRtMap;
    private Map<String, Double> varInMap;
    private Map<String, Double> varOutMap;

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
        List<Job> jobs = adag.getJob();
        for (int j = 0; j < jobs.size(); j++) {
            if (!jobsTotal.containsKey(jobs.get(j).getName())) {
                jobsTotal.put(jobs.get(j).getName(), new ArrayList<>());
            }
            jobsTotal.get(jobs.get(j).getName()).add(jobs.get(j));
            fileOfJob.put(jobs.get(j), current.getName());
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

        minRtMap = new HashMap<>(jobsTotal.size());
        minInMap = new HashMap<>(jobsTotal.size());
        minOutMap = new HashMap<>(jobsTotal.size());

        maxRtMap = new HashMap<>(jobsTotal.size());
        maxInMap = new HashMap<>(jobsTotal.size());
        maxOutMap = new HashMap<>(jobsTotal.size());

        varRtMap = new HashMap<>(jobsTotal.size());
        varInMap = new HashMap<>(jobsTotal.size());
        varOutMap = new HashMap<>(jobsTotal.size());

        // loop for different jobs
        // for (ArrayList<Job> value : jobsTotal.values()) {
        for (Entry<String, List<Job>> entry : jobsTotal.entrySet()) {
            String name = entry.getKey();
            List<Job> value = entry.getValue();

            List<Double> runtime = new ArrayList<>(value.size());
            List<Double> inputByteJ = new ArrayList<>(value.size());
            List<Double> outputByteJ = new ArrayList<>(value.size());

            double rtMin = Double.POSITIVE_INFINITY;
            double rtMax = Double.NEGATIVE_INFINITY;
            double inMin = Double.POSITIVE_INFINITY;
            double inMax = Double.NEGATIVE_INFINITY;
            double outMin = Double.POSITIVE_INFINITY;
            double outMax = Double.NEGATIVE_INFINITY;

            double runtimeSum = 0.;
            double inputSum = 0.;
            double outputSum = 0.;
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
                runtimeSum += runtime.get(k);
                inputSum += inputByteJ.get(k);
                outputSum += outputByteJ.get(k);

                rtMax = Math.max(rtMax, runtime.get(k));
                rtMin = Math.min(rtMin, runtime.get(k));

                outMax = Math.max(outMax, outputByteJ.get(k));
                outMin = Math.min(outMin, outputByteJ.get(k));

                inMax = Math.max(inMax, inputByteJ.get(k));
                inMin = Math.min(inMin, inputByteJ.get(k));

            }

            double runTimeMean = runtimeSum / value.size();
            double inputMean = inputSum / value.size();
            double outputMean = outputSum / value.size();

            double rtVar = 0.;
            double inVar = 0.;
            double outVar = 0.;

            for (int k = 0; k < value.size(); k++) {
                double rt = runtime.get(k);
                rtVar += Math.pow(rt - runTimeMean, 2.) / value.size();

                double in = inputByteJ.get(k);
                inVar += Math.pow(in - inputMean, 2.) / value.size();

                double out = outputByteJ.get(k);
                outVar += Math.pow(out - outputMean, 2.) / value.size();
            }

            // check some values
            for (int k = 0; k < value.size(); k++) {
                double rt = runtime.get(k);
                if (rt > runTimeMean + 3 * Math.sqrt(rtVar)) {
                    System.out.println("Strange high runtime value for " + name + " with value: " + rt + " but mean is " + runTimeMean);
                } else if (rt < runTimeMean - 3 * Math.sqrt(rtVar)) {
                    System.out.println("Strange low runtime value for " + name + " with value: " + rt + " but mean is " + runTimeMean);
                }
            }

            meanRtMap.put(name, runTimeMean);
            meanInMap.put(name, inputMean);
            meanOuMapt.put(name, outputMean);

            countMap.put(name, value.size());

            minRtMap.put(name, rtMin);
            maxRtMap.put(name, rtMax);

            minInMap.put(name, inMin);
            maxInMap.put(name, inMax);

            minOutMap.put(name, outMin);
            maxOutMap.put(name, outMax);

            varInMap.put(name, inVar);
            varOutMap.put(name, outVar);
            varRtMap.put(name, rtVar);

            if (pw != null) {
                printAllDataOfOneTaskType(value, runtime, inputByteJ, outputByteJ);
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
            s = String.format("%s;%s;%f;%f;%f;%f%n",
                    fileOfJob.get(jobs.get(i)), jobs.get(i).getName(),
                    inputByteJ.get(i), runtime.get(i), inputByteJ.get(i),
                    outputByteJ.get(i));
            write(s);
        }
    }

    private void printStatistics(String name) throws IOException {
        String s = String.format("%n;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s%n", "name",
                "count", "iMean", "iVar", "iMin", "iMax", "rMean", "rVar",
                "rMin", "rMax", "oMean", "oVar", "oMin", "oMax");
        write(s);

        double runTimeMean = meanRtMap.get(name);
        double inputMean = meanInMap.get(name);
        double outputMean = meanOuMapt.get(name);

        int count = countMap.get(name);

        double rtMin = minRtMap.get(name);
        double rtMax = maxRtMap.get(name);

        double inMin = minInMap.get(name);
        double inMax = maxInMap.get(name);

        double outMin = minOutMap.get(name);
        double outMax = maxOutMap.get(name);

        double inVar = varInMap.get(name);
        double outVar = varOutMap.get(name);
        double rtVar = varRtMap.get(name);

        s = String.format(";%s;%d;%f;%f;%f;%f;%f;%f;%f;%f;%f;%f;%f;%f%n", name,
                count, inputMean, inVar, inMin, inMax, runTimeMean, rtVar,
                rtMin, rtMax, outputMean, outVar, outMin, outMax);
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

            Executionprofile.Workflows.Workflow.Jobs.Job.Runtime runtime = new Executionprofile.Workflows.Workflow.Jobs.Job.Runtime();
            Inputs inputs = new Inputs();
            Outputs outputs = new Outputs();
            job.setRuntime(runtime);
            job.setInputs(inputs);
            job.setOutputs(outputs);

            double rt = meanRtMap.get(name);
            double in = meanInMap.get(name);
            double ou = meanOuMapt.get(name);
            int c = countMap.get(name);
            double maxRt = maxRtMap.get(name);
            double minRt = minRtMap.get(name);

            double maxIn = maxInMap.get(name);
            double minIn = minInMap.get(name);

            double maxOut = maxOutMap.get(name);
            double minOut = minOutMap.get(name);

            double rtVar = varRtMap.get(name);
            double inVar = varInMap.get(name);
            double outVar = varOutMap.get(name);

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
