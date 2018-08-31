package algorithm.pbws.strategies.aggregation.br.fix.combinator;

import java.util.List;

import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.initialization.impl.SchedulingTaskList;
import statics.util.BillingUtil;
import statics.util.Util;

public class BR_LP_Succ_NoGaps_EqualSize_FixDL implements Aggregator {

	/**
	 * 
	 * 
	 * @param thrower
	 * @param catcher
	 * @param throwerLastPath
	 * @return true, if the thrower is not released. false if the thrower is
	 *         released or nothing happened
	 */
	public AggregationResult tryMigrateThrowerPathAfterCatcher_EqualInstanceSize_NoGaps(
			Lane thrower, Lane catcher, SchedulingTaskList throwerLastPath) {
		if (thrower.getInstanceSize() != catcher.getInstanceSize()
				|| throwerLastPath.getInstanceSize() != thrower
						.getInstanceSize()) {
			throw new RuntimeException(
					"LP_Succ_NoGaps_EqualSize_FixedDL wrong instance sizes");
		}
		if (catcher.getUmodChildren().contains(thrower)
				|| catcher.getUmodParents().contains(thrower)) {
			throw new RuntimeException();
		}

		double catcherExecutionEndtime = catcher.getEndTime();
		double throwerExStartTime = throwerLastPath.getStartTime();

		double throwerShiftTime = catcherExecutionEndtime - throwerExStartTime;
		if (throwerShiftTime < Util.DOUBLE_THRESHOLD) {
			return null;
		}
		BillingUtil bu = BillingUtil.getInstance();
		double newCosts = 
				bu
				.getNewCosts_NoGaps_EqualSize(catcher, thrower, throwerLastPath);
		double oldCosts = thrower.getCost() + catcher.getCost();

		if (newCosts >= oldCosts) {
			return null;
		}

		double availableThrowerShiftTime = throwerLastPath.getRSWfix();
		if (availableThrowerShiftTime - throwerShiftTime < -Util.DOUBLE_THRESHOLD) {
			boolean satisfyNewStartTimeFix = throwerLastPath.satisfyNewStartTimeFix(catcherExecutionEndtime);
			if(!satisfyNewStartTimeFix) {
				return null;
			}

		}

		AggregationResult result = new AggregationResult(this);
		result.setThrower(thrower);
		result.setCatcher(catcher);
		result.setThrowerLastPath(throwerLastPath);
		result.setThrowerShiftTime(throwerShiftTime);
		result.setNewCosts(newCosts);
		result.setOldCosts(oldCosts);
		return result;
	}

	@Override
	public void performCombination(AggregationResult result) {
		Lane thrower = result.getThrower();
		Lane catcher = result.getCatcher();
		List<SchedulingTask> throwersLastPath = result.getThrowerLastPath();
		double throwerShiftTime = result.getThrowerShiftTime();

		Lane migrator;
		if (throwersLastPath.size() == thrower.getUmodTasks().size()) {
			migrator = thrower;
		} else {
			migrator = thrower.extractLane(throwersLastPath);
		}
		migrator.shift(throwerShiftTime);
		catcher.reassignToEndFrom(migrator);
	}

}
