package algorithm.pbws.strategies.aggregation.dr.fix;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.misc.getters.DR_CG_ManyToManyOutgoing;
import algorithm.misc.getters.DR_CatcherGetter;
import algorithm.pbws.PBWSInit;
import algorithm.pbws.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_NoGaps_DifferSize_FixedDL;
import algorithm.pbws.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_NoGaps_EqualSize_FixedDL;
import algorithm.pbws.strategies.aggregation.dr.impl.DR_MeToMany;

public class DR_ManyToManyOutgoingRelationNoGaps {

	private final DR_MeToMany meToMany;

	public DR_ManyToManyOutgoingRelationNoGaps(PBWSInit pbwsInit) {
		DR_Manager_EqualSize dr_Manager_EqualSize = new DR_Manager_FP_Succ_NoGaps_EqualSize_FixedDL(
				);
		DR_Manager_DifferSize dr_Manager_DifferSize = new DR_Manager_FP_Succ_NoGaps_DifferSize_FixedDL(
				);
		DR_CatcherGetter cg = new DR_CG_ManyToManyOutgoing(pbwsInit);
		meToMany = new DR_MeToMany(pbwsInit, dr_Manager_EqualSize,
				dr_Manager_DifferSize, cg);
	}

	public void combineManyToManyOutgoingRelation() {
		meToMany.combineMeToManyRelation("DR_ManyToManyOutgoingRelationNoGaps");
	}

}
