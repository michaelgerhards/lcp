package algorithm.pbws.strategies.aggregation.br.fix.combinator;

import algorithm.misc.ScaledPseudoLane;
import java.util.List;

import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.initialization.impl.SchedulingTaskList;
import statics.util.BillingUtil;
import statics.util.Util;

public class BR_LP_Succ_NoGaps_DifferSize_TScale_FixDL implements Aggregator {
	

	public AggregationResult tryMigrateThrowerPathAfterCatcher_DifferentInstanceSize_ThrowerScale_NoGaps(
			Lane thrower, Lane catcher, SchedulingTaskList throwerLastPath) {
		if (thrower.getInstanceSize() == catcher.getInstanceSize()
				|| throwerLastPath.getInstanceSize() != thrower
						.getInstanceSize()) {
			throw new RuntimeException(
					"wrong instance size in LP_Succ_NoGaps_DifferentSize_ThrowerScale_FixedDL");
		}
		if (catcher.getUmodChildren().contains(thrower)
				|| catcher.getUmodParents().contains(thrower)) {
			throw new RuntimeException();
		}

		ScaledPseudoLane newThrowerLastPath = thrower.tryScalingIdlFix(
				catcher.getInstanceSize(), throwerLastPath);
		if (newThrowerLastPath == null) {
			return null;
		}

		double catcherExecutionEndtime = catcher.getEndTime();
		double throwerExStartTime = throwerLastPath.getStartTime();

		double throwerShiftTime = catcherExecutionEndtime - throwerExStartTime;

		if (throwerShiftTime < Util.DOUBLE_THRESHOLD) {
			return null;
		}

		double combinedCosts = thrower.getCost() + catcher.getCost();
		BillingUtil bu = BillingUtil.getInstance();
		double newCombinedCosts = bu
				.getNewCombinedCosts_LP_NoGaps_DifferentSize(thrower, catcher,
						throwerLastPath, newThrowerLastPath);
		if (newCombinedCosts >= combinedCosts) {
			return null;
		}

		if (!newThrowerLastPath.hasGap()) {
			final double availableThrowerShiftTime = newThrowerLastPath
					.getRSWfix();
			if (throwerShiftTime > availableThrowerShiftTime) {
				return null;
			}
		} else {
			double time = catcher.getEndTime();
			boolean satisfyNewStartTimeFix = newThrowerLastPath.satisfyNewStartTimeFix(time);
			if(!satisfyNewStartTimeFix) {
				return null;
			}
		}

		AggregationResult result = new AggregationResult(this);
		result.setThrower(thrower);
		result.setCatcher(catcher);
		result.setThrowerLastPath(throwerLastPath);
		result.setNewThrowerLastPath(newThrowerLastPath);
		result.setThrowerShiftTime(throwerShiftTime);
		result.setNewCosts(newCombinedCosts);
		result.setOldCosts(combinedCosts);
		return result;

	}

	@Override
	public void performCombination(AggregationResult result) {
		Lane thrower = result.getThrower();
		Lane catcher = result.getCatcher();
		List<SchedulingTask> throwerLastPath = result.getThrowerLastPath();
		ScaledPseudoLane newThrowerLastPath = result.getNewThrowerLastPath();
		double throwerShiftTime = result.getThrowerShiftTime();

		Lane migrator;
		if (throwerLastPath.size() == thrower.getUmodTasks().size()) {
			migrator = thrower;
		} else {
			migrator = thrower.extractLane(throwerLastPath);
		}
		migrator.scale(newThrowerLastPath);
		migrator.shift(throwerShiftTime);
		catcher.reassignToEndFrom(migrator);

	}

}
