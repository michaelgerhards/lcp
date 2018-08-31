package algorithm.pbws2.strategies.aggregation.dr.fix;

import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.misc.getters.DR_CG_ManyToManyOutgoing;
import algorithm.misc.getters.DR_CatcherGetter;
import algorithm.pbws2.PBWSInit2;
import algorithm.pbws2.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_GapsAll_DifferSize_FixedDL2;
import algorithm.pbws2.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_GapsAll_EqualSize_FixedDL2;
import algorithm.pbws2.strategies.aggregation.dr.impl.DR_MeToMany2;

public class DR_ManyToManyOutgoingRelationGapsAll2 {

    private final DR_MeToMany2 meToMany;

    public DR_ManyToManyOutgoingRelationGapsAll2(PBWSInit2 pbwsInit) {
        DR_Manager_EqualSize dr_Manager_EqualSize = new DR_Manager_FP_Succ_GapsAll_EqualSize_FixedDL2();
        DR_Manager_DifferSize dr_Manager_DifferSize = new DR_Manager_FP_Succ_GapsAll_DifferSize_FixedDL2();
        DR_CatcherGetter cg = new DR_CG_ManyToManyOutgoing(pbwsInit);
        meToMany = new DR_MeToMany2(pbwsInit, dr_Manager_EqualSize, dr_Manager_DifferSize, cg);
    }

    public void combineManyToManyOutgoingRelation() {
        meToMany.combineMeToManyRelation("DR_ManyToManyOutgoingRelationGapsAll");
    }

}
