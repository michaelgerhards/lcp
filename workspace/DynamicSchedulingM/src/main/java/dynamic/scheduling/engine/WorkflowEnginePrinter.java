package dynamic.scheduling.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import reality.RealJob;
import reality.RealJobStatus;
import reality.RealResourceStatus;
import statics.initialization.SchedulingTask;
import statics.util.BillingUtil;
import statics.util.Debug;
import cloud.BasicInstance;
import cloud.InstanceStatus;

public class WorkflowEnginePrinter {

	private final WorkflowEngineImpl engine;

	public WorkflowEnginePrinter(WorkflowEngineImpl engine) {
		this.engine = engine;
	}

	<T> String getSchedulingPlan(Collection<BasicInstance<T>> insts) {
		StringBuffer buf1 = new StringBuffer(10000);
		List<BasicInstance<T>> sorted = new ArrayList<BasicInstance<T>>(insts);
		Collections.sort(sorted);
		BillingUtil bu = engine.getBillingUtil();

		String headline = String.format(
				"%5s %5s %15s %6s %3s %7s %7s %10s %10s %10s %s%n", "ID",
				"SIZE", "Status", "cost", "atu", "uc", "ot", "st", "et", "tt",
				"tasks");
		buf1.append(headline);
		for (BasicInstance<T> instance : sorted) {

			double cost, uc, ot, et, tt;
			int atus;
			boolean terminated;
			if (instance.getStatus() == RealResourceStatus.TERMINATED || instance.getStatus() == InstanceStatus.TERMINATED) {
				terminated = true;
			} else {
				terminated = false;
			}

			if (terminated) {
				cost = bu.getCost(instance);
				atus = bu.getUsedATUs(instance.getExecutionTime());
				uc = bu.getUnusedCapacity(instance.getExecutionTime());
				ot = bu.getOvertime(instance.getExecutionTime());
				et = instance.getEndTime();
				tt = instance.getTerminatedTime();
			} else {
				cost = -1;
				atus = -1;
				uc = -1;
				ot = -1;
				et = -1;
				tt = -1;
			}

			String s = String
					.format("%5s %5s %15s %6.2f %3d %7.2f %7.2f %10.2f %10.2f %10.2f %s%n",
							instance.getName(), instance.getInstanceSize()
									.getName(),
							instance.getStatus().toString(), cost, atus, uc,
							ot, instance.getStartTime(), et, tt,
							Arrays.toString(instance.getUmodTasks().toArray()));
			buf1.append(s);
		}
		String str1 = buf1.toString();
		return str1;
	}

	public void printResults() {
		printPlannedTasks();
		printAssignments();
	}

	public void printAssignments() {
		@SuppressWarnings("unchecked")
		String str1 = getSchedulingPlan((Collection) engine.getPlan()
				.getInstances().values());
		@SuppressWarnings("unchecked")
		String str2 = getSchedulingPlan((Collection) engine.getCloudManager()
				.getResourceManager().getInstanceAll().values());

		Debug.INSTANCE
				.aPrintln("------------------------- ASSIGNMENTS ------------------------------------------------");
		if (str1.equals(str2)) {
			Debug.INSTANCE.aPrintln(str1);
			Debug.INSTANCE.aPrintln("equal assignments");
		} else {
			Debug.INSTANCE.aPrintln("Plan");
			Debug.INSTANCE.aPrintln(str1);
			Debug.INSTANCE.aPrintln("Run");
			Debug.INSTANCE.aPrintln(str2);
			Debug.INSTANCE.aPrintln("not equal assignments");
		}
	}

	public void printPlannedTasks() {
		int i = 0;
		final String nl = String.format("%n");
		StringBuffer buf1 = new StringBuffer();
		Debug.INSTANCE.aPrintln("--------- TASKS --------------");
		for (SchedulingTask task : engine.getTaskManager().getAll()) {

			String s = String.format(
					"%5d %10s %25s %10.3f\t%10.3f\t%10.3f %s\t%s", i, task
							.getId(), task.getType(), task.getStartTime(), task
							.getEndTime(), task.getDuration(), task.getStatus()
							.toString(), task.getResource().getName());
			buf1.append(s);
			buf1.append(nl);
			// Debug.INSTANCE.println(s);
			i++;
		}
		String s1 = buf1.toString();
		buf1 = null;
		Debug.INSTANCE.aPrintln(s1);
		i = 0;
		StringBuffer buf2 = new StringBuffer();
		Debug.INSTANCE.aPrintln("--------- JOBS --------------");
		for (RealJob task : engine.getAllJobs()) {

			String s;
			if (task.getStatus() == RealJobStatus.COMPLETED) {
				s = String.format(
						"%5d %10s %25s %10.3f\t%10.3f\t%10.3f %s\t%s", i, task
								.getId(), task.getType(), task.getStartTime(),
						task.getEndTime(), task.getDuration(), task.getStatus()
								.toString(), task.getResource().toString());
			} else {
				s = String.format("%5d %10s %25s %10.3f\t%10s\t%10s %s\t%s", i,
						task.getId(), task.getType(), task.getStartTime(),
						"???", "???", task.getStatus().toString(), task
								.getResource().toString());

			}

			buf2.append(s);
			buf2.append(nl);
			i++;
		}
		String s2 = buf2.toString();
		buf2 = null;

		if (s1.equals(s2)) {
			Debug.INSTANCE.aPrintln("equal");
		} else {
			Debug.INSTANCE.aPrintln("WARNING!!!");
			Debug.INSTANCE.aPrintln(s2);
		}

	}
}
