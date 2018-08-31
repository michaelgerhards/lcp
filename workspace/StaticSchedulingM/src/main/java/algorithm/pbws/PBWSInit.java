package algorithm.pbws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import algorithm.AbstractAlgorithm;
import algorithm.misc.LaneSchedulingPrinter;
import algorithm.pbws.strategies.aggregation.br.fix.BR_CombineNeighborsFullPath;
import algorithm.pbws.strategies.aggregation.br.fix.BR_CombineNeighborsLastPath;
import algorithm.pbws.strategies.aggregation.br.flex.BR_ShiftJoinFullPath;
import algorithm.pbws.strategies.aggregation.br.flex.BR_ShiftJoinLastPath;
import algorithm.pbws.strategies.aggregation.dr.fix.DR_FreeForAllGapsAllow;
import algorithm.pbws.strategies.aggregation.dr.fix.DR_ManyToManyIncomingRelationGapsAll;
import algorithm.pbws.strategies.aggregation.dr.fix.DR_ManyToManyIncomingRelationNoGaps;
import algorithm.pbws.strategies.aggregation.dr.fix.DR_ManyToManyOutgoingRelationGapsAll;
import algorithm.pbws.strategies.aggregation.dr.fix.DR_ManyToManyOutgoingRelationNoGaps;
import algorithm.pbws.strategies.aggregation.dr.fix.DR_ManyToOneRelationNoGaps;
import algorithm.pbws.strategies.aggregation.dr.fix.DR_OneToManyRelationGapsAll;
import algorithm.pbws.strategies.aggregation.dr.fix.DR_OneToManyRelationNoGaps;
import algorithm.pbws.strategies.aggregation.dr.fix.DR_OneToOneRelationGapsAll;
import algorithm.pbws.strategies.aggregation.dr.fix.DR_OneToOneRelationNoGaps;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;
import statics.initialization.impl.LaneIndex;
import statics.util.Debug;
import statics.util.GUIVisualizer;
import statics.util.Util;

public abstract class PBWSInit extends AbstractAlgorithm {

    public LaneSchedulingPrinter printer;
    public BreakDownCostPrinter costPrinter;
    protected final DR_OneToOneRelationNoGaps oto;
    protected final DR_OneToManyRelationNoGaps otm;
    protected final DR_ManyToManyIncomingRelationNoGaps mtmI;
    protected final DR_ManyToManyOutgoingRelationNoGaps mtmO;
    protected final DR_ManyToOneRelationNoGaps mto;

    protected final DR_OneToOneRelationGapsAll otoGapsAll;
    protected final DR_OneToManyRelationGapsAll otmGapsAll;
    protected final DR_ManyToManyIncomingRelationGapsAll mtmIGapsAll;
    protected final DR_ManyToManyOutgoingRelationGapsAll mtmOGapsAll;
    protected final DR_ManyToOneRelationNoGaps mtoGapsAll;

    protected final DR_FreeForAllGapsAllow ffa;

    private final Set<Lane> considerJoins;

    private List<SchedulingTask> initialJoinTasks;

    protected final BR_CombineNeighborsFullPath fpfix;
    protected final BR_CombineNeighborsLastPath lpfix;
    protected final BR_ShiftJoinFullPath fpflex;
    protected final BR_ShiftJoinLastPath lpflex;

    public PBWSInit() {

        considerJoins = new HashSet<>();

        fpfix = new BR_CombineNeighborsFullPath(this);
        lpfix = new BR_CombineNeighborsLastPath(this);
        fpflex = new BR_ShiftJoinFullPath(this);
        lpflex = new BR_ShiftJoinLastPath(this);

        oto = new DR_OneToOneRelationNoGaps(this);
        otm = new DR_OneToManyRelationNoGaps(this);
        mtmI = new DR_ManyToManyIncomingRelationNoGaps(this);
        mtmO = new DR_ManyToManyOutgoingRelationNoGaps(this);
        mto = new DR_ManyToOneRelationNoGaps(this);

        otoGapsAll = new DR_OneToOneRelationGapsAll(this);
        otmGapsAll = new DR_OneToManyRelationGapsAll(this);
        mtmIGapsAll = new DR_ManyToManyIncomingRelationGapsAll(this);
        mtmOGapsAll = new DR_ManyToManyOutgoingRelationGapsAll(this);
        mtoGapsAll = new DR_ManyToOneRelationNoGaps(this);

        ffa = new DR_FreeForAllGapsAllow(this);

    }

    private void checkJoin(SchedulingTask child) {
        int parentSize = child.getParents().size();
        if (parentSize > 1) {
            initialJoinTasks.add(child);
        }
    }

    private boolean containsStillJoinTask(List<SchedulingTask> tasks) {
        for (SchedulingTask task : tasks) {
            if (isStillJoinTask(task)) {
                return true;
            }
        }
        return false;
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
        getWorkflow().getLanes().stream().filter((joinLane) -> (isJoinLane(joinLane) && containsStillJoinTask(joinLane.getUmodTasks()))).forEach((joinLane) -> {
            joinLanes.add(joinLane);
        });
        return joinLanes;
    }

    public List<SchedulingTask> getJoinTasks() {
        List<SchedulingTask> joinTasks = new ArrayList<SchedulingTask>(getInitialJoinTasks().size());
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
        for (SchedulingTask child : children) {
            if (child != exitTask) {
                if (child.getLane() == null) {
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
                    Debug.INSTANCE.printf(Integer.MAX_VALUE, "set %s at %s", child, index);

                    checkJoin(child);
                    initLane(child);
                }
            } // if
        } // for child
    }

    private void initLanes() {
        initialJoinTasks = new ArrayList<>();
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
