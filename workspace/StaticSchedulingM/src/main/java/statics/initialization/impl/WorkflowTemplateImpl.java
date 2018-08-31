package statics.initialization.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import statics.initialization.DependencyGraph;
import statics.initialization.DependencyTask;
import statics.initialization.Planner;
import statics.initialization.SchedulingInformation;
import statics.initialization.WorkflowInstance;
import statics.initialization.WorkflowTemplate;

class WorkflowTemplateImpl implements WorkflowTemplate {

    private final DependencyGraph graph;
    private final SchedulingInformation information;

    WorkflowTemplateImpl(DependencyGraph graph, SchedulingInformation information) {
        this.graph = graph;
        this.information = information;
    }

    private static Map<Integer, SchedulingTaskImpl> generateTasks(DependencyGraph graph, SchedulingInformation information, WorkflowInstanceImpl sgraph) {
        Map<Integer, SchedulingTaskImpl> schedulingTasks = new HashMap<>();
        // create sTasks
        Collection<DependencyTask> dTasks = graph.getTasks().values();

        for (DependencyTask dTask : dTasks) {
            SchedulingTaskImpl task = new SchedulingTaskImpl(dTask.getId(), dTask.getType(), information.getExecutionTimes(dTask), information.getVariances(dTask), sgraph);
            schedulingTasks.put(task.getId(), task);
        }

        Map<Set<Integer>, Set<SchedulingTaskImpl>> cache = new HashMap<>();
        for (SchedulingTaskImpl task : schedulingTasks.values()) {
            DependencyTask dTask = graph.getTasks().get(task.getId());
            Set<SchedulingTaskImpl> children = checkCache(dTask.getChildren().keySet(), cache, schedulingTasks);
            Set<SchedulingTaskImpl> parents = checkCache(dTask.getParents().keySet(), cache, schedulingTasks);
            Set<SchedulingTaskImpl> descendants = checkCache(dTask.getDescendants().keySet(), cache, schedulingTasks);
            Set<SchedulingTaskImpl> anchestors = checkCache(dTask.getAnchestors().keySet(), cache, schedulingTasks);
            task.setOnce(children, parents, descendants, anchestors);
        }
        return Collections.unmodifiableMap(schedulingTasks);
    }

    private static Set<SchedulingTaskImpl> checkCache(Set<Integer> key, Map<Set<Integer>, Set<SchedulingTaskImpl>> cache, Map<Integer, SchedulingTaskImpl> schedulingTasks) {
        Set<SchedulingTaskImpl> result = cache.get(key);
        if (result == null) {
            // cache miss
            Set<SchedulingTaskImpl> values = new HashSet<>();
            for (int i : key) {
                SchedulingTaskImpl t = schedulingTasks.get(i);
                values.add(t);
            }
            result = Collections.unmodifiableSet(values);
            cache.put(key, result);
        }
        return result;
    }

    @Override
    public WorkflowInstance createWorkflowInstance(double deadline, Planner etPlanner) {
        WorkflowInstanceImpl sgraph = new WorkflowInstanceImpl(graph.getName(), information.getAtuLength(), information.getSizes());
        Map<Integer, SchedulingTaskImpl> generateTasks = generateTasks(graph, information, sgraph);
        sgraph.setTasksOnce(generateTasks);
        sgraph.setDeadline(deadline);
        sgraph.setEtPlanner(etPlanner);
        return sgraph;
    }

    @Override
    public void updatePlanner(WorkflowInstance workflow, Planner p) {
        WorkflowInstanceImpl impl = (WorkflowInstanceImpl) workflow;
        impl.setEtPlanner(p);
    }

}
