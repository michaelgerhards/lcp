package statics.initialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SchedTaskManager {

    // TODO replace with hash collections
    private final List<SchedulingTask> all = new ArrayList<>();
    private final List<SchedulingTask> waiting = new ArrayList<>();
    private final List<SchedulingTask> ready = new LinkedList<>();
    private final List<SchedulingTask> running = new ArrayList<>();
    private final List<SchedulingTask> completed = new ArrayList<>();

    private final List<SchedulingTask> waitingView = Collections.unmodifiableList(waiting);
    private final List<SchedulingTask> readyView = Collections.unmodifiableList(ready);
    private final List<SchedulingTask> runningView = Collections.unmodifiableList(running);
    private final List<SchedulingTask> completedView = Collections.unmodifiableList(completed);
    private final List<SchedulingTask> allView = Collections.unmodifiableList(all);

    public SchedTaskManager(Collection<SchedulingTask> tasks) {
        all.addAll(tasks);
        waiting.addAll(tasks);
    }

    public void goReady(SchedulingTask task) {
        boolean exist = waiting.remove(task);
        if (!exist) {
            throw new RuntimeException(task.toString());
        }
        ready.add(task);
    }

    public void goRunning(SchedulingTask task) {
        boolean exist = ready.remove(task);
        if (!exist) {
            throw new RuntimeException(task.toString());
        }
        running.add(task);
    }

    public void goCompleted(SchedulingTask task) {
        boolean exist = running.remove(task);
        if (!exist) {
            throw new RuntimeException(task.toString());
        }
        completed.add(task);
    }

    public List<SchedulingTask> getWaiting() {
        return waitingView;
    }

    public List<SchedulingTask> getReady() {
        return readyView;
    }

    public List<SchedulingTask> getRunning() {
        return runningView;
    }

    public List<SchedulingTask> getCompleted() {
        return completedView;
    }

    public List<SchedulingTask> getAll() {
        return allView;
    }

}
