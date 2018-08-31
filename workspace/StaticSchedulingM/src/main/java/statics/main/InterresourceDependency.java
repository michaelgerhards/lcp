package statics.main;

import java.util.HashMap;
import java.util.Map;

import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.initialization.impl.Lane;

public class InterresourceDependency {

	public int countInterresourceDependency(WorkflowInstance instance) {
		int interresourceDependency = 0;
		for (Lane lane : instance.getLanes()) {
			for (SchedulingTask task : lane.getUmodTasks()) {
				for (SchedulingTask child : task.getChildren()) {
					if (child != instance.getExit()) {
						Lane childLane = child.getLane();
						if (childLane != lane) {
							interresourceDependency++;
						}
					}
				}
			}
		}
		return interresourceDependency;
	}

	public int countIntraresourceDependencies(WorkflowInstance instance) {
		Map<SchedulingTask, Map<SchedulingTask, Boolean>> connected = new HashMap<>();
		int intraresourceDependency = 0;
		for (Lane lane : instance.getLanes()) {
			for (int i = 0; i < lane.getTasksCount(); ++i) {
				SchedulingTask src = lane.getUmodTasks().get(i);
				for (int j = i + 1; j < lane.getTasksCount(); ++j) {
					SchedulingTask dest = lane.getUmodTasks().get(j);
					boolean conn = connected(src, dest, connected);
					if (!conn) {
						intraresourceDependency++;
					}
				}
			}
		}
		return intraresourceDependency;
	}

	private boolean connected(SchedulingTask src, SchedulingTask dest,
			Map<SchedulingTask, Map<SchedulingTask, Boolean>> connected) {

		// directly connected
		if (src.getChildren().contains(dest.getId())) {
			establishConnection(src, dest, connected, true);
			return true;
		}

		// look in cache
		if (connected.containsKey(src)) {
			Map<SchedulingTask, Boolean> successors = connected.get(src);
			if (successors.containsKey(dest)) {
				Boolean areConnected = successors.get(dest);
				return areConnected;
			}
		}

		// look recursive for children
		for (SchedulingTask child : src.getChildren()) {
			if (connected(child, dest, connected)) {
				establishConnection(src, dest, connected, true);
				return true;
			}
		}

		// not connected
		establishConnection(src, dest, connected, false);
		return false;
	}

	private void establishConnection(SchedulingTask src, SchedulingTask dest,
			Map<SchedulingTask, Map<SchedulingTask, Boolean>> connected,
			boolean result) {
		Map<SchedulingTask, Boolean> successors;
		if (connected.containsKey(src)) {
			successors = connected.get(src);
		} else {
			successors = new HashMap<SchedulingTask, Boolean>();
			connected.put(src, successors);
		}
		successors.put(dest, result);
	}

}
