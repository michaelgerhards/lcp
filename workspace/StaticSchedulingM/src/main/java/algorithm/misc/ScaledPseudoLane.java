package algorithm.misc;

import java.util.Collection;
import java.util.Map;

import cloud.InstanceSize;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.initialization.impl.SchedulingTaskList;
import statics.util.Duration;
import statics.util.Util;

public class ScaledPseudoLane implements Duration {

    private final SchedulingTaskList originalTasks;
    private final Map<SchedulingTask, Double> newStartTime;
    private final boolean gap;

    public ScaledPseudoLane(SchedulingTaskList originalTasks,
            Map<SchedulingTask, Double> newStartTime, boolean gap) {
        this.originalTasks = originalTasks;
        this.newStartTime = newStartTime;
        this.gap = gap;
    }

    public double getRSWfixOfParameter(Lane lane) {
        double rswFix = Double.MAX_VALUE;
        for (SchedulingTask task : lane.getUmodTasks()) {
            double tswFix = calcTSWfix(task.getEndTime(), task.getChildren(),
                    lane.getUmodTasks());
            if (tswFix < rswFix) {
                rswFix = tswFix;
                if (rswFix < Util.DOUBLE_THRESHOLD) {
                    rswFix = 0;
                    break;
                }
            }
        }
        return rswFix;
    }

    public boolean satisfyNewStartTimeFix(double startTime) {
        double time = startTime;
        for (SchedulingTask task : originalTasks) {
            double st = getStartTimeOfTask(task);
            double et = getEndTimeOfTask(task);
            double reqSWfix = time - st;
            if (reqSWfix > Util.DOUBLE_THRESHOLD) {
                double avSWfix = calcTSWfix(et, task.getChildren(),
                        originalTasks);
                if (reqSWfix - avSWfix > Util.DOUBLE_THRESHOLD) {
                    return false;
                }
            } else {
                reqSWfix = 0;
            }
            time = et + reqSWfix;
        }
        return true;
    }

    public boolean satisfyNewStartTimeFlex(double startTime) {
        double time = startTime;
        for (SchedulingTask task : originalTasks) {
            double st = getStartTimeOfTask(task);
            double et = getEndTimeOfTask(task);
            double reqSWflex = time - st;
            if (reqSWflex > Util.DOUBLE_THRESHOLD) {
                double avSWflex = calcTSWflex(et, task.getChildren(),
                        originalTasks); // TODO statics?
                if (reqSWflex - avSWflex > Util.DOUBLE_THRESHOLD) {
                    return false;
                }
            } else {
                reqSWflex = 0;
            }
            time = et + reqSWflex;
        }
        return true;
    }

    public double getRSWfix() {
        double rswFix = Double.MAX_VALUE;
        for (SchedulingTask task : originalTasks) {
            double tswFix = calcTSWfix(getEndTimeOfTask(task),
                    task.getChildren(), originalTasks);
            if (tswFix < rswFix) {
                rswFix = tswFix;
                if (rswFix < Util.DOUBLE_THRESHOLD) {
                    rswFix = 0;
                    break;
                }
            }
        }
        return rswFix;
    }

    public double calcTSWfix(double taskEndTime,
            Collection<SchedulingTask> children,
            Collection<SchedulingTask> notLimitingTasks) {
        double idlFix = calcTIDLfix(taskEndTime, children, notLimitingTasks);
        double tSWfix = idlFix - taskEndTime;
        return tSWfix;
    }

    private double calcTIDLfix(double taskEndTime,
            Collection<SchedulingTask> children,
            Collection<SchedulingTask> notLimitingTasks) {
        double idlFix = originalTasks.getWorkflow().getDeadline();
        for (SchedulingTask child : children) {
            if (!notLimitingTasks.contains(child)) {
                double childStartTime;
                if (newStartTime.containsKey(child)) {
                    childStartTime = newStartTime.get(child);
                } else {
                    childStartTime = child.getStartTime();
                }
                if (childStartTime < idlFix) {
                    idlFix = childStartTime;
                    if (Math.abs(idlFix - taskEndTime) < Util.DOUBLE_THRESHOLD) {
                        idlFix = taskEndTime;
                        break;
                    }
                }
            }
        }
        return idlFix;
    }

    public double getRSWflex() {
        double rswFlex = Double.MAX_VALUE;
        for (SchedulingTask task : originalTasks) {
            double tswFlex = calcTSWflex(getEndTimeOfTask(task),
                    task.getChildren(), originalTasks);
            if (tswFlex < rswFlex) {
                rswFlex = tswFlex;
                if (rswFlex < Util.DOUBLE_THRESHOLD) {
                    rswFlex = 0;
                    break;
                }
            }

        }
        return rswFlex;
    }

    public double calcTSWflex(double taskEndTime,
            Collection<SchedulingTask> children,
            Collection<SchedulingTask> notLimitingTasks) {
        double idlFlex = calcTIDLflex(taskEndTime, children, notLimitingTasks);
        double tSWflex = idlFlex - taskEndTime;
        return tSWflex;
    }

    private double calcTIDLflex(double taskEndTime,
            Collection<SchedulingTask> children,
            Collection<SchedulingTask> notLimitingTasks) {
        double idlFlex = originalTasks.getWorkflow().getDeadline();
        for (SchedulingTask child : children) {
            if (!notLimitingTasks.contains(child)) {
                double childStartTime;
                if (newStartTime.containsKey(child)) {
                    childStartTime = newStartTime.get(child);
                } else {
                    childStartTime = child.getStartTime();
                }

                if (child != originalTasks.getWorkflow().getExit()) {
                    Lane childLane = child.getLane();
                    double rsWflex = childLane.getRSWflex();
                    childStartTime += rsWflex;
                }

                if (childStartTime < idlFlex) {
                    idlFlex = childStartTime;
                    if (Math.abs(idlFlex - taskEndTime) < Util.DOUBLE_THRESHOLD) {
                        idlFlex = taskEndTime;
                        break;
                    }
                }
            }
        }
        return idlFlex;
    }

    public double getStartTimeOfTask(SchedulingTask task) {
        return newStartTime.get(task);
    }

    public double getEndTimeOfTask(SchedulingTask task) {
        double st = getStartTimeOfTask(task);
        double ex = task.getExecutionTime(getInstanceSize());
        double et = st + ex;
        return et;
    }

    public double getEndTime() {
        SchedulingTask task = originalTasks.get(originalTasks.size() - 1);
        return getEndTimeOfTask(task);
    }

    public double getStartTime() {
        return newStartTime.get(getOriginalTasks().get(0));
    }

    public double getExecutionTime() {
        return getEndTime() - getStartTime();
    }

    public InstanceSize getInstanceSize() {
        return originalTasks.getInstanceSize();
    }

    public SchedulingTaskList getOriginalTasks() {
        return originalTasks;
    }

    public boolean hasGap() {
        return gap;
    }

}
