package algorithm.comparator.path;

import java.util.Comparator;

import algorithm.pcp.strategy.StrategyResult;
import statics.util.BillingUtil;
import statics.util.Duration;

public class StrategyResultComparator implements Comparator<StrategyResult> {

    @Override
    public int compare(StrategyResult data, StrategyResult globalData) {
        StrategyResult globalDataInput = globalData;
        if (data != null) {
            if (globalData == null || data.getCosts() < globalData.getCosts()) {
                globalData = data;
            } else if (data.getCosts() == globalData.getCosts()) {
                if (data.getClass() != globalData.getClass()) {
                    globalData = data;
                } else {

                    BillingUtil bu = BillingUtil.getInstance();

                    double dataRemainingTime = getRemainingTime(bu, data);
                    double globalRemainingTime = getRemainingTime(bu, globalData);

                    double dataWeight = dataRemainingTime * data.getInstance().getInstanceSize().getSpeedup();
                    double globalWeight = globalRemainingTime * globalData.getInstance().getInstanceSize().getSpeedup();

                    if (dataWeight > globalWeight) {
                        globalData = data;
                    }
                    if (dataRemainingTime > globalRemainingTime) {
                        globalData = data;
                    }
                }
            }
        }
        if (globalData == globalDataInput) {
            return 1;
        } else {
            return -1;
        }

    }

    private double getRemainingTime(BillingUtil bu, double starttime, double endtime) {
        // TODO unused capacity, billing util in algorithm
        double duration = endtime - starttime;
        double atus = bu.getUsedATUs(duration);
        double arg0remainingTime = atus
                * bu.getAtuLength() - duration;
        return arg0remainingTime;
    }

    private double getRemainingTime(BillingUtil bu, Duration arg0) {
        return getRemainingTime(bu, arg0.getStartTime(), arg0.getEndTime());
    }

}
