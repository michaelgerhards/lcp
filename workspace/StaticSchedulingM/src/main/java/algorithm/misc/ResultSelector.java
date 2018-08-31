package algorithm.misc;



import algorithm.misc.aggregation.AggregationResult;

public interface ResultSelector {

	AggregationResult selectResult(AggregationResult result1,
			AggregationResult result2);

}