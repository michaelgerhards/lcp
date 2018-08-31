package algorithm.pbws2.strategies.aggregation.dr.fix;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.pbws2.PBWSInit2;
import algorithm.pbws2.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_GapsAll_DifferSize_FixedDL2;
import algorithm.pbws2.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_GapsAll_EqualSize_FixedDL2;
import algorithm.pbws2.strategies.aggregation.dr.impl.DR_FreeForAll2;

public class DR_FreeForAllGapsAllow2 {

    private final DR_FreeForAll2 ffa;

    public DR_FreeForAllGapsAllow2(PBWSInit2 pbwsInit) {
        DR_Manager_EqualSize dr_Manager_EqualSize = new DR_Manager_FP_Succ_GapsAll_EqualSize_FixedDL2();
        DR_Manager_DifferSize dr_Manager_DifferSize = new DR_Manager_FP_Succ_GapsAll_DifferSize_FixedDL2();
        ffa = new DR_FreeForAll2(pbwsInit, dr_Manager_EqualSize, dr_Manager_DifferSize);
    }

    public void reviseAllocationPlanForNotDirectlyRelatedTasks() {
        ffa.reviseAllocationPlanForNotDirectlyRelatedTasks();

    }

}
