package algorithm.pbws2.strategies.aggregation.dr.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.misc.DR_ResultSelector;
import algorithm.misc.aggregation.AggregationResult;
import algorithm.pbws.Comparators;
import algorithm.pbws2.PBWSInit2;
import statics.initialization.impl.Lane;
import statics.util.Debug;

public class DR_FreeForAll2 {

    private final PBWSInit2 algorithm;
    private final DR_Manager_EqualSize dr_Manager_EqualSize;
    private final DR_Manager_DifferSize dr_Manager_DifferSize;

    public DR_FreeForAll2(PBWSInit2 algorithm, DR_Manager_EqualSize dr_Manager_EqualSize,
            DR_Manager_DifferSize dr_Manager_DifferSize) {
        this.algorithm = algorithm;
        this.dr_Manager_EqualSize = dr_Manager_EqualSize;
        this.dr_Manager_DifferSize = dr_Manager_DifferSize;
    }

    public boolean reviseAllocationPlanForNotDirectlyRelatedTasks() {
        boolean found = false;
        Debug.INSTANCE.println(2, "start combine ffa relation equal sizes");

        List<Lane> outers = new ArrayList<>(algorithm.getWorkflow().getLanes());

//        Collections.sort(outers, Comparators.laneLowerStartTimeComparator);
        int i = 0;
        for (Lane outer : outers) {
            if (!algorithm.getWorkflow().existsLane(outer)) {
                continue;
            }

//            List<Lane> partners = outers.subList(i + 1, outers.size());
            AggregationResult finalResult;
            do {
                finalResult = null;
//				List<Lane> nextRoundPartners = new ArrayList<Lane>(partners.size());

                for (int j = i + 1; j < outers.size(); ++j) {
                    Lane inner = outers.get(j);

//                for (Lane inner : partners) {
                    if (outer == inner || !algorithm.getWorkflow().existsLane(inner)) {
                        continue;
                    }
                    AggregationResult result;
                    if (inner.getInstanceSize() != outer.getInstanceSize()) {
                        result = dr_Manager_DifferSize.checkDifferentInstanceSize(inner, outer);
                    } else {
                        result = dr_Manager_EqualSize.checkEqualInstanceSize(inner, outer);

                    }
//					if (result != null) {
//						nextRoundPartners.add(inner);
//					}

                    finalResult = DR_ResultSelector.selectResult(finalResult, result);

                    // if best result = optimal result -> break
                    if (finalResult != null) {
                        break;
                        // XXX
                    }
                }

                if (finalResult != null) {
                    finalResult.reassign();
                    if (algorithm.getWorkflow().existsLane(finalResult.getThrower())) {
                        outer = finalResult.getThrower();
                    } else {
                        outer = finalResult.getCatcher();
                    }
                    found = true;
//                    partners = nextRoundPartners;
                }
            } while (finalResult != null);
            ++i;
        } // for catchers
        return found;
    }

}
