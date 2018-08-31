package algorithm.heftmy;

import algorithm.AbstractAlgorithm;
import algorithm.StaticSchedulingAlgorithm;
import reality.RealResource;
import statics.initialization.SchedulingTask;
import statics.initialization.WorkflowInstance;
import statics.initialization.impl.Lane;
import statics.util.Util;

/**
 *
 * @author mike
 */
public class HEFTMy extends AbstractAlgorithm {
    
    @Override
    public String getAlgorithmName() {
        return "My HEFT";
    }
    
    @Override
    public String getAlgorithmNameAbbreviation() {
        return "My HEFT";
    }
    
    

    @Override
    protected void scheduleIntern() {
        
        
        
        
        
    }
    
    
    private void scheduleTask(SchedulingTask task) {
//        
//        
//        RealResource best = null;
//                double bestRemaining = Double.MAX_VALUE;
//                for (RealResource r : availableLanes) {
//                    double upTime = now - r.getStartTime();
//
//                    double unusedCapacity = billingUtil.getUnusedCapacity(upTime);
//                    while (exTime - unusedCapacity > Util.DOUBLE_THRESHOLD) {
//                        unusedCapacity += workflow.getAtuLength();
//                    }
//
//                    double remaining = unusedCapacity - exTime;
//                    if (remaining < bestRemaining) {
//                        bestRemaining = remaining;
//                        best = r;
//                    }
//
//                }
//
//                Lane lane;
//                if (best == null) {
//                    lane = workflow.instantiate(fastestSize);
//                } else {
//                    lane = (Lane) best.getInstance();
//                    availableLanes.remove(best);
//                }
        
        
    }
    
}
