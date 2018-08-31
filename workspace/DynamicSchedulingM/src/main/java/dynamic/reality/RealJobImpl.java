package dynamic.reality;

import reality.EventHandler;
import reality.EventType;
import reality.RealJob;
import reality.RealJobStatus;
import reality.Time;
import reality.executiontimes.JobExecutionTimeManager;
import statics.initialization.SchedulingTask;
import statics.initialization.TaskStatus;
import statics.util.Util;

public class RealJobImpl implements Comparable<RealJob>, RealJob {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((task == null) ? 0 : task.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RealJobImpl other = (RealJobImpl) obj;
        if (task == null) {
            if (other.task != null) {
                return false;
            }
        } else if (!task.equals(other.task)) {
            return false;
        }
        return true;
    }

    private final SchedulingTask task;
    private final double endTime;
    private final double exTime;
    private final RealResourceImpl instance;
    private final double startTime;

    static RealJobImpl startJob(SchedulingTask task, RealResourceImpl instance, double startTime, EventHandler jobCompletionHandler) {
        return new RealJobImpl(task, instance, startTime, jobCompletionHandler);
    }

    private RealJobImpl(SchedulingTask task, RealResourceImpl instance, double startTime, EventHandler jobCompletionHandler) {
        if (task.getStatus() != TaskStatus.READY) {
            throw new RuntimeException(task.getStatus().toString());
        }
        if (!Time.getInstance().isInFuture(startTime)) {
            throw new RuntimeException();
        }

        this.startTime = startTime;
        this.task = task;
        this.instance = instance;

        // times
        exTime = JobExecutionTimeManager.getInstance().getExecutionTime(task, instance.getInstanceSize());
        endTime = startTime + exTime;

        // events
        QueueEventImpl ev = new QueueEventImpl(endTime, EventType.JOB_COMPLETED, this, jobCompletionHandler);

        Time.getInstance().addEvent(ev);

    }

    @Override
    public double getExecutionTime() {
        if (getStatus() == RealJobStatus.COMPLETED) {
            return exTime;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public RealJobStatus getStatus() {
        if (Time.getInstance().getActualTime() - endTime > -Util.DOUBLE_THRESHOLD) {
            return RealJobStatus.COMPLETED;
        } else {
            return RealJobStatus.RUNNING;
        }
    }

    @Override
    public int getId() {
        return getTask().getId();
    }

    @Override
    public int getType() {
        return getTask().getType();
    }

    @Override
    public double getStartTime() {
        return startTime;
    }

    @Override
    public double getEndTime() {
        if (getStatus() == RealJobStatus.COMPLETED) {
            return endTime;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public RealResourceImpl getResource() {
        return instance;
    }

    @Override
    public String toString() {
        return String.valueOf(getId());
    }

    @Override
    public SchedulingTask getTask() {
        return task;
    }

    @Override
    public int compareTo(RealJob o) {
        return getId() - o.getId();
    }
}
