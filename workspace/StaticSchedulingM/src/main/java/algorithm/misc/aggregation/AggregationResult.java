package algorithm.misc.aggregation;

import algorithm.misc.ScaledPseudoLane;
import java.util.List;
import java.util.Set;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;

public class AggregationResult {
	private Lane thrower;
	private Lane catcher;
	private SchedulingTask firstChildOfLastTask;
	private ScaledPseudoLane newThrower;
	private ScaledPseudoLane newCatcher;
	private double throwerShiftTime;
	private List<SchedulingTask> catcherMovePath;
	private List<SchedulingTask> throwerLastPath;
	private ScaledPseudoLane newThrowerLastPath;
	private SchedulingTask throwerLastTask;
	private Aggregator combinator;
	private double newCosts = -1;
	private double oldCosts = -1;
	private Set<Lane> shifted;
	
	
	public double getSavedCosts() {
		if(newCosts < 0) {
			throw new RuntimeException("newCosts unset");
		}
		if(oldCosts < 0) {
			throw new RuntimeException("oldCosts unset");
		}
		return oldCosts - newCosts;
	}
	
	public AggregationResult(Aggregator combinator) {
		this.combinator = combinator;
	}
	
	public void reassign() {
		combinator.performCombination(this);
	}

	public Lane getThrower() {
		return thrower;
	}

	public void setThrower(Lane thrower) {
		this.thrower = thrower;
	}

	public Lane getCatcher() {
		return catcher;
	}

	public void setCatcher(Lane catcher) {
		this.catcher = catcher;
	}

	public SchedulingTask getFirstChildOfLastTask() {
		return firstChildOfLastTask;
	}

	public void setFirstChildOfLastTask(SchedulingTask firstChildOfLastTask) {
		this.firstChildOfLastTask = firstChildOfLastTask;
	}

	public ScaledPseudoLane getNewThrower() {
		return newThrower;
	}

	public void setNewThrower(ScaledPseudoLane newThrower) {
		this.newThrower = newThrower;
	}

	public double getThrowerShiftTime() {
		return throwerShiftTime;
	}

	public void setThrowerShiftTime(double throwerShiftTime) {
		this.throwerShiftTime = throwerShiftTime;
	}

	public List<SchedulingTask> getCatcherMovePath() {
		return catcherMovePath;
	}

	public void setCatcherMovePath(List<SchedulingTask> catcherMovePath) {
		this.catcherMovePath = catcherMovePath;
	}

	public List<SchedulingTask> getThrowerLastPath() {
		return throwerLastPath;
	}

	public void setThrowerLastPath(List<SchedulingTask> throwerLastPath) {
		this.throwerLastPath = throwerLastPath;
	}

	public ScaledPseudoLane getNewThrowerLastPath() {
		return newThrowerLastPath;
	}

	public void setNewThrowerLastPath(ScaledPseudoLane newThrowerLastPath) {
		this.newThrowerLastPath = newThrowerLastPath;
	}

	public SchedulingTask getThrowerLastTask() {
		return throwerLastTask;
	}

	public void setThrowerLastTask(SchedulingTask throwerLastTask) {
		this.throwerLastTask = throwerLastTask;
	}

	public ScaledPseudoLane getNewCatcher() {
		return newCatcher;
	}

	public void setNewCatcher(ScaledPseudoLane newCatcher) {
		this.newCatcher = newCatcher;
	}

	public double getOldCosts() {
		return oldCosts;
	}

	public void setOldCosts(double oldCosts) {
		this.oldCosts = oldCosts;
	}

	public double getNewCosts() {
		return newCosts;
	}

	public void setNewCosts(double newCosts) {
		this.newCosts = newCosts;
	}

	public Set<Lane> getShifted() {
		return shifted;
	}

	public void setShifted(Set<Lane> shifted) {
		this.shifted = shifted;
	}
}