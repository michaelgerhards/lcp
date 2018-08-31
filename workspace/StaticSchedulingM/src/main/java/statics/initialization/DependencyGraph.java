package statics.initialization;

import java.util.Map;

public interface DependencyGraph {

    public static final int ENTRY_ID = 99998;
    public static final int EXIT_ID = 99999;

    DependencyTask getEntry();

    DependencyTask getExit();

    String getName();

    Map<Integer, DependencyTask> getTasks();

    int getTasksCount();

}
