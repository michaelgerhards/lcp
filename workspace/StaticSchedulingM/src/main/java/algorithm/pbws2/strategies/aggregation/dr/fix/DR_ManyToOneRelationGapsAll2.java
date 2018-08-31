package algorithm.pbws2.strategies.aggregation.dr.fix;

import algorithm.StaticSchedulingAlgorithm;
import algorithm.misc.DR_Manager_DifferSize;
import algorithm.misc.DR_Manager_EqualSize;
import algorithm.misc.getters.DR_TG_ManyToOne;
import algorithm.misc.getters.DR_ThrowerGetter;
import algorithm.pbws2.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_GapsAll_DifferSize_FixedDL2;
import algorithm.pbws2.strategies.aggregation.dr.fix.manager.DR_Manager_FP_Succ_GapsAll_EqualSize_FixedDL2;

import algorithm.pbws2.strategies.aggregation.dr.impl.DR_ManyToMe2;

public class DR_ManyToOneRelationGapsAll2 {

    private final DR_ManyToMe2 manyToMe;

    public DR_ManyToOneRelationGapsAll2(StaticSchedulingAlgorithm pbwsInit) {
        DR_Manager_EqualSize dr_Manager_EqualSize = new DR_Manager_FP_Succ_GapsAll_EqualSize_FixedDL2();
        DR_Manager_DifferSize dr_Manager_DifferSize = new DR_Manager_FP_Succ_GapsAll_DifferSize_FixedDL2();
        DR_ThrowerGetter tg = new DR_TG_ManyToOne(pbwsInit);
        manyToMe = new DR_ManyToMe2(pbwsInit, dr_Manager_EqualSize, dr_Manager_DifferSize, tg);
    }

    public void combineManyToOneRelation() {
        manyToMe.combineManyToMeRelation("DR_ManyToOneRelationGapsAll");
    }

}
