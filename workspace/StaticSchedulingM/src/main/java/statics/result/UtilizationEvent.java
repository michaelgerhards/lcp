package statics.result;

import statics.util.Util;

public class UtilizationEvent implements Comparable<UtilizationEvent> {

	public double time;
	public int type;

	public UtilizationEvent(double time, int type) {
		super();
		this.time = time;
		this.type = type;
	}

	@Override
	public int compareTo(UtilizationEvent o) {
		double diff = time - o.time;

		if (Math.abs(diff) < Util.DOUBLE_THRESHOLD) {
			return 0;
		} else if (time < o.time) {
			return -1;
		} else {
			return 1;
		}
	}

}
