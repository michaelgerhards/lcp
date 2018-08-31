package statics.initialization.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cloud.InstanceSize;
import java.util.HashSet;
import java.util.Set;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.Debug;
import statics.util.Util;

public class SchedulingTaskList implements List<SchedulingTask>, Serializable {

    private static final long serialVersionUID = 2357779655429653453L;
    private final List<SchedulingTask> tasks;
    private InstanceSize instanceSize;
    private final WorkflowInstance workflow;

    public SchedulingTaskList(List<SchedulingTask> tasks, InstanceSize size, WorkflowInstance workflow) {
        this.tasks = tasks;
        this.workflow = workflow;
        this.instanceSize = size;
    }

    public SchedulingTask getFirst() {
        if (!tasks.isEmpty()) {
            return tasks.get(0);
        }
        return null;
    }

    public SchedulingTask getLast() {
        if (!tasks.isEmpty()) {
            return tasks.get(tasks.size() - 1);
        }
        return null;
    }

    public double getExecutionTime() {
        double et = getEndTime() - getStartTime();
        return et;
    }

    public double getEndTime() {
        if (tasks.isEmpty()) {
            return 0;
        }
        return getLast().getEndTime();
    }

    public double getStartTime() {
        if (tasks.isEmpty()) {
            return 0;
        }
        return getFirst().getStartTime();
    }

    public InstanceSize getInstanceSize() {
        return instanceSize;
    }

    public double getRSWfix() {
        Set<SchedulingTask> tasksSet = new HashSet<>(tasks);
        return Lane.calcRSWfix(tasksSet);
    }

    public double getRSWflex() {
        Set<SchedulingTask> tasksSet = new HashSet<>(tasks);
        return Lane.calcRSWflex(tasksSet, Collections.<Lane>emptyList());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
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
        SchedulingTaskList other = (SchedulingTaskList) obj;
        if (tasks == null) {
            if (other.tasks != null) {
                return false;
            }
        } else if (!tasks.equals(other.tasks)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (Debug.INSTANCE.getDebug() < 0) {
            new RuntimeException("called to string of " + getClass()).printStackTrace(System.out);
            System.exit(0);
        }

        // XXX improve
        return "SchedulingTaskList [tasks=" + tasks + "]";
    }

    @Override
    public int size() {
        return tasks.size();
    }

    @Override
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        checkType(o);
        return tasks.contains(o);
    }

    @Override
    public Iterator<SchedulingTask> iterator() {
        return tasks.iterator();
    }

    @Override
    public Object[] toArray() {
        return tasks.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return tasks.toArray(a);
    }

    @Override
    public boolean add(SchedulingTask e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return tasks.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends SchedulingTask> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends SchedulingTask> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SchedulingTask get(int index) {
        return tasks.get(index);
    }

    @Override
    public SchedulingTask set(int index, SchedulingTask element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, SchedulingTask element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SchedulingTask remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        checkType(o);
        return tasks.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        checkType(o);
        return tasks.lastIndexOf(o);
    }

    @Override
    public ListIterator<SchedulingTask> listIterator() {
        return tasks.listIterator();
    }

    @Override
    public ListIterator<SchedulingTask> listIterator(int index) {
        return tasks.listIterator(index);
    }

    @Override
    public SchedulingTaskList subList(int fromIndex, int toIndex) {
        return new SchedulingTaskList(new ArrayList<>(tasks.subList(fromIndex, toIndex)), instanceSize, workflow);
    }

    private void checkType(Object o) {
        if (!(o instanceof SchedulingTask)) {
            throw new RuntimeException();
        }
    }

    // call by Lane
    void setSize(InstanceSize size) {
        this.instanceSize = size;
    }

    public WorkflowInstance getWorkflow() {
        return workflow;
    }

    public boolean satisfyNewStartTimeFix(double startTime) {
        double time = startTime;
        Set<SchedulingTask> tasksSet = new HashSet<>(tasks);
        for (SchedulingTask task : tasks) {
            double st = task.getStartTime();
            double et = task.getEndTime();
            double reqSWfix = time - st;
            if (reqSWfix > Util.DOUBLE_THRESHOLD) {
                double avSWfix = task.getSWfix(tasksSet);
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
        for (SchedulingTask task : tasks) {
            double st = task.getStartTime();
            double et = task.getEndTime();
            double reqSWflex = time - st;
            if (reqSWflex > Util.DOUBLE_THRESHOLD) {
                double avSWflex = task.getSWflex(Collections.emptySet()); // TODO statics?
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

}
