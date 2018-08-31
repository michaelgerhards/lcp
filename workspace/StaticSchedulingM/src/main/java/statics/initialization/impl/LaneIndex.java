package statics.initialization.impl;

import java.io.Serializable;

public class LaneIndex implements Comparable<LaneIndex>, Serializable {

    private static final long serialVersionUID = 6913222997912652653L;
    private final int value;
    private Lane lane;

    LaneIndex(int value, Lane lane) {
        if (value < 0) {
            throw new IllegalArgumentException("value < 0: " + value);
        }
        this.value = value;
        this.lane = lane;
    }

    @Override
    public int compareTo(LaneIndex arg0) {
        return value - arg0.value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.value;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LaneIndex other = (LaneIndex) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    
    
   

    @Override
    public String toString() {
        return value == 0 ? "DUMMY" : String.valueOf(value);
    }

    public Lane getLane() {
        return lane;
    }

    void invalidateLane() {
        lane = null;
    }

}
