package algorithm;

import org.apache.log4j.Logger;
import statics.initialization.WorkflowInstance;

public interface StaticSchedulingAlgorithm extends WorkflowContainer {

    String getAlgorithmName();

    String getAlgorithmNameAbbreviation();

    WorkflowInstance schedule(WorkflowInstance workflow);

    @Override
    WorkflowInstance getWorkflow();

    default Logger getLogger() {
        return Logger.getRootLogger();
    }

}
