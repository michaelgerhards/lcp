package algorithm.pbws.strategies.aggregation.dr.fix;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.pbws.PBWSInit;
import algorithm.pbws.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_GapsAll_DifferSize_FixedDL;
import algorithm.pbws.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_GapsAll_EqualSize_FixedDL;
import algorithm.pbws.strategies.aggregation.dr.impl.DR_FreeForAll;

public class DR_FreeForAllGapsAllow {

	private final DR_FreeForAll ffa;

	public DR_FreeForAllGapsAllow(PBWSInit pbwsInit) {
		DR_Manager_EqualSize dr_Manager_EqualSize = new DR_Manager_FP_Succ_GapsAll_EqualSize_FixedDL(
				);
		DR_Manager_DifferSize dr_Manager_DifferSize = new DR_Manager_FP_Succ_GapsAll_DifferSize_FixedDL(
				);
		ffa = new DR_FreeForAll(pbwsInit, dr_Manager_EqualSize,
				dr_Manager_DifferSize);
	}

	public void reviseAllocationPlanForNotDirectlyRelatedTasks() {
		ffa.reviseAllocationPlanForNotDirectlyRelatedTasks();

	}

}
