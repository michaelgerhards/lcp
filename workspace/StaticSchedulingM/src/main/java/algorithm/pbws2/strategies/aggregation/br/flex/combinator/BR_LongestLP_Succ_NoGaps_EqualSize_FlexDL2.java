package algorithm.pbws2.strategies.aggregation.br.flex.combinator;

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

public class BR_LongestLP_Succ_NoGaps_EqualSize_FlexDL2 implements Aggregator {

    public AggregationResult tryMigratePath_ThrowerAfterCatcher_EqualInstanceSizes(Lane catcher, Lane thrower) {
        if (thrower.getInstanceSize() != catcher.getInstanceSize()) {
            throw new RuntimeException("unequal size");
        }

        if (catcher.getUmodChildren().contains(thrower) || catcher.getUmodParents().contains(thrower)) {
            throw new RuntimeException();
        }

        int maxPath = thrower.getPathCount();
        AggregationResult bestResult = null;
        double oldCosts = thrower.getCost() + catcher.getCost();
        for (int currentPath = 1; currentPath <= maxPath; ++currentPath) {
            SchedulingTaskList throwerPath = thrower.getUmodLastPath(currentPath);

            double throwShift = catcher.getEndTime() - throwerPath.getStartTime();

            if (throwShift < Util.DOUBLE_THRESHOLD) {
                break;
            }

            double newCosts = BillingUtil.getInstance().getNewCosts_NoGaps_EqualSize(catcher, thrower, throwerPath);

            if (newCosts <= oldCosts) {
                double throwerGlobalShiftSpace = throwerPath.getRSWflex();
                if (throwerGlobalShiftSpace - throwShift < -Util.DOUBLE_THRESHOLD) {
//                    boolean satisfyNewStartTimeFlex = throwerPath.satisfyNewStartTimeFlex(catcher.getEndTime());
//                    if (!satisfyNewStartTimeFlex) {
                    continue;
//                    }
                }
                AggregationResult result = new AggregationResult(this);
                result.setCatcher(catcher);
                result.setThrower(thrower);
                result.setThrowerLastPath(throwerPath);
                result.setThrowerShiftTime(throwShift);
                result.setOldCosts(oldCosts);
                result.setNewCosts(newCosts);
                bestResult = result;
            }
        }
        return bestResult;
    }

    @Override
    public void performCombination(AggregationResult result) {
        Lane thrower = result.getThrower();
        Lane catcher = result.getCatcher();
        List<SchedulingTask> throwerPath = result.getThrowerLastPath();
        double throwShift = result.getThrowerShiftTime();
        Debug.INSTANCE.println(Integer.MAX_VALUE, "shift join");

        Lane migrator;
        if (throwerPath.size() == thrower.getUmodTasks().size()) {
            migrator = thrower;
        } else {
            migrator = thrower.extractLane(throwerPath);
        }
        Set<Lane> shifted = migrator.shiftEquallyRecursiveWithAllSuccessors(throwShift);
        shifted.add(catcher);
        shifted.add(thrower);
        shifted.addAll(thrower.getUmodChildren());
        shifted.addAll(catcher.getUmodChildren());
        result.setShifted(shifted);

        catcher.reassignToEndFrom(migrator);
    }

}
