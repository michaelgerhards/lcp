package statics.util;

import java.util.List;

import cloud.InstanceSize;
import statics.initialization.SchedulingTask;

public class CloudUtil {

    private static CloudUtil instance;

    public static final CloudUtil getInstance(List<InstanceSize> sizes) {
        instance = new CloudUtil(sizes);
        return instance;
    }

    public static final CloudUtil getInstance() {
        return instance;
    }

    private final InstanceSize fastestSize;
    private final InstanceSize cheapestSize;
    private final InstanceSize dummySize;
    private final InstanceSize mostProfitableSzie;

    private final List<InstanceSize> sizes;

    public CloudUtil(List<InstanceSize> sizes) {
        this.sizes = sizes;
        InstanceSize fastest = null;
        InstanceSize cheapest = null;
        InstanceSize dummy = null;
        InstanceSize mostProfitableSzie = null;

        for (InstanceSize size : sizes) {
            if (size.isDummy()) {
                dummy = size;
                continue;
            }

            if (fastest == null) {
                fastest = size;
            } else if (size.getSpeedup() > fastest.getSpeedup()) {
                fastest = size;
            }

            if (cheapest == null) {
                cheapest = size;
            } else if (size.getCostPerTimeInterval() < cheapest.getCostPerTimeInterval()) {
                cheapest = size;
            }

            if (mostProfitableSzie == null) {
                mostProfitableSzie = size;
            } else if (size.getSpeedup() / size.getCostPerTimeInterval() > mostProfitableSzie.getSpeedup() / mostProfitableSzie.getCostPerTimeInterval()) {
                mostProfitableSzie = size;
            }

        }
        fastestSize = fastest;
        cheapestSize = cheapest;
        dummySize = dummy;
        this.mostProfitableSzie = mostProfitableSzie;
    }

    public InstanceSize getFastestSize() {
        return fastestSize;
    }

    public InstanceSize getMostProfitableSzie() {
        return mostProfitableSzie;
    }

    public InstanceSize nextSize(InstanceSize current) {
        boolean found = false;
        for (InstanceSize next : sizes) {
            if (found) {
                return next;
            }
            if (next == current) {
                found = true;
            }
        }
        return null;
    }

    public double getFastestExecutionTime(SchedulingTask task) {
        if (task == task.getWorkflow().getEntry() || task == task.getWorkflow().getExit()) {
            return 0;
        } else {
            return task.getExecutionTime(fastestSize);
        }
    }

    public InstanceSize getFastestSize(SchedulingTask task) {
        if (task == task.getWorkflow().getEntry() || task == task.getWorkflow().getExit()) {
            return dummySize;
        } else {
            return fastestSize;
        }
    }

    public InstanceSize getCheapestSize() {
        return cheapestSize;
    }

}
