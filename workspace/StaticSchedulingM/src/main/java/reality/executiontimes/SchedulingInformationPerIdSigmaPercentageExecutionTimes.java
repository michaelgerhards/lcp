//package reality.executiontimes;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.SortedMap;
//import java.util.TreeMap;
//
//import cloud.InstanceSize;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Random;
//import statics.initialization.DependencyGraph;
//import statics.initialization.SchedulingTask;
//import statics.initialization.impl.SchedulingInformationImplPerId;
//
//public class SchedulingInformationPerIdSigmaPercentageExecutionTimes implements ExecutionTimes {
//
//    private final Map<Integer, Map<InstanceSize, Double>> executionTimes = new HashMap<>();
//
//    public SchedulingInformationPerIdSigmaPercentageExecutionTimes(SchedulingInformationImplPerId staticTimes, double sigmaPercentage, long seed) {
//        // could also take the variances of the static Times.
//        Random random = new Random(seed);
//
//        List<Integer> taskIds = new ArrayList<>(staticTimes.getTaskIds());
//        int unimportantTaskId = taskIds.get(0);
//        Map<InstanceSize, Double> unimportantMapping = staticTimes.getExecutionTimes(unimportantTaskId);
//        List<InstanceSize> instanceSizes = new ArrayList<>(unimportantMapping.keySet()); // assume that all tasks have the same instance sizes
//        Collections.sort(taskIds);
//        Collections.sort(instanceSizes);
//
//        for (int taskId : taskIds) {
//            Map<InstanceSize, Double> taskExecutionTimes = staticTimes.getExecutionTimes(taskId);
//            Map<InstanceSize, Double> taskExecutionTimesNew = new HashMap<>();
//            this.executionTimes.put(taskId, taskExecutionTimesNew);
//
//            for (InstanceSize size : instanceSizes) {
//                double taskExecutionTime = taskExecutionTimes.get(size);
//                if (taskExecutionTime == Double.POSITIVE_INFINITY) {
//                    taskExecutionTimesNew.put(size, taskExecutionTime);
//                    continue;
//                }
//                double mean = taskExecutionTime;
//                double sigma = sigmaPercentage * taskExecutionTime;
//                double randomValue = random.nextGaussian();
//                double deviatedExecutionTime = randomValue * sigma + mean;
//                taskExecutionTimesNew.put(size, deviatedExecutionTime);
//            }
//        }
//    }
//
//    @Override
//    public double getExecutionTime(SchedulingTask task, InstanceSize size) {
////        try {
//            Map<InstanceSize, Double> map = executionTimes.get(task.getId());
//            double exTime = map.get(size);
//            return exTime;
////        } catch (RuntimeException ex) {
////            System.out.println();
////            String taskStr = String.valueOf(task);
////            String sizeStr = String.valueOf(size);
////            String exStr = String.valueOf(executionTimes);
////            System.out.printf("%s, %s, %s%n", taskStr, sizeStr, exStr);
////            throw ex;
////        }
//    }
//
//}
