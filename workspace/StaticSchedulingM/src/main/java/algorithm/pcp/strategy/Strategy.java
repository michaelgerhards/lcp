package algorithm.pcp.strategy;

import java.util.List;

import cloud.BasicInstance;
import statics.initialization.SchedulingTask;

public interface Strategy {

	StrategyResult tryStrategy(BasicInstance<SchedulingTask> instance, List<SchedulingTask> pcp);
	
	String getName();
        
        int getLoopCount();
        
        int getTaken();
        
        int getAccepted();

}