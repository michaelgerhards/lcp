package algorithm;

import static statics.initialization.TaskStatus.READY;
import static statics.initialization.TaskStatus.WAITING;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

import statics.initialization.SchedulingTask;
import statics.util.Util;
import static statics.initialization.WorkflowInstance.performanceMode;

public abstract class PathAlgorithm extends AbstractAlgorithm {

    public PathAlgorithm() {
        super();
    }

    public PathAlgorithm(Logger logger) {
        super(logger);
    }

    protected void updateDependentTasks(List<SchedulingTask> assignPath) {
        for (SchedulingTask tii : assignPath) {
            updateDependentTasks(tii);
        }
    }

    private void updateDependentTasks(SchedulingTask tii) {
        updateEST_EFTforSuccessors(tii);
        tii.setLatestEndTime(-1.);
        AbstractAlgorithm.calcLFT(tii);
        updateLFTforPredecessors(tii);
    }

    private void updateEST_EFTforSuccessors(SchedulingTask current) {
        Collection<SchedulingTask> children = current.getChildren();
        for (SchedulingTask child : children) {
            if (child.getLane() == null) {
                double oldEst = child.getStartTime();
                child.setStartTime(-1);
                double newEst = calcEST(child);
                if (Math.abs(newEst - oldEst) > Util.DOUBLE_THRESHOLD) {
                    child.setEndTime(child.getStartTime() + getCloudUtil().getFastestExecutionTime(child));
                    updateEST_EFTforSuccessors(child);
                }
            }
        }
    }

    private void updateLFTforPredecessors(SchedulingTask current) {
        Collection<SchedulingTask> parents = current.getParents();
        for (SchedulingTask parent : parents) {
            if (parent.getLane() == null) {
                double oldLft = parent.getLatestEndTime();
                parent.setLatestEndTime(-1.);
                double newLft = AbstractAlgorithm.calcLFT(parent);
                if (Math.abs(newLft - oldLft) > Util.DOUBLE_THRESHOLD) {
                    updateLFTforPredecessors(parent);
                }
            }
        }
    }

    public static List<SchedulingTask> getCriticalPath(SchedulingTask current) {
        List<SchedulingTask> pcp = new ArrayList<SchedulingTask>();
        SchedulingTask ti = current;
        while (hasUnassignedParent(ti)) {
            SchedulingTask criticalParent = getCriticalParent(ti);
            pcp.add(0, criticalParent);
            ti = criticalParent;
        }
        pcp = performanceMode ? pcp : Collections.unmodifiableList(pcp);
        return pcp;
    }

    public static boolean hasUnassignedParent(SchedulingTask current) {
        Collection<SchedulingTask> parents = current.getParents();
        for (SchedulingTask parent : parents) {
            if (parent == parent.getWorkflow().getEntry()) {
                // entry is assigned!
                continue;
            }

            if (current != current.getWorkflow().getExit()) {
                if (parent.getLane() == null) {

                    if (parent.getStatus() == WAITING || parent.getStatus() == READY) {
                        return true;
                    }
                }
            } else {
                // current is last node
                // XXX why distinguish???
                Collection<SchedulingTask> children = parent.getChildren();
                if (children.size() == 1) {
                    if (parent.getLane() == null) {
                        if (parent.getStatus() == WAITING || parent.getStatus() == READY) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static SchedulingTask getCriticalParent(SchedulingTask current) {
        Collection<SchedulingTask> parents = current.getParents();
        SchedulingTask criticalParent = null;
        double maxValue = -1;
        for (SchedulingTask parent : parents) {
            if (parent.getLane() == null) {
                if (parent.getStatus() == WAITING || parent.getStatus() == READY) {
                    double value = parent.getEndTime();
                    if (value > maxValue) {
                        maxValue = value;
                        criticalParent = parent;
                    }
                }
            }
        }
        return criticalParent;
    }

}
