package reality.executiontimes;

public class JobExecutionTimeManager {

    private static ExecutionTimes exTimes;

    // RealJob: RealJob
    public static ExecutionTimes getInstance() {
        if (exTimes == null) {
            throw new RuntimeException("JobExecutionTimeManager not set");
        }
        return exTimes;
    }

    public static void setInstance(ExecutionTimes exTimes) {
        JobExecutionTimeManager.exTimes = exTimes;
    }

    private JobExecutionTimeManager() {
        // singleton
    }

}
