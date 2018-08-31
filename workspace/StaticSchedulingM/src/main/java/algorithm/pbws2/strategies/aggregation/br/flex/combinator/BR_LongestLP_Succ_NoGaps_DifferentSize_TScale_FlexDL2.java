package algorithm.pbws2.strategies.aggregation.br.flex.combinator;

import algorithm.misc.ScaledPseudoLane;
import java.util.List;
import java.util.Set;

import algorithm.misc.aggregation.AggregationResult;
import algorithm.misc.aggregation.Aggregator;

import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.initialization.impl.SchedulingTaskList;
import statics.util.BillingUtil;
import statics.util.Debug;
import statics.util.Util;

public class BR_LongestLP_Succ_NoGaps_DifferentSize_TScale_FlexDL2 implements
        Aggregator {

    public AggregationResult tryMigrateLastPath_ThrowerAfterCatcher_ThrowerScale_DifferentInstanceSize(Lane catcher, Lane thrower) {
        if (thrower.getInstanceSize() == catcher.getInstanceSize()) {
            throw new RuntimeException("equal size");
        }

        if (catcher.getUmodChildren().contains(thrower) || catcher.getUmodParents().contains(thrower)) {
            throw new RuntimeException();
        }

        int maxPath = thrower.getPathCount();
        AggregationResult bestResult = null;
        for (int currentPath = 1; currentPath <= maxPath; ++currentPath) {

            SchedulingTaskList throwerLastPath = thrower.getUmodLastPath(currentPath);

            ScaledPseudoLane newThrowerLastPath = thrower.tryScalingIdlFlex(catcher.getInstanceSize(), throwerLastPath);
            if (newThrowerLastPath == null) {
                continue;
            }

            double throwShift = catcher.getEndTime() - newThrowerLastPath.getStartTime();

            if (throwShift < Util.DOUBLE_THRESHOLD) {
                break;
            }

            double combinedCosts = thrower.getCost() + catcher.getCost();
            double newCombinedCosts = BillingUtil.getInstance().getCosts_LP_NoGaps_DifferSize(catcher, thrower, newThrowerLastPath, throwerLastPath);

            if (newCombinedCosts <= combinedCosts) {

                double newThrowerGlobalShiftSpace = newThrowerLastPath.getRSWflex();

                if (throwShift - newThrowerGlobalShiftSpace > Util.DOUBLE_THRESHOLD) {
//                    boolean satisfyNewStartTimeFlex = newThrowerLastPath                            .satisfyNewStartTimeFlex(catcher.getEndTime());
//                    if (!satisfyNewStartTimeFlex) {
                    continue;
//                    }
                }

                AggregationResult result = new AggregationResult(this);
                result.setCatcher(catcher);
                result.setThrower(thrower);
                result.setThrowerLastPath(throwerLastPath);
                result.setThrowerShiftTime(throwShift);
                result.setNewThrowerLastPath(newThrowerLastPath);
                result.setNewCosts(newCombinedCosts);
                result.setOldCosts(combinedCosts);
                bestResult = result;
            }
        }
        return bestResult;
    }

    @Override
    public void performCombination(AggregationResult result) {
        Lane catcher = result.getCatcher();
        Lane thrower = result.getThrower();
        List<SchedulingTask> throwerLastPath = result.getThrowerLastPath();
        ScaledPseudoLane newThrowerLastPath = result.getNewThrowerLastPath();
        double throwShift = result.getThrowerShiftTime();

//		algorithm.printer.printJoinShift(result, throwShift, 0);
        Debug.INSTANCE.println(Integer.MAX_VALUE, "shift join");

        Lane migrator;
        if (throwerLastPath.size() == thrower.getUmodTasks().size()) {
            migrator = thrower;
        } else {
            migrator = thrower.extractLane(throwerLastPath);
        }
        Set<Lane> shifted = migrator.prepareSuccessorsForOwnVertScale(
                newThrowerLastPath, throwShift);
        shifted.add(catcher);
        shifted.add(thrower);
        shifted.addAll(thrower.getUmodChildren());
        shifted.addAll(catcher.getUmodChildren());
        result.setShifted(shifted);
        migrator.scale(newThrowerLastPath);
        migrator.shift(throwShift);
        catcher.reassignToEndFrom(migrator);

    }

}
