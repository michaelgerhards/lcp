package algorithm.pbws;

import algorithm.comparator.pbws.lane.JoinLaneComparatorLargestFirst;
import algorithm.comparator.pbws.lane.JoinLaneComparatorSmallestFirst;
import algorithm.comparator.pbws.lane.LaneExpensiveFirstComparator;
import algorithm.comparator.pbws.lane.LaneHigherExecutionTimeComparator;
import algorithm.comparator.pbws.lane.LaneHigherStartTimeComparator;
import algorithm.comparator.pbws.lane.LaneLastPathHigherExecutionTimeComparator;
import algorithm.comparator.pbws.lane.LaneLastPathShorterExecutionTimeComparator;
import algorithm.comparator.pbws.lane.LaneLaterExecutionEndTimeComparator;
import algorithm.comparator.pbws.lane.LaneLowerStartTimeComparator;
import algorithm.comparator.pbws.lane.LanePathCountComparator;
import algorithm.comparator.pbws.lane.LaneShorterExecutionTimeComparator;
import algorithm.comparator.pbws.lane.LaneWastedFreeTimeComparator;
import algorithm.comparator.pbws.laneindex.LaneIndexExpensiveFirstComparator;
import algorithm.comparator.pbws.laneindex.LaneIndexHigherStartTimeComparator;
import algorithm.comparator.pbws.laneindex.LaneIndexLowerStartTimeComparator;
import algorithm.comparator.pbws.reference.LaneBestUnusedCapacityHosterForLastPathComparator;
import algorithm.comparator.pbws.reference.LaneLastPathBestFitIntoWastedFreeTimeComparator;
import algorithm.comparator.pbws.task.JoinTaskComparatorLargestFirst;
import algorithm.comparator.pbws.task.TaskHigherStartTimeComparator;

public class Comparators {

	private Comparators() {
		// static class
	}
	
	static {
		laneIndexExpensiveFirstComparator = new LaneIndexExpensiveFirstComparator();

		laneExpensiveFirstComparator = new LaneExpensiveFirstComparator();
		laneIndexLowerStartTimeComparator = new LaneIndexLowerStartTimeComparator();
		laneLowerStartTimeComparator = new LaneLowerStartTimeComparator();
		joinLaneComparatorLargestFirst = new JoinLaneComparatorLargestFirst();
		joinLaneComparatorSmallestFirst = new JoinLaneComparatorSmallestFirst();
		laneLaterExecutionEndTimeComparator = new LaneLaterExecutionEndTimeComparator();
		laneLastPathHigherExecutionTimeComparator = new LaneLastPathHigherExecutionTimeComparator();
		laneWastedFreeTimeComparator = new LaneWastedFreeTimeComparator();
		laneHigherExecutionTimeComparator = new LaneHigherExecutionTimeComparator();
		laneShorterExecutionTimeComparator = new LaneShorterExecutionTimeComparator();
		laneLastPathShorterExecutionTimeComparator = new LaneLastPathShorterExecutionTimeComparator();
		laneHigherStartTimeComparator = new LaneHigherStartTimeComparator();

		lanePathCountComparator = new LanePathCountComparator();
		taskHigherStartTimeComparator = new TaskHigherStartTimeComparator();

		laneLastPathBestFitIntoWastedFreeTimeComparator = new LaneLastPathBestFitIntoWastedFreeTimeComparator();
		joinTaskComparatorLargestFirst = new JoinTaskComparatorLargestFirst();
		laneIndexHigherStartTimeComparator = new LaneIndexHigherStartTimeComparator();
		laneBestUnusedCapacityHosterForLastPathComparator = new LaneBestUnusedCapacityHosterForLastPathComparator();

	}

	public static final LaneExpensiveFirstComparator laneExpensiveFirstComparator;
	public static final LaneIndexLowerStartTimeComparator laneIndexLowerStartTimeComparator;
	public static final LaneIndexHigherStartTimeComparator laneIndexHigherStartTimeComparator;
	public static final JoinLaneComparatorLargestFirst joinLaneComparatorLargestFirst;
	public static final JoinLaneComparatorSmallestFirst joinLaneComparatorSmallestFirst;
	public static final LaneLaterExecutionEndTimeComparator laneLaterExecutionEndTimeComparator;
	public static final LaneLastPathHigherExecutionTimeComparator laneLastPathHigherExecutionTimeComparator;
	public static final LaneWastedFreeTimeComparator laneWastedFreeTimeComparator;
	public static final LaneHigherExecutionTimeComparator laneHigherExecutionTimeComparator;
	public static final LaneShorterExecutionTimeComparator laneShorterExecutionTimeComparator;
	public static final LaneLastPathShorterExecutionTimeComparator laneLastPathShorterExecutionTimeComparator;
	public static final LaneHigherStartTimeComparator laneHigherStartTimeComparator;
	public static final LanePathCountComparator lanePathCountComparator;
	public static final JoinTaskComparatorLargestFirst joinTaskComparatorLargestFirst;
	public static final TaskHigherStartTimeComparator taskHigherStartTimeComparator;
	public static final LaneLastPathBestFitIntoWastedFreeTimeComparator laneLastPathBestFitIntoWastedFreeTimeComparator;
	public static final LaneBestUnusedCapacityHosterForLastPathComparator laneBestUnusedCapacityHosterForLastPathComparator;
	public static final LaneLowerStartTimeComparator laneLowerStartTimeComparator;
	public static final LaneIndexExpensiveFirstComparator laneIndexExpensiveFirstComparator;

}