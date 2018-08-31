package statics.initialization.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import cloud.CloudFactory;
import executionprofile.generated.Adag;
import executionprofile.generated.Adag.Job;
import executionprofile.generated.TransferType;
import statics.initialization.DependencyGraph;
import statics.initialization.WorkflowInstance;
import statics.initialization.impl.scaler.DaxScaler;
import statics.initialization.impl.scaler.DefaultDaxScaler;
import statics.initialization.impl.scaler.DefaultProfileScaler;
import statics.initialization.impl.scaler.ProfileScaler;
import executionprofile.generated.Executionprofile;

public class HomogenRuntimeProfilesFactory {

    private HomogenRuntimeProfilesFactory() {
        // private
    }

    public static HomogenRuntimeProfilePerId readHomogenRuntimeProfilesFromDaxPerId(File file) {
        return readHomogenRuntimeProfilesFromDaxPerId(file, new DefaultDaxScaler());
    }

    public static HomogenRuntimeProfilePerId readHomogenRuntimeProfilesFromDaxPerId(File file, DaxScaler scaler) {
        Adag adag = JAXB.unmarshal(file, Adag.class);
        Map<Integer, Double> ets = new HashMap<>();
        Map<Integer, Double> vars = new HashMap<>();
        List<Job> jobs = adag.getJob();

        jobs.stream().forEach((job) -> {
            int id = jobIDToInt(job.getId());
            double runtime = job.getRuntime().doubleValue();
            runtime = job.getUses().stream().filter((uses) -> (uses.getTransfer() == TransferType.TRUE)).map((uses) -> uses.getSize().doubleValue()).map((size) -> size / CloudFactory.MEGABYTE_IN_BYTE_20).map((transfertime) -> transfertime).reduce(runtime, (accumulator, _item) -> accumulator + _item);
            runtime = scaler.getExecutionTime(job, runtime);
            ets.put(id, runtime);
            vars.put(id, 0.);
        });

        handleArtificialTasks(ets, vars);
        HomogenRuntimeProfilePerId profiles = new HomogenRuntimeProfilePerId(ets, vars);
        return profiles;
    }

    public static HomogenRuntimeProfilePerType readHomogenRuntimeProfilesFromDaxPerType_Use_Means(File file) {

        Adag adag = JAXB.unmarshal(file, Adag.class);

        Map<Integer, Integer> count = new HashMap<>();
        Map<Integer, Double> ets = new HashMap<>();
        Map<Integer, Double> vars = new HashMap<>();
        List<Job> jobs = adag.getJob();

        sumValues(jobs, count, ets);
        normalizeValues(count, ets);
        calcVar(jobs, count, ets, vars);

        handleArtificialTasks(ets, vars);

        HomogenRuntimeProfilePerType profiles = new HomogenRuntimeProfilePerType(ets, vars);
        return profiles;
    }

    private static void handleArtificialTasks(Map<Integer, Double> ets, Map<Integer, Double> vars) {
        ets.put(DependencyGraph.ENTRY_ID, 0.);
        ets.put(DependencyGraph.EXIT_ID, 0.);
        vars.put(DependencyGraph.ENTRY_ID, 0.);
        vars.put(DependencyGraph.EXIT_ID, 0.);
    }

    private static void calcVar(List<Job> jobs, Map<Integer, Integer> count, Map<Integer, Double> ets, Map<Integer, Double> vars) {
        jobs.stream().forEach((job) -> {
            int name = WorkflowInstance.jobNameToInt(job.getName());
            double runtime = job.getRuntime().doubleValue();

            double mean = ets.get(name);
            double diff = Math.pow(runtime - mean, 2);
            diff /= count.get(name);

            if (vars.containsKey(name)) {
                double sum = vars.get(name);
                sum += diff;
                vars.put(name, sum);
            } else {
                vars.put(name, diff);
            }
        });
    }

    private static void normalizeValues(Map<Integer, Integer> count, Map<Integer, Double> value) {
        value.keySet().stream().forEach((name) -> {
            double doub = value.get(name);
            int c = count.get(name);
            doub /= c;
            value.put(name, doub);
        });
    }

    private static void sumValues(List<Job> jobs, Map<Integer, Integer> count, Map<Integer, Double> value) {
        jobs.stream().forEach((job) -> {
            int name = WorkflowInstance.jobNameToInt(job.getName());
            double runtime = job.getRuntime().doubleValue();

            if (count.containsKey(name)) {
                Integer integer = count.get(name);
                integer++;
                count.put(name, integer);
            } else {
                count.put(name, 1);
            }

            if (value.containsKey(name)) {
                Double doub = value.get(name);
                doub += runtime;
                value.put(name, doub);
            } else {
                value.put(name, runtime);
            }
        });
    }

    public static HomogenRuntimeProfilePerType readHomogenRuntimeProfilesFromExecutionProfilePerType(File file) {
        return readHomogenRuntimeProfilesFromExecutionProfilePerType(file, new DefaultProfileScaler());
    }

    public static HomogenRuntimeProfilePerType readHomogenRuntimeProfilesFromExecutionProfilePerType(File file, ProfileScaler scaler) {
        Executionprofile exProfile = JAXB.unmarshal(file, Executionprofile.class);
        Map<Integer, Double> ets = new HashMap<>();
        Map<Integer, Double> vars = new HashMap<>();

        exProfile.getWorkflows().getWorkflow().stream().forEach((workflow) -> {
            workflow.getJobs().getJob().stream().forEach((job) -> {
                int name = WorkflowInstance.jobNameToInt(job.getName());
                double runtime = job.getRuntime().getMean();
                double input = job.getInputs().getMean();
                input /= CloudFactory.MEGABYTE_IN_BYTE_20;
                double output = job.getOutputs().getMean();
                output /= CloudFactory.MEGABYTE_IN_BYTE_20;
                runtime += input + output;
                runtime = scaler.getExecutionTime(job, runtime);
                ets.put(name, runtime);

                double var = job.getRuntime().getVar();
                double varI = job.getInputs().getVar();
                varI /= Math.pow(CloudFactory.MEGABYTE_IN_BYTE_20, 2);
                double varO = job.getOutputs().getVar();
                varO /= Math.pow(CloudFactory.MEGABYTE_IN_BYTE_20, 2);
                var += varI + varO;
                var = scaler.getVarTime(job, var);
                vars.put(name, var);
            });
        });
        handleArtificialTasks(ets, vars);

        HomogenRuntimeProfilePerType profiles = new HomogenRuntimeProfilePerType(ets, vars);
        return profiles;
    }

    public static int jobIDToInt(String jobId) {
        String number = jobId.substring(2); // ID00000
        return Integer.parseInt(number);
    }

//    public static int jobNameToInt(String jobName) {
//        Integer i = jobNameToId.get(jobName);
//        if (i == null) {
//            i = jobNameToId.size() + 1;
//            jobNameToId.put(jobName, i);
//        }
//        return i;
//    }
//
//    private static final Map<String, Integer> jobNameToId = new HashMap<>();
}
