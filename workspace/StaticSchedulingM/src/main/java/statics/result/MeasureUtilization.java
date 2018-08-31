package statics.result;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import statics.util.Debug;
import statics.util.Duration;

public class MeasureUtilization {

    private final SortedMap<Double, UtilizationEvent> events = new TreeMap<>();

    public void clear() {
        events.clear();
    }

    public <T extends Duration> void measure(Collection<T> instances) {
        for (Duration instance : instances) {
            double time = instance.getStartTime();
            UtilizationEvent event = new UtilizationEvent(time, 1);
            double endTime = instance.getEndTime();
            UtilizationEvent endEvent = new UtilizationEvent(endTime, -1);
            put(time, event);
            put(endTime, endEvent);
        }
    }

    public void setDeadline(double time) {
        UtilizationEvent event = new UtilizationEvent(time, 0);
        put(time, event);
    }

    private void put(double time, UtilizationEvent event) {
        if (events.containsKey(time)) {
            UtilizationEvent exEvent = events.remove(time);
            exEvent.type += event.type;
            event = exEvent;
        }
        events.put(time, event);

    }

    public void print() {
        int number = 0;
        int max = 0;
        for (UtilizationEvent event : events.values()) {
            int newNumber = number + event.type;
            if (newNumber > max) {
                max = newNumber;
            }

            double time = event.time;
            Debug.INSTANCE.aPrintf("%10.2f from %3d to %3d%n", time, number, newNumber);
            number = newNumber;
        }
    }

    public int calcMax() {
        int number = 0;
        int max = 0;
        for (UtilizationEvent event : events.values()) {
            number += event.type;
            if (number > max) {
                max = number;
            }
        }
        return max;
    }

    public void printExcel() {

        int newNumber = 0;

        int number = 0;
        Debug.INSTANCE.aPrintf("%10s\t%5s%n", "time", "#res");
        for (UtilizationEvent event : events.values()) {
            newNumber = number + event.type;
            double time = event.time;
            Debug.INSTANCE.aPrintf("%10.2f\t%5d%n", time, number);
            Debug.INSTANCE.aPrintf("%10.2f\t%5d%n", time, newNumber);
//			Debug.INSTANCE.printf(Locale.GERMAN, "%10.2f\t%3d%n", time, number);
//			Debug.INSTANCE.printf(Locale.GERMAN, "%10.2f\t%3d%n", time, newNumber);

            number = newNumber;
        }
    }

}
