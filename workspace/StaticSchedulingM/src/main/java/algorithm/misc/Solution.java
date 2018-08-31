package algorithm.misc;

import cloud.InstanceSize;
import statics.initialization.WorkflowInstance;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import static statics.util.Util.*;

/**
 *
 * @author mike
 */
public class Solution {

    public final double cost;
    public final Lane resource;
    public final Slot slot;
    public final boolean newresource;
    public final InstanceSize size;
    public final WorkflowInstance workflow;

    public Solution(Lane resource, Slot slot, double cost, boolean newresource, InstanceSize size, WorkflowInstance workflow) {
        this.resource = resource;
        this.slot = slot;
        this.cost = cost;
        this.newresource = newresource;
        this.size = size;
        this.workflow = workflow;
    }

    public boolean betterThan(Solution other) {
        // A solution is better than no solution
        if (other == null) {
            return true;
        }

        // Cheaper solutions are better
        if (this.cost < other.cost) {
            return true;
        } else if (this.cost > other.cost) {
            return false;
        }

        double thisgapsize = this.getGap();
        double othergapsize = other.getGap();

        double diff = thisgapsize - othergapsize;
        if (diff > DOUBLE_THRESHOLD) {
            return false;
        } else if (diff < -DOUBLE_THRESHOLD) {
            return true;
        }

        // do not waste money beyond deadline
        if (!this.newresource && other.newresource) {
            return isBillingEndBeforeDeadline();
        } else if (this.newresource && !other.newresource) {
            return other.isBillingEndBeforeDeadline();
        }

        // instance sizes
        if (size.isFaster(other.size)) {
            return true;
        } else if (other.size.isFaster(size)) {
            return false;
        }

        double thisend = this.slot.start + this.slot.duration;
        double otherend = other.slot.start + other.slot.duration;

        if (thisend < otherend) {
            return true;
        } else if (thisend > otherend) {
            return true;
        }

        // both are on new or both are on existing resources
        // Earlier starts are better
        if (this.slot.start < other.slot.start) {
            return true;
        }
        if (this.slot.start > other.slot.start) {
            return false;
        }

        double thisbtadl = getBillingTimeAfterDeadline();
        double otherbtadl = other.getBillingTimeAfterDeadline();

        if (thisbtadl > otherbtadl) {
            return false;
        } else if (otherbtadl > thisbtadl) {
            return true;
        }

        return true;
    }

    private boolean isBillingEndBeforeDeadline() {
        BillingUtil bu = BillingUtil.getInstance();
        double end = slot.start + slot.duration;
        if (slot.start - resource.getEndTime() > -DOUBLE_THRESHOLD) {
            // after end
            double bet = bu.getBillingEndTime(resource.getStartTime(), end);
            return workflow.getDeadline() - bet > -DOUBLE_THRESHOLD;
        } else if (resource.getStartTime() - end > -DOUBLE_THRESHOLD) {
            // before start
            double bet = bu.getBillingEndTime(slot.start, resource.getEndTime());
            return workflow.getDeadline() - bet > -DOUBLE_THRESHOLD;
        } else {
            // in between
            return true;
        }
    }

    private double getBillingTimeAfterDeadline() {
        BillingUtil bu = BillingUtil.getInstance();
        double end = slot.start + slot.duration;
        if (slot.start - resource.getEndTime() > -DOUBLE_THRESHOLD) {
            // after end
            double bet = bu.getBillingEndTime(resource.getStartTime(), end);
            return Math.max(0, bet - workflow.getDeadline());
        } else if (resource.getStartTime() - end > -DOUBLE_THRESHOLD) {
            // before start
            double bet = bu.getBillingEndTime(slot.start, resource.getEndTime());
            return Math.max(0, bet - workflow.getDeadline());
        } else {
            // in between
            return 0;
        }
    }

    private double getGap() {
        double thisgapsize;
        if (newresource) {
            thisgapsize = 0;
        } else {
            double end = slot.start + slot.duration;
            if (slot.start - resource.getEndTime() > -DOUBLE_THRESHOLD) {
                // after end
                thisgapsize = slot.start - resource.getEndTime();
            } else if (resource.getStartTime() - end > -DOUBLE_THRESHOLD) {
                // before start
                thisgapsize = resource.getStartTime() - end;
            } else {
                // in between, negative gap means closing the gap
                thisgapsize = -slot.duration;
            }
        }
        return thisgapsize;
    }

    @Override
    public String toString() {
        return String.format("Solution c=%10.1f r=%5s slot=%s new=%5s s=%s", cost, (resource != null ? resource.getId() : "new"), slot, newresource, size);
    }

}
