package statics.initialization.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import statics.initialization.DependencyTask;
import statics.initialization.WorkflowInstance;

final class DependencyTaskImpl implements DependencyTask, Serializable {

    private static final long serialVersionUID = -4734152987274330552L;
    private final int id;
    private final int type;
    private boolean isFinalized = false;
    private Map<Integer, DependencyTask> children;
    private Map<Integer, DependencyTask> parents;
    private Map<Integer, DependencyTask> anchestors;
    private Map<Integer, DependencyTask> descendants;

    DependencyTaskImpl(int id, int type) {
        this.id = id;
        this.type = type;
        children = new HashMap<>();
        parents = new HashMap<>();
        anchestors = new HashMap<>();
        descendants = new HashMap<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getType() {
        return type;
    }

    void doFinalize() {
        checkFinalized();
        isFinalized = true;
        children = Collections.unmodifiableMap(children);
        parents = Collections.unmodifiableMap(parents);
        anchestors = Collections.unmodifiableMap(anchestors);
        descendants = Collections.unmodifiableMap(descendants);
    }

    void addAnchestor(DependencyTaskImpl anchestor) {
        checkFinalized();
        this.anchestors.put(anchestor.getId(), anchestor);
        anchestor.descendants.put(this.getId(), this);
    }
    
     void addDescendant(DependencyTaskImpl descendant) {
        checkFinalized();
        this.descendants.put(descendant.getId(), descendant);
        descendant.anchestors.put(this.getId(), this);
    }

    void addParent(DependencyTaskImpl parent) {
        checkFinalized();
        this.parents.put(parent.getId(), parent);
        parent.children.put(this.getId(), this);
    }

    void removeParent(DependencyTaskImpl parent) {
        checkFinalized();
        this.parents.remove(parent.getId());
        parent.children.remove(this.getId());
    }

    @Override
    public Map<Integer, DependencyTask> getChildren() {
        return children;
    }

    @Override
    public Map<Integer, DependencyTask> getParents() {
        return parents;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(String.format("%s\t", id));
        String typeStr = WorkflowInstance.intToJobName(type);
        buf.append(String.format("%30s\t", typeStr));
        buf.append("parents= [");
        for (DependencyTask p : parents.values()) {
            buf.append(p.getId());
            buf.append(", ");
        }
        buf.append("]\tchildren= [");
        for (DependencyTask c : children.values()) {
            buf.append(c.getId());
            buf.append(", ");
        }
        buf.append("]");
        String s = buf.toString();
        return s;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        DependencyTask other = (DependencyTask) obj;
        return id == other.getId();
    }

    private void checkFinalized() {
        if (isFinalized) {
            throw new RuntimeException("Task already finalized");
        }
    }

    @Override
    public Map<Integer, DependencyTask> getAnchestors() {
        return anchestors;
    }

    @Override
    public Map<Integer, DependencyTask> getDescendants() {
        return descendants;
    }

}
