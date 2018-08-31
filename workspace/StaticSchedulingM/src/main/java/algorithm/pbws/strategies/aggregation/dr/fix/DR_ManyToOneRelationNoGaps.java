package algorithm.pbws.strategies.aggregation.dr.fix;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.misc.getters.DR_TG_ManyToOne;
import algorithm.misc.getters.DR_ThrowerGetter;
import algorithm.pbws.PBWSInit;
import algorithm.pbws.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_NoGaps_DifferSize_FixedDL;
import algorithm.pbws.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_NoGaps_EqualSize_FixedDL;
import algorithm.pbws.strategies.aggregation.dr.impl.DR_ManyToMe;

public class DR_ManyToOneRelationNoGaps {

	private final DR_ManyToMe manyToMe;

	public DR_ManyToOneRelationNoGaps(PBWSInit pbwsInit) {
		DR_Manager_EqualSize dr_Manager_EqualSize = new DR_Manager_FP_Succ_NoGaps_EqualSize_FixedDL(
				);
		DR_Manager_DifferSize dr_Manager_DifferSize = new DR_Manager_FP_Succ_NoGaps_DifferSize_FixedDL(
				);
		DR_ThrowerGetter tg = new DR_TG_ManyToOne(pbwsInit);
		manyToMe = new DR_ManyToMe(pbwsInit, dr_Manager_EqualSize,
				dr_Manager_DifferSize, tg);
	}

	public void combineManyToOneRelation() {
		manyToMe.combineManyToMeRelation("DR_ManyToOneRelationNoGaps");
	}

}
