package statics.util;

import algorithm.misc.ScaledPseudoLane;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import cloud.InstanceSize;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.initialization.impl.SchedulingTaskList;

public class Util {

    public static final double MIN_EX_TIME = 0.01;
    public static final double MIN_TRANSFER_TIME = 0.00;
    public static final int SECONDS_IN_MINUTES = 60;
    public static final int SECONDS_IN_HOURS = 3600;
    public static final double DOUBLE_THRESHOLD = 0.001;
    public static final double UNSET = -10.0475984484;

    public static <T> void swapList(List<T> allJoins, int ind1, int ind2) {
        T join1 = allJoins.get(ind1);
        T join2 = allJoins.get(ind2);
        allJoins.set(ind1, join2);
        allJoins.set(ind2, join1);
    }

    public static <T> int bestOfList(List<T> lanes, Comparator<T> comp,
            int startindex) {
        int bestLaneIndex = startindex;
        for (int i = startindex + 1; i < lanes.size(); ++i) {
            T lane = lanes.get(i);
            if (comp.compare(lane, lanes.get(bestLaneIndex)) < 0) {
                bestLaneIndex = i;
            }
        }
        return bestLaneIndex;
    }

    public static double calcExecutionTime(List<SchedulingTask> pcp, InstanceSize instanceSize) {
        double duration = 0;
        duration = pcp.stream().map((SchedulingTask ti) -> ti.getExecutionTime(instanceSize)).reduce(duration, (Double accumulator, Double _item) -> accumulator + _item);
        return duration;
    }

    private Util() {
        // nothing
    }

    public static double round2Digits(double value) {
        if (value == Double.POSITIVE_INFINITY
                || value == Double.NEGATIVE_INFINITY || value == Double.NaN) {
            return value;
        }
        return Math.round(value * 100.) / 100.;
    }

    public static boolean contains(int c, int[] aa) {
        for (int a : aa) {
            if (c == a) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(int c, Integer[] aa) {
        for (int a : aa) {
            if (c == a) {
                return true;
            }
        }
        return false;
    }

    public static Collection<Tupel<SchedulingTask, SchedulingTask>> getInterleavings(List<SchedulingTask> tasks) {
        List<Tupel<SchedulingTask, SchedulingTask>> interleavings = new ArrayList<>();
        for (int ti = 0; ti < tasks.size() - 1; ++ti) {
            SchedulingTask parent = tasks.get(ti);
            SchedulingTask child = tasks.get(ti + 1);
            double et = parent.getEndTime();
            double st = child.getStartTime();
            if (et > st) {
                interleavings.add(new Tupel<>(parent, child));
            }
        }
        return interleavings;
    }

    public static Collection<Tupel<SchedulingTask, SchedulingTask>> getGaps(List<SchedulingTask> tasks, double eps) {
        List<Tupel<SchedulingTask, SchedulingTask>> gaps = new ArrayList<>();
        for (int ti = 0; ti < tasks.size() - 1; ++ti) {
            SchedulingTask parent = tasks.get(ti);
            SchedulingTask child = tasks.get(ti + 1);
            double et = parent.getEndTime();
            double st = child.getStartTime();
            if (st - et > eps) {
                gaps.add(new Tupel<>(parent, child));
            }
        }
        return gaps;
    }

    public static boolean isSequentialRelation(SchedulingTask parentTask,
            SchedulingTask childTask) {
        Collection<SchedulingTask> parentsOfChild = childTask.getParents();

        Collection<SchedulingTask> childrenOfParent = parentTask.getChildren();
        return parentsOfChild.size() == 1 && childrenOfParent.size() == 1 && childrenOfParent.contains(childTask);
    }

    public static double getGap(Lane catcher, Lane thrower, double throwerShift) {
        double newCatcherEnd = catcher.getEndTime();
        double newThrowerStart = thrower.getStartTime() + throwerShift;
        double gap = newThrowerStart - newCatcherEnd;
        if (gap < -Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException("gap < 0 for catcher= " + catcher + " and thrower= " + thrower);
        }
        return gap;
    }

    public static double getExecutionTimeOfLastPathUsingSdlFix(Lane o1, InstanceSize newInstanceSize) {
        SchedulingTaskList o1LP = o1.getUmodLastPath();
        double o1LpExecutionTime;

        if (o1.getInstanceSize() != newInstanceSize) {
            ScaledPseudoLane newO1 = o1.tryScalingIdlFix(newInstanceSize, o1LP);
            if (newO1 != null) {
                // newO1.catchUpForNewSize();
                o1LpExecutionTime = newO1.getExecutionTime();
            } else {
                o1LpExecutionTime = -1;
            }
        } else {
            o1LpExecutionTime = o1LP.getExecutionTime();
        }
        return o1LpExecutionTime;
    }

    public static boolean throwerStartsAfterCatcherEnds(double throwerStartTime, double throwerShift, double catcherEndTime) {
        double throwerST = throwerStartTime + throwerShift;
        double catcherET = catcherEndTime;
        double diff = throwerST - catcherET;
        return diff > -Util.DOUBLE_THRESHOLD;
    }

    public static double calcNewTaskEst(double taskTime, Collection<SchedulingTask> tasksOnInstance, SchedulingTask ti) {
        double estti = ti.getStartTime();
        // consider unscheduled parents
        Collection<SchedulingTask> parentNodes = ti.getParents();
        for (SchedulingTask parent : parentNodes) {
            if (!tasksOnInstance.contains(parent)) {
                double value = parent.getEndTime();
                if (taskTime < value) {
                    taskTime = value;

                    if (value - estti > Util.DOUBLE_THRESHOLD) {
                        throw new RuntimeException("Wrong EST! task=" + ti
                                + " unscheduled parent=" + parent + " est="
                                + estti + " latestParentendtime=" + value);
                    }
                }
            }
        }
        return taskTime;
    }

    public static double calcNewTaskEstIgnoreTaskOnOwnInstance(double taskTime, SchedulingTask ti) {
        double estti = ti.getStartTime();
        // consider unscheduled parents
        Collection<SchedulingTask> parentNodes = ti.getParents();
        for (SchedulingTask parent : parentNodes) {
            if (parent.getLane() != ti.getLane()) {
                double value = parent.getEndTime();
                if (taskTime < value) {
                    taskTime = value;

                    if (value - estti > Util.DOUBLE_THRESHOLD) {
                        throw new RuntimeException("Wrong EST! task=" + ti
                                + " unscheduled parent=" + parent + " est="
                                + estti + " latestParentendtime=" + value);
                    }
                }

            }
        }
        return taskTime;
    }

}
