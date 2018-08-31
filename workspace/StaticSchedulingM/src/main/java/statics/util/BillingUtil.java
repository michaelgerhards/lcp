package statics.util;

import algorithm.misc.ScaledPseudoLane;
import java.util.Collection;
import cloud.BasicInstance;
import cloud.InstanceSize;
import statics.initialization.impl.Lane;
import statics.initialization.impl.SchedulingTaskList;

public class BillingUtil {

    private static BillingUtil instance;

    public static BillingUtil getInstance(double atuLength) {
        instance = new BillingUtil(atuLength);
        return instance;
    }

    public static BillingUtil getInstance() {
        return instance;
    }

    private final Double atuLength;

    public BillingUtil(Double atuLength) {
        this.atuLength = atuLength;
    }

    public double calcWastedTime(double catcherStart, double catcherEnd, double throwerShift, double throwerStart, double throwerEnd) {
        double duration = throwerEnd + throwerShift - catcherStart;
        double wastedTime = getUnusedCapacity(duration);

        double gap = throwerStart + throwerShift - catcherEnd;
        wastedTime += gap;
        return wastedTime;
    }

    /**
     * catcher and thrower have same instance sizes.
     *
     * @param catcher
     * @param thrower
     * @param throwerPath is migrated from thrower to catcher
     * @return
     */
    public boolean cheaperOrEquals_EqualInstanceSize_NoGaps(Lane catcher, Lane thrower, SchedulingTaskList throwerPath) {
        int saved = getSavedAtus_NoGaps_EqualSize(catcher, thrower, throwerPath);
        return saved >= 0;
    }

    public <T> double getTotalCosts(Collection<BasicInstance<T>> collection) {
        double totalCosts = 0;
        for (BasicInstance<T> instance : collection) {
            double duration = instance.getExecutionTime();
            int atus = getUsedATUs(duration);
            double resourceCosts = getCosts(atus, instance.getInstanceSize());
            totalCosts += resourceCosts;
        }
        return totalCosts;
    }

    public <T> double getCost(BasicInstance<T> thrower) {
        int throwerAtus = getUsedATUs(thrower.getExecutionTime());
        double throwerCosts = getCosts(throwerAtus, thrower.getInstanceSize());
        return throwerCosts;
    }

    public double getCombinedCosts_EqualSize(Lane catcher, Lane thrower, double throwerShift) {
        double newStart = catcher.getStartTime();
        double newEnd = thrower.getEndTime() + throwerShift;
        double newDuration = newEnd - newStart;
        int atus = getUsedATUs(newDuration);
        double newCosts = getCosts(atus, thrower.getInstanceSize());
        return newCosts;
    }

    public double getCombinedCosts_DifferentSize(Lane catcher, ScaledPseudoLane thrower, double throwerShift) {
        if (catcher.getInstanceSize() != thrower.getInstanceSize()) {
            throw new RuntimeException();
        }
        double newStart = catcher.getStartTime();
        double newEnd = thrower.getEndTime() + throwerShift;
        double newDuration = newEnd - newStart;
        int atus = getUsedATUs(newDuration);
        double newCosts = getCosts(atus, thrower.getInstanceSize());
        return newCosts;
    }

    public double getCombinedCosts_DifferentSize(ScaledPseudoLane catcher, double catcherShift, Lane thrower, double throwerShift) {
        double newStart = catcher.getStartTime() + catcherShift;
        double newEnd = thrower.getEndTime() + throwerShift;
        double newDuration = newEnd - newStart;
        int atus = getUsedATUs(newDuration);
        double newCosts = getCosts(atus, thrower.getInstanceSize());
        return newCosts;
    }

    public boolean cheaperOrEquals_DifferentInstanceSize_NoGaps(Lane thrower, Lane catcher, SchedulingTaskList throwerLastPath, ScaledPseudoLane newThrowerLastPath) {
        double throwerCosts = getCost(thrower);
        double catcherCosts = getCost(catcher);
        double combinedCosts = throwerCosts + catcherCosts;

        double lastPathExTime = throwerLastPath.getExecutionTime();
        double newThrowerExTime = thrower.getExecutionTime() - lastPathExTime;
        int newThrowerAtus = getUsedATUs(newThrowerExTime);
        double newThrowerCosts = getCosts(newThrowerAtus, thrower.getInstanceSize());

        double newLastPathExTime = newThrowerLastPath.getExecutionTime();
        int newCatcherAtus = getUsedATUs(newLastPathExTime + catcher.getExecutionTime());
        double newCatcherCosts = getCosts(newCatcherAtus, catcher.getInstanceSize());

        double newCombinedCosts = newThrowerCosts + newCatcherCosts;

        return newCombinedCosts <= combinedCosts;
    }

    public boolean cheaperOrEquals_DifferentInstanceSize_Gaps(Lane lane1,
            Lane lane2, ScaledPseudoLane newLane1) {
        double newThrowerEndTime = newLane1.getEndTime();
        double newThrowerStartTime = newLane1.getStartTime();
        double newThrowerExTime = newThrowerEndTime - newThrowerStartTime;

        double origThrowerCosts = getCost(lane1);

        double catcherCosts = getCost(lane2);

        double origCombinedCosts = origThrowerCosts + catcherCosts;

        double gap = 0;
        if (lane2.getStartTime() < newLane1.getStartTime()) {
            // lane 2 starts before lane 1
            gap = newLane1.getStartTime() - lane2.getEndTime();

        } else {
            // lane 1 starts before lane 2
            gap = lane2.getStartTime() - newLane1.getEndTime();
        }

        int newCatcherAtus = getUsedATUs(lane2.getExecutionTime() + newThrowerExTime + gap);
        double newCombinedCosts = getCosts(newCatcherAtus, lane2.getInstanceSize());
        boolean newIsCheaper = newCombinedCosts <= origCombinedCosts;
        return newIsCheaper;
    }

    /**
     * throwerpath is combined with cather -> no gaps catcher and thrower have same instance size.
     *
     * @param catcher
     * @param thrower
     * @param throwerPath is migrated from thrower to catcher
     * @return
     */
    public int getSavedAtus_NoGaps_EqualSize(Lane catcher, Lane thrower, SchedulingTaskList throwerPath) {
        int catcherATUs = getUsedATUs(catcher.getExecutionTime());
        int throwerATUs = getUsedATUs(thrower.getExecutionTime());
        int combinedATUs = catcherATUs + throwerATUs;

        int newCatcherATUs = getUsedATUs(catcher.getExecutionTime() + throwerPath.getExecutionTime());
        int newThrowerATUs = getUsedATUs(thrower.getExecutionTime() - throwerPath.getExecutionTime());
        int newCombinedATUs = newCatcherATUs + newThrowerATUs;

        int saved = combinedATUs - newCombinedATUs;
        return saved;
    }

    public double getNewCosts_NoGaps_EqualSize(Lane catcher, Lane thrower, SchedulingTaskList throwerPath) {
        int newCatcherATUs = getUsedATUs(catcher.getExecutionTime() + throwerPath.getExecutionTime());
        int newThrowerATUs = getUsedATUs(thrower.getExecutionTime() - throwerPath.getExecutionTime());
        int newCombinedATUs = newCatcherATUs + newThrowerATUs;

        double newCombinedCosts = getCosts(newCombinedATUs, thrower.getInstanceSize());
        return newCombinedCosts;
    }

    public int getUsedATUs(double duration) {
        int atus = (int) Math.ceil(duration / getAtuLength());
        return atus;
    }

    public double getCosts(int atus, InstanceSize instanceSize) {
        double costs = atus * instanceSize.getCostPerTimeInterval();
        return costs;
    }

    public double getNewCombinedCosts_LP_NoGaps_DifferentSize(Lane thrower, Lane catcher, SchedulingTaskList throwerLastPath, ScaledPseudoLane newThrowerLastPath) {
        int newThrowerAtus = getUsedATUs(thrower.getExecutionTime() - throwerLastPath.getExecutionTime());
        double newThrowerCosts = getCosts(newThrowerAtus, thrower.getInstanceSize());

        double newLastPathEndTime = newThrowerLastPath.getEndTime();

        double newLastPathStartTime = newThrowerLastPath.getStartTime();// (throwerLastPath.get(0));
        double newLastPathExTime = newLastPathEndTime - newLastPathStartTime;

        int newCatcherAtus = getUsedATUs(catcher.getExecutionTime() + newLastPathExTime);
        double newCatcherCosts = getCosts(newCatcherAtus, catcher.getInstanceSize());
        double newCombinedCosts = newThrowerCosts + newCatcherCosts;
        return newCombinedCosts;
    }

    public double getCosts_GapsAll_DifferSize(Lane thrower, Lane catcher,
            ScaledPseudoLane newCatcher, double throwerShiftTime) {
        double newDuration = thrower.getEndTime() + throwerShiftTime - (newCatcher.getStartTime());
        int newAtus = getUsedATUs(newDuration);
        double newCosts = getCosts(newAtus, thrower.getInstanceSize());
        return newCosts;
    }

    public double getCosts_LP_NoGaps_DifferSize(Lane catcher, Lane thrower, ScaledPseudoLane newThrowerLastPath, SchedulingTaskList throwerLastPath) {
        int newThrowerAtus = getUsedATUs(thrower.getExecutionTime() - throwerLastPath.getExecutionTime());
        double newThrowerCosts = getCosts(newThrowerAtus, thrower.getInstanceSize());

        double newLastPathExTime = newThrowerLastPath.getExecutionTime();

        int newCatcherAtus = getUsedATUs(catcher.getExecutionTime() + newLastPathExTime);
        double newCatcherCosts = getCosts(newCatcherAtus, catcher.getInstanceSize());
        double newCombinedCosts = newThrowerCosts + newCatcherCosts;
        return newCombinedCosts;
    }

    public int getAtuDiff(Duration oldD, Duration newD) {
        double oldDuration = oldD.getEndTime() - oldD.getStartTime();
        int oldAtus = getUsedATUs(oldDuration);
        double newDuration = newD.getEndTime() - newD.getStartTime();
        int newAtus = getUsedATUs(newDuration);
        int atuDiff = newAtus - oldAtus;
        return atuDiff;
    }

    public double getUnusedCapacity(double duration) {
        int atus = getUsedATUs(duration);
        double payedTime = getPayedTime(atus);
        return payedTime - duration;
    }

    public double getPayedTime(int atus) {
        return atus * getAtuLength();
    }

    public double getOvertime(double executiontime) {
        int lessAtus = getUsedATUs(executiontime) - 1;
        double lastBillingStart = lessAtus * getAtuLength();
        return executiontime - lastBillingStart;
    }

    public double getBillingEndTime(Duration duration) {
        return getBillingEndTime(duration.getStartTime(), duration.getEndTime());
    }

    public double getBillingEndTime(double starttime, double endtime) {
        double duration = endtime - starttime;
        double atus = getUsedATUs(duration);
        double result = starttime + atus * getAtuLength();
        return result;
    }

    public double getBillingStartTime(Duration readOnlyInstance) {
        double duration = readOnlyInstance.getEndTime() - readOnlyInstance.getStartTime();
        double atus = getUsedATUs(duration);
        double start = readOnlyInstance.getEndTime() - atus * getAtuLength();
        if (start < 0) {
            start = 0;
        }
        return start;
    }

    public Double getAtuLength() {
        return atuLength;
    }

}
