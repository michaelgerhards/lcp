package dynamic.reality;

import reality.EventHandler;
import statics.initialization.SchedulingTask;
import reality.EventType;
import reality.QueueEvent;
import reality.RealResource;
import reality.Time;

public class QueueEventImpl implements Comparable<QueueEventImpl>, QueueEvent {

    private final double time;
    private final RealJobImpl job;
    private final SchedulingTask task;
    private final EventType type;
    private final RealResource resource;
    private final EventHandler handler;

    public QueueEventImpl(double time, EventType type, RealJobImpl job, EventHandler handler) {
        this.handler = handler;
        this.resource = notNull(job.getResource());
        this.time = setTime(time);
        this.task = null;
        this.job = notNull(job);
        this.type = notNull(type);
    }

    public QueueEventImpl(double time, EventType type, RealResource resource, EventHandler handler) {
        this.handler = handler;
        this.resource = notNull(resource);
        this.time = setTime(time);
        this.task = null;
        this.job = null;
        this.type = notNull(type);
    }

    public QueueEventImpl(double time, EventType type, SchedulingTask task, EventHandler handler) {
        this.handler = handler;
        this.resource = null;
        this.time = setTime(time);
        this.task = notNull(task);
        this.job = null;
        this.type = notNull(type);
    }

    private <T> T notNull(T resource) {
        if (resource == null) {
            throw new NullPointerException();
        } else {
            return resource;
        }
    }

    private double setTime(double time) {
        if (time >= Time.getInstance().getActualTime()) {
            return time;
        } else {
            throw new RuntimeException("time < actualtime: " + time + " vs. " + Time.getInstance().getActualTime());
        }
    }

    @Override
    public RealJobImpl getJob() {
        return job;
    }

    @Override
    public double getTime() {
        return time;
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public int compareTo(QueueEventImpl o) {
        if (o.getTime() < this.time) {
            return 1;
        } else if (o.getTime() > this.time) {
            return -1;
        }
        if (this.getType() == EventType.WORKFLOW_COMPLETED && o.getType() != EventType.WORKFLOW_COMPLETED) {
            return -1;
        } else if (this.getType() != EventType.WORKFLOW_COMPLETED && o.getType() == EventType.WORKFLOW_COMPLETED) {
            return 1;
        }

        if (this.getType() == EventType.TASK_LAST_START_TIME_EXCEEDED && o.getType() != EventType.TASK_LAST_START_TIME_EXCEEDED) {
            return 1;
        } else if (this.getType() != EventType.TASK_LAST_START_TIME_EXCEEDED && o.getType() == EventType.TASK_LAST_START_TIME_EXCEEDED) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "QueueEventImpl [time=" + time + ", job=" + job + ", task=" + task + ", type=" + type + ", resource=" + resource + ", handler=" + handler + "]";
    }

    @Override
    public RealResource getResource() {
        return resource;
    }

    @Override
    public SchedulingTask getTask() {
        return task;
    }

    @Override
    public EventHandler getEventHandler() {
        return handler;
    }

}
