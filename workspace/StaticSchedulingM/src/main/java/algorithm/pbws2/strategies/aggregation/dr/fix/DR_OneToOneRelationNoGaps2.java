package algorithm.pbws2.strategies.aggregation.dr.fix;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.pbws2.PBWSInit2;
import algorithm.pbws2.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_NoGaps_DifferSize_FixedDL2;
import algorithm.pbws2.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_NoGaps_EqualSize_FixedDL2;
import algorithm.pbws2.strategies.aggregation.dr.impl.DR_OneToOneRelation2;

public class DR_OneToOneRelationNoGaps2 {

	private final DR_OneToOneRelation2 oneToOne;

	public DR_OneToOneRelationNoGaps2(PBWSInit2 pbwsInit) {
		DR_Manager_EqualSize dr_Manager_EqualSize = new DR_Manager_FP_Succ_NoGaps_EqualSize_FixedDL2(
				pbwsInit);
		DR_Manager_DifferSize dr_Manager_DifferSize = new DR_Manager_FP_Succ_NoGaps_DifferSize_FixedDL2(
				pbwsInit);
		oneToOne = new DR_OneToOneRelation2(pbwsInit, dr_Manager_EqualSize,
				dr_Manager_DifferSize);
	}

	public void combineOneToOneRelationNoGaps() {
		oneToOne.combineOneToOneRelation("DR_OneToOneRelationNoGaps");
	}
}
