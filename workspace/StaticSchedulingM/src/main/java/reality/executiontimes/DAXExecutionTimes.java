package reality.executiontimes;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.JAXB;

import cloud.Cloud;
import cloud.CloudFactory;
import cloud.InstanceSize;
import executionprofile.generated.Adag;
import executionprofile.generated.Adag.Job;
import executionprofile.generated.Adag.Job.Uses;
import statics.initialization.DependencyGraph;
import statics.initialization.DependencyTask;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.CloudUtil;
import statics.util.Util;

public class DAXExecutionTimes implements ExecutionTimes {

    private static final Map<File, Map<Cloud, DAXExecutionTimes>> instances = new HashMap<>(1002);

    public static DAXExecutionTimes getInstance(File file, Cloud cloud) {
        DAXExecutionTimes result;
        Map<Cloud, DAXExecutionTimes> map = instances.get(file);
        if (map != null) {
            result = map.get(cloud); // could be null
        } else {
            map = new HashMap<>(5);
            result = null;
        }
        if (result == null) {
            result = new DAXExecutionTimes(file, cloud);
            map.put(cloud, result);
        }
        return result;
    }

    public static void clearCache() {
        instances.clear();
    }

    private final Map<Integer, Map<InstanceSize, Double>> executionTimes = new HashMap<>();
    private static DependencyGraph graph;


    public DAXExecutionTimes(File file, Cloud cloud) {
        Adag adag = JAXB.unmarshal(file, Adag.class);
        initArtificialTasks(cloud.getSizes());

        CloudUtil cUtil = new CloudUtil(cloud.getSizes());
        InstanceSize fastest = cUtil.getFastestSize();

        for (Job job : adag.getJob()) {
            if (graph != null) {
                int id = WorkflowInstance.jobIDToInt(job.getId());
                DependencyTask dependencyTask = graph.getTasks().get(id);
                int dType = dependencyTask.getType();
                int jType = WorkflowInstance.jobNameToInt(job.getName());
                if (dType != jType) {
                    throw new RuntimeException("tasktypes are not equal! Execution time is set to wrong tasktype dTask=" + dependencyTask + " job=" + job);
                }
            }

            Map<InstanceSize, Double> ets = new HashMap<>();
            executionTimes.put(WorkflowInstance.jobIDToInt(job.getId()), ets);

            double fastestEx = job.getRuntime().doubleValue();
            for (Uses uses : job.getUses()) {
                double size = uses.getSize().doubleValue();
                size /= CloudFactory.MEGABYTE_IN_BYTE_20;
                fastestEx += size;
            }

            for (InstanceSize instanceSize : cloud.getSizes()) {
                double slowDown = fastest.getSpeedup() / instanceSize.getSpeedup();
                double et = fastestEx * slowDown;
                if (et < Util.MIN_EX_TIME) {
                    et = Util.MIN_EX_TIME;
                }
                et = Util.round2Digits(et);
                ets.put(instanceSize, et);
            }
        }
    }

    private void initArtificialTasks(List<InstanceSize> sizes) {
        SortedMap<InstanceSize, Double> zeros = new TreeMap<>();
        for (InstanceSize size : sizes) {
            if (size.isDummy()) {
                zeros.put(size, 0.);
            } else {
                zeros.put(size, Double.POSITIVE_INFINITY);
            }
        }
        executionTimes.put(DependencyGraph.ENTRY_ID, zeros);
        executionTimes.put(DependencyGraph.EXIT_ID, zeros);
    }

    @Override
    public double getExecutionTime(SchedulingTask task, InstanceSize size) {
        try {
            Map<InstanceSize, Double> map = executionTimes.get(task.getId());
            double exTime = map.get(size);
            return exTime;
        } catch (RuntimeException ex) {
            System.out.println();
            String taskStr = String.valueOf(task);
            String sizeStr = String.valueOf(size);
            String exStr = String.valueOf(executionTimes);
            System.out.printf("%s, %s, %s%n", taskStr, sizeStr, exStr);
            throw ex;
        }
    }

    public static void setReferenzGraph(DependencyGraph graph) {
        DAXExecutionTimes.graph = graph;
    }

}
