package statics.util;

public interface Duration {

	double getStartTime();

	double getEndTime();
	
	default double getDuration() {
		return Util.round2Digits(getEndTime() - getStartTime());
	}
	
}
