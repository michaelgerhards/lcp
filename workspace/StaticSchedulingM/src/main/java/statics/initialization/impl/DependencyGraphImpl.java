package statics.initialization.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import statics.initialization.DependencyGraph;
import statics.initialization.DependencyTask;

class DependencyGraphImpl implements Serializable, DependencyGraph {

    private static final long serialVersionUID = 1209043213925839808L;
    private final String name;
    private Map<Integer, DependencyTaskImpl> tasks;
    private boolean isFinalized = false;

    public DependencyGraphImpl(String name) {
        this.name = name;
        tasks = new HashMap<>();
    }

    @Override
    public DependencyTask getEntry() {
        return tasks.get(ENTRY_ID);
    }

    @Override
    public DependencyTask getExit() {
        return tasks.get(EXIT_ID);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<Integer, DependencyTask> getTasks() {
        @SuppressWarnings("rawtypes")
        Map t1 = (Map) tasks;
        @SuppressWarnings("unchecked")
        Map<Integer, DependencyTask> t2 = (Map<Integer, DependencyTask>) t1;
        return t2;
    }

    @Override
    public int getTasksCount() {
        return tasks.size();
    }

    void createTask(int id, int type) {
        checkFinalized();
        DependencyTaskImpl task = new DependencyTaskImpl(id, type);
        tasks.put(task.getId(), task);
    }

    void doFinalize() {
        checkFinalized();
        tasks = Collections.unmodifiableMap(tasks);
        for (DependencyTaskImpl task : tasks.values()) {
            task.doFinalize();
        }
        isFinalized = true;
    }

    void createArtificialTasks() {
        checkFinalized();
        if (tasks.containsKey(ENTRY_ID) || tasks.containsKey(EXIT_ID)) {
            throw new RuntimeException("entry and exit tasks already exist.");
        }

        createTask(ENTRY_ID, ENTRY_ID);
        createTask(EXIT_ID, EXIT_ID);

        DependencyTaskImpl entry = tasks.get(ENTRY_ID);
        DependencyTaskImpl exit = tasks.get(EXIT_ID);

        for (DependencyTaskImpl task : tasks.values()) {
            if (task != entry && task != exit && task.getParents().size() == 0) {
                createDependency(entry, task);
            }
            if (task != entry && task != exit && task.getChildren().size() == 0) {
                createDependency(task, exit);
            }
        }
    }

    private void checkFinalized() {
        if (isFinalized) {
            throw new RuntimeException("Graph already finalized");
        }
    }

    void createDependency(int parentId, int childId) {
        DependencyTaskImpl child = tasks.get(childId);
        DependencyTaskImpl parent = tasks.get(parentId);
        createDependency(parent, child);
    }

    void createDependency(DependencyTaskImpl parent, DependencyTaskImpl child) {
        checkFinalized();
        child.addParent(parent);
    }

    void cap() {
        checkFinalized();
        Map<DependencyTask, Map<DependencyTask, Boolean>> connected = new HashMap<>();
        List<DependencyTask[]> toDelete = new ArrayList<>(getTasksCount());
        // try to go from x to z over y.
        // if possible: transition x-z is transitive.
        for (DependencyTask x : tasks.values()) {
            for (DependencyTask y : x.getChildren().values()) {
                for (DependencyTask z : x.getChildren().values()) {
                    if (y != z && connected(y, z, connected)) {
                        toDelete.add(new DependencyTask[]{x, z});
                    }
                }
            }
        }
        for (DependencyTask[] del : toDelete) {
            DependencyTaskImpl parent = (DependencyTaskImpl) del[0];
            DependencyTaskImpl child = (DependencyTaskImpl) del[1];
            if (child.getParents().containsKey(parent.getId())) {
                child.removeParent(parent);
            }
        }
    }

    void calcAnchestorsAndDescendants() {
        checkFinalized();
        List<DependencyTask> postOrder = calcTopOrder();
        // from exit to entry
        for (DependencyTask task : postOrder) {
            DependencyTaskImpl taskI = (DependencyTaskImpl) task;
            for (DependencyTask child : task.getChildren().values()) {
                taskI.addDescendant((DependencyTaskImpl) child);
                for (DependencyTask desc : child.getDescendants().values()) {
                    taskI.addDescendant((DependencyTaskImpl) desc);
                }
            }
        }
    }

    private List<DependencyTask> calcTopOrder() {
        List<DependencyTask> postOrder = new ArrayList<>();
        Set<DependencyTask> marked = new HashSet<>();
        dfs(tasks.get(ENTRY_ID), marked, postOrder);
        return postOrder;
    }

    private static void dfs(DependencyTask task, Set<DependencyTask> marked, List<DependencyTask> postOrder) {
        marked.add(task);
        task.getChildren().values().stream().filter((child) -> (!marked.contains(child))).forEach((child) -> {
            dfs(child, marked, postOrder);
        });
        postOrder.add(task);
    }

    private boolean connected(DependencyTask src, DependencyTask dest, Map<DependencyTask, Map<DependencyTask, Boolean>> connected) {
        // directly connected
        if (src.getChildren().containsKey(dest.getId())) {
            establishConnection(src, dest, connected, true);
            return true;
        }

        // look in cache
        if (connected.containsKey(src)) {
            Map<DependencyTask, Boolean> successors = connected.get(src);
            if (successors.containsKey(dest)) {
                Boolean areConnected = successors.get(dest);
                return areConnected;
            }
        }

        // look recursive for children
        for (DependencyTask child : src.getChildren().values()) {
            if (connected(child, dest, connected)) {
                establishConnection(src, dest, connected, true);
                return true;
            }
        }

        // not connected
        establishConnection(src, dest, connected, false);
        return false;
    }

    private void establishConnection(DependencyTask src, DependencyTask dest, Map<DependencyTask, Map<DependencyTask, Boolean>> connected, boolean result) {
        Map<DependencyTask, Boolean> successors;
        if (connected.containsKey(src)) {
            successors = connected.get(src);
        } else {
            successors = new HashMap<>();
            connected.put(src, successors);
        }
        successors.put(dest, result);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(name);
        buf.append(System.lineSeparator());

        List<DependencyTask> sorted = new ArrayList<>(tasks.values());
        Collections.sort(sorted);

        sorted.stream().map((t) -> {
            buf.append(t.toString());
            return t;
        }).forEach((_item) -> {
            buf.append(System.lineSeparator());
        });

        String s = buf.toString();
        return s;
    }

}
