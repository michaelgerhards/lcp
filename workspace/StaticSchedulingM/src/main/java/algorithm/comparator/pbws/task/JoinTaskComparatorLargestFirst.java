package algorithm.comparator.pbws.task;

import java.util.Comparator;
import java.util.Set;

import algorithm.AbstractAlgorithm;
import statics.initialization.SchedulingTask;
import statics.initialization.impl.Lane;

/**
 * joins with more branches before joins with less branches.
 * Ties are broken by lower est before later est
 * 
 * @author Gerhards
 * 
 */
public class JoinTaskComparatorLargestFirst implements Comparator<SchedulingTask> {



	@Override
	public int compare(SchedulingTask arg0, SchedulingTask arg1) {
		Set<Lane> arg0ParentLanesOfTask = AbstractAlgorithm.getParentLanesOfTaskSetReadOnly(arg0);
		Set<Lane> arg1ParentLanesOfTask = AbstractAlgorithm.getParentLanesOfTaskSetReadOnly(arg1);
		
		int diff = arg1ParentLanesOfTask.size() - arg0ParentLanesOfTask.size();
		if (diff != 0) {
			return diff;
		} else {
			double diff2 = arg0.getStartTime() - arg1.getStartTime();
			if (diff2 == 0) {
				return 0;
			} else if (diff2 > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}

}
