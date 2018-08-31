package statics.initialization;

import java.util.Map;

public interface DependencyTask extends Comparable<DependencyTask> {

    int getId();

    int getType();

    Map<Integer, DependencyTask> getChildren();

    Map<Integer, DependencyTask> getParents();
    
    Map<Integer, DependencyTask> getAnchestors();

    Map<Integer, DependencyTask> getDescendants();

    @Override
    default int compareTo(DependencyTask o) {
        return this.getId() - o.getId();
    }

}
