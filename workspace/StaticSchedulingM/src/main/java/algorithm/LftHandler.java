//package algorithm;
//
//import java.util.Collection;
//
//import statics.initialization.SchedulingTask;
//import util.CloudUtil;
//import util.Util;
//
//public class LftHandler {
//
//	private final AbstractAlgorithm algorithm;
//
//	public LftHandler(AbstractAlgorithm algorithm) {
//		this.algorithm = algorithm;
//	}
//
//	public void calcLFT() {
//		// lft = latest finish time
//		for (SchedulingTask task : algorithm.getWorkflow().getTasks().values()) {
//			task.setLatestEndTime((double) -1);
//		}
//		AbstractAlgorithm.calcLFT(algorithm.getWorkflow().getEntry());
//	}
//
//}
