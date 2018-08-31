package reality;

import java.util.PriorityQueue;
import java.util.Queue;

import statics.util.Util;

public class Time {

    private static Time instance;

    public static Time startInstance() {
        if (instance != null) {
            throw new RuntimeException();
        }
        instance = new Time();
        return instance;
    }

    public static void stopInstance() {
        instance = null;
    }

    public static Time getInstance() {
        if (instance == null) {
            throw new RuntimeException();
        }
        return instance;
    }

    private Time() {
        // singleton
    }

    private final Queue<QueueEvent> events = new PriorityQueue<QueueEvent>();
    private double actualTime = 0;

    public double getActualTime() {
        return actualTime;
    }

    public QueueEvent pollNextEvent() {
        QueueEvent event = events.poll();
        double eventTime = event.getTime();
        setActualTime(eventTime);
        return event;
    }

    private void setActualTime(double actualTime) {
        if (actualTime < this.actualTime) {
            throw new RuntimeException("" + actualTime);
        }
        this.actualTime = actualTime;
    }

    public void addEvent(QueueEvent event) {
        if (event.getTime() - actualTime < -Util.DOUBLE_THRESHOLD) {
            throw new RuntimeException(
                    "Event placed in past: " + event + " at " + event.getTime() + " present= " + actualTime);
        }
        events.add(event);
    }

    public boolean hasFurtherEvents() {
        return !events.isEmpty();
    }

    public boolean isInFuture(double time) {
        boolean value = time - actualTime > -Util.DOUBLE_THRESHOLD;
        return value;
    }

    public boolean isInPast(double time) {
        return !isInFuture(time);
    }
}
