package algorithm.pbws.strategies.aggregation.dr.fix;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.pbws.PBWSInit;
import algorithm.pbws.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_NoGaps_DifferSize_FixedDL;
import algorithm.pbws.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_NoGaps_EqualSize_FixedDL;
import algorithm.pbws.strategies.aggregation.dr.impl.DR_OneToOneRelation;

public class DR_OneToOneRelationNoGaps {

	private final DR_OneToOneRelation oneToOne;

	public DR_OneToOneRelationNoGaps(PBWSInit pbwsInit) {
		DR_Manager_EqualSize dr_Manager_EqualSize = new DR_Manager_FP_Succ_NoGaps_EqualSize_FixedDL(
				);
		DR_Manager_DifferSize dr_Manager_DifferSize = new DR_Manager_FP_Succ_NoGaps_DifferSize_FixedDL(
				);
		oneToOne = new DR_OneToOneRelation(pbwsInit, dr_Manager_EqualSize,
				dr_Manager_DifferSize);
	}

	public void combineOneToOneRelationNoGaps() {
		oneToOne.combineOneToOneRelation("DR_OneToOneRelationNoGaps");
	}
}
