package algorithm.comparator.path;

import java.util.Comparator;

import algorithm.pcp.strategy.StrategyResult;

public class NewStrategyResultComparator implements Comparator<StrategyResult> {

	@Override
	public int compare(StrategyResult data, StrategyResult globalData) {
		StrategyResult globalDataInput = globalData;

		if (data != null
				&& (globalData == null || data.getCosts() < globalData
						.getCosts())) {
			// TODO how to handle equal costs?
			globalData = data;
		}

		if (globalData == globalDataInput) {
			return 1;
		} else {
			return -1;
		}

	}

}
