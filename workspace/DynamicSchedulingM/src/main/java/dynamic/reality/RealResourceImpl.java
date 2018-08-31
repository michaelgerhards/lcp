package dynamic.reality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.util.Util;
import cloud.Instance;
import cloud.InstanceSize;
import cloud.InstanceStatus;
import reality.EventHandler;
import reality.RealJob;
import reality.RealJobStatus;
import reality.RealResource;
import reality.RealResourceStatus;
import reality.Time;

public class RealResourceImpl implements RealResource {

	private final List<RealJobImpl> tasks = new ArrayList<RealJobImpl>();
	private final List<RealJobImpl> uModTasks;
	private final Instance instance;
	private double startTime = Double.MAX_VALUE;
	private boolean activated = false; // delete
	private double terminatedTime = Util.UNSET;
	private final WorkflowInstance workflow;

	RealResourceImpl(Instance instance, WorkflowInstance workflow) {
		this.instance = instance;
		this.workflow = workflow;
		uModTasks = Collections.unmodifiableList(tasks);
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getInfo() {
		return getName() + " " + getStatus() + " " + Arrays.toString(tasks.toArray());
	}

	@Override
	public double getStartTime() {
		if (getStatus() == RealResourceStatus.OFFLINE) {
			throw new RuntimeException(instance.getName());
		}
		return startTime;
	}

	@Override
	public double getEndTime() {
		if (getStatus() == RealResourceStatus.TERMINATED) {
			if (tasks.size() > 0) {
				return tasks.get(tasks.size() - 1).getEndTime();
			} else {
				return 0;
			}

		} else {
			throw new RuntimeException(instance.getName());
		}
	}

	@Override
	public InstanceSize getInstanceSize() {
		return instance.getInstanceSize();
	}

	@Override
	public String getName() {
		return getInstance().getName();
	}

	@Override
	public RealJobImpl scheduleTask(SchedulingTask task, double taskStartTime, EventHandler jobCompletionHandler) {
		if ((getInstance().getStatus() == InstanceStatus.READY
				|| getInstance().getStatus() == InstanceStatus.READY_TO_START)
				&& getStatus() != RealResourceStatus.TERMINATED && getStatus() != RealResourceStatus.RUNNING) {
			if (!tasks.isEmpty()) {
				double et = tasks.get(tasks.size() - 1).getEndTime();
				if (taskStartTime - et < -Util.DOUBLE_THRESHOLD) {
					throw new RuntimeException("task " + task + "scheduled on running resource " + this + " at "
							+ taskStartTime + " but et is " + et);
				}
			} else {
				activate(taskStartTime);
			}
			RealJobImpl job = RealJobImpl.startJob(task, this, taskStartTime, jobCompletionHandler);
			tasks.add(job);
			return job;
		} else {
			throw new RuntimeException(getInfo());
		}
	}

	@Override
	public List<RealJob> getUmodTasks() {
		return (List) uModTasks;
	}

	@Override
	public RealResourceStatus getStatus() {
		if (!activated) {
			return RealResourceStatus.OFFLINE;
		}
		if (terminatedTime >= 0) {
			return RealResourceStatus.TERMINATED;
		}

		if (tasks.size() > 0) {
			RealJobStatus status = tasks.get(tasks.size() - 1).getStatus();

			if (status == RealJobStatus.RUNNING) {
				return RealResourceStatus.RUNNING;
			} else if (status == RealJobStatus.COMPLETED) {
				return RealResourceStatus.IDLING;
			} else {
				throw new RuntimeException();
			}
		} else {
			return RealResourceStatus.IDLING;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getInstance() == null) ? 0 : getInstance().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RealResourceImpl other = (RealResourceImpl) obj;
		if (getInstance() == null) {
			if (other.getInstance() != null)
				return false;
		} else if (!getInstance().equals(other.getInstance()))
			return false;
		return true;
	}

	private void activate(double time) {
		if (activated) {
			throw new RuntimeException();
		}

		activated = true;
		startTime = time;
	}

	@Override
	public void terminate(double time) {
		if (terminatedTime > 0) {
			throw new RuntimeException();
		}
		if (Time.getInstance().isInPast(time)) {
			throw new RuntimeException();
		}
		terminatedTime = time;
	}

	@Override
	public Instance getInstance() {
		return instance;
	}

	@Override
	public double getTerminatedTime() {
		if (getStatus() != RealResourceStatus.TERMINATED) {
			throw new RuntimeException();
		}
		return terminatedTime;
	}

	@Override
	public WorkflowInstance getWorkflow() {
		return workflow;
	}

	

}
