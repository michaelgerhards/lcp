package algorithm.pbws2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import algorithm.AbstractAlgorithm;
import algorithm.pbws.BreakDownCostPrinter;
import algorithm.misc.LaneSchedulingPrinter;
import algorithm.pbws2.strategies.aggregation.br.fix.BR_CombineNeighborsFullPath2;
import algorithm.pbws2.strategies.aggregation.br.fix.BR_CombineNeighborsLastPath2;
import algorithm.pbws2.strategies.aggregation.br.flex.BR_ShiftJoinFullPath2;
import algorithm.pbws2.strategies.aggregation.br.flex.BR_ShiftJoinLastPath2;
import algorithm.pbws2.strategies.aggregation.dr.fix.DR_FreeForAllGapsAllow2;
import algorithm.pbws2.strategies.aggregation.dr.fix.DR_ManyToManyIncomingRelationGapsAll2;
import algorithm.pbws2.strategies.aggregation.dr.fix.DR_ManyToManyIncomingRelationNoGaps2;
import algorithm.pbws2.strategies.aggregation.dr.fix.DR_ManyToManyOutgoingRelationGapsAll2;
import algorithm.pbws2.strategies.aggregation.dr.fix.DR_ManyToManyOutgoingRelationNoGaps2;
import algorithm.pbws2.strategies.aggregation.dr.fix.DR_ManyToOneRelationNoGaps2;
import algorithm.pbws2.strategies.aggregation.dr.fix.DR_OneToManyRelationGapsAll2;
import algorithm.pbws2.strategies.aggregation.dr.fix.DR_OneToManyRelationNoGaps2;
import algorithm.pbws2.strategies.aggregation.dr.fix.DR_OneToOneRelationGapsAll2;
import algorithm.pbws2.strategies.aggregation.dr.fix.DR_OneToOneRelationNoGaps2;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;
import statics.util.Debug;
import statics.util.GUIVisualizer;
import statics.util.Util;

public abstract class PBWSInit2 extends AbstractAlgorithm {

    public LaneSchedulingPrinter printer;
    public BreakDownCostPrinter costPrinter;

    protected final DR_OneToOneRelationNoGaps2 oto;
    protected final DR_OneToManyRelationNoGaps2 otm;
    protected final DR_ManyToManyIncomingRelationNoGaps2 mtmI;
    protected final DR_ManyToManyOutgoingRelationNoGaps2 mtmO;
    protected final DR_ManyToOneRelationNoGaps2 mto;

    protected final DR_OneToOneRelationGapsAll2 otoGapsAll;
    protected final DR_OneToManyRelationGapsAll2 otmGapsAll;
    protected final DR_ManyToManyIncomingRelationGapsAll2 mtmIGapsAll;
    protected final DR_ManyToManyOutgoingRelationGapsAll2 mtmOGapsAll;
    protected final DR_ManyToOneRelationNoGaps2 mtoGapsAll;

    protected final DR_FreeForAllGapsAllow2 ffa;

    private final Set<Lane> considerJoins;

    private List<SchedulingTask> initialJoinTasks;

    protected final BR_CombineNeighborsFullPath2 fpfix;
    protected final BR_CombineNeighborsLastPath2 lpfix;
    protected final BR_ShiftJoinFullPath2 fpflex;
    protected final BR_ShiftJoinLastPath2 lpflex;

    public PBWSInit2() {

        considerJoins = new HashSet<>();

        fpfix = new BR_CombineNeighborsFullPath2(this);
        lpfix = new BR_CombineNeighborsLastPath2(this);
        fpflex = new BR_ShiftJoinFullPath2(this);
        lpflex = new BR_ShiftJoinLastPath2(this);

        oto = new DR_OneToOneRelationNoGaps2(this);
        otm = new DR_OneToManyRelationNoGaps2(this);
        mtmI = new DR_ManyToManyIncomingRelationNoGaps2(this);
        mtmO = new DR_ManyToManyOutgoingRelationNoGaps2(this);
        mto = new DR_ManyToOneRelationNoGaps2(this);

        otoGapsAll = new DR_OneToOneRelationGapsAll2(this);
        otmGapsAll = new DR_OneToManyRelationGapsAll2(this);
        mtmIGapsAll = new DR_ManyToManyIncomingRelationGapsAll2(this);
        mtmOGapsAll = new DR_ManyToManyOutgoingRelationGapsAll2(this);
        mtoGapsAll = new DR_ManyToOneRelationNoGaps2(this);

        ffa = new DR_FreeForAllGapsAllow2(this);

    }

    private void checkJoin(SchedulingTask child) {
        int parentSize = child.getParents().size();
        if (parentSize > 1) {
            initialJoinTasks.add(child);
        }
    }

    private boolean containsStillJoinTask(List<SchedulingTask> tasks) {
        return tasks.stream().anyMatch((task) -> (isStillJoinTask(task)));
    }

    protected abstract void doIt();

    @Override
    public String getAlgorithmName() {
        return "Pattern Based Workflow Scheduling";
    }

    @Override
    public String getAlgorithmNameAbbreviation() {
        return "PBWS";
    }

    public boolean containsConsiderJoins(Lane l) {
        return considerJoins.contains(l);
    }

    public void updateConsiderJoins(Collection<Lane> modified) {
        considerJoins.clear();
        modified.stream().forEach((mod) -> {
            mod.getUmodParents().stream().forEach((parent) -> {
                considerJoins.addAll(parent.getUmodChildren());
            });
        });
    }

    private List<SchedulingTask> getInitialJoinTasks() {
        return initialJoinTasks;
    }

    public List<Lane> getJoinLanes() {
        List<Lane> joinLanes = new ArrayList<>();
        getWorkflow().getLanes().stream().filter((joinLane) -> (isJoinLane(joinLane)
                && containsStillJoinTask(joinLane.getUmodTasks()))).forEach((joinLane) -> {
                    joinLanes.add(joinLane);
                });
        return joinLanes;
    }

    public List<SchedulingTask> getJoinTasks() {
        List<SchedulingTask> joinTasks = new ArrayList<>(getInitialJoinTasks().size());
        getInitialJoinTasks().stream().filter((task) -> (isStillJoinTask(task))).forEach((task) -> {
            joinTasks.add(task);
        });
        return joinTasks;
    }

    /**
     * recursive algorithm to assign children of parent to lanes
     *
     * @param parentTask
     */
    private void initLane(SchedulingTask parentTask) {
        Collection<SchedulingTask> children = parentTask.getChildren();
        SchedulingTask exitTask = getWorkflow().getExit();
        children.stream().filter((child) -> (child != exitTask)).filter((child) -> (child.getLane() == null)).map((child) -> {
            Collection<SchedulingTask> parentsOfChild = child.getParents();
            SchedulingTask firstParent = parentsOfChild.iterator().next();
            LaneIndex index;
            if (Util.isSequentialRelation(firstParent, child) && firstParent != getWorkflow().getEntry()) {
                Lane l = parentTask.getLane();
                l.addTaskAtEnd(child);
                index = l.getId();
            } else {
                Lane l = getWorkflow().instantiate(getCloudUtil().getFastestSize());
                considerJoins.add(l);
                index = l.getId();
                l.addTaskAtEnd(child);
            }
            Debug.INSTANCE.println(Integer.MAX_VALUE, "set " , child , " at " , index);
            return child;
        }).map((child) -> {
            checkJoin(child);
            return child;
        }).forEach((child) -> {
            initLane(child);
        });
    }

    private void initLanes() {
        initialJoinTasks = new ArrayList<>(500);
        initLane(getWorkflow().getEntry());
        initialJoinTasks = Collections.unmodifiableList(initialJoinTasks);
    }

    @Override
    public void scheduleIntern() {
        printer = new LaneSchedulingPrinter(getWorkflow());
        costPrinter = new BreakDownCostPrinter(this);
        getWorkflow().getExit().setStartTime(getWorkflow().getDeadline());
        getWorkflow().getExit().setEndTime(getWorkflow().getDeadline());
        getVisualizer().printSchedulingTableWithoutInstances();
        initLanes();
        doIt();
        Debug.INSTANCE.println(1, "end algorithm");
        GUIVisualizer.guiVisualize("Scheduling Complete", getWorkflow());
    }

}
