package algorithm.cheapestAfastest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import algorithm.pcp.CriticalPathAlgorithm;
import statics.initialization.SchedulingTask;

public class CheapestAlgorithm extends CriticalPathAlgorithm {

	@Override
	public void scheduleIntern() {
		calcLFT(getWorkflow().getEntry());
		
		List<SchedulingTask> scheduled = calcExecutionOrder();
		// System.out.println("path: " + formatList(path));
		assignPath(scheduled, true, false);

		getWorkflow().getExit().setStartTime(getWorkflow().getDeadline());
		getWorkflow().getExit().setEndTime(getWorkflow().getDeadline());

		getVisualizer().printSchedulingTable();
	}

	private List<SchedulingTask> calcExecutionOrder() {
		List<SchedulingTask> readyForSchedule = new LinkedList<SchedulingTask>(
				getWorkflow().getEntry().getChildren());
		List<SchedulingTask> scheduled = new ArrayList<SchedulingTask>(
				getWorkflow().getTasks().size() + 1);

		while (!readyForSchedule.isEmpty()) {
			SchedulingTask task = readyForSchedule.remove(0);
			if (task == getWorkflow().getExit()) {
				continue;
			}

			Collection<SchedulingTask> children = task.getChildren();
			scheduled.add(task);
			for (SchedulingTask child : children) {
				// all parents must be scheduled
				Collection<SchedulingTask> parents = child.getParents();
				boolean parentsReady = true;
				for (SchedulingTask parent : parents) {
					if (parent == getWorkflow().getEntry()) {
						continue;
					} else if (!scheduled.contains(parent)) {
						parentsReady = false;
						break;
					}
				}
				// child is not scheduled
				// child is not ready for schedule
				if (parentsReady && !readyForSchedule.contains(child)
						&& child.getResource() == null
						&& !scheduled.contains(child)) {
					readyForSchedule.add(child);
				}
			}

		}
		return scheduled;
	}

	@Override
	public String getAlgorithmName() {
		return "Cheapest Algorithm";
	}

	@Override
	public String getAlgorithmNameAbbreviation() {
		return "CHEAP";
	}

}
