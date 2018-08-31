package cloud;

import java.io.Serializable;

public final class InstanceSize implements Comparable<InstanceSize>, Serializable {

    private static final long serialVersionUID = 1944703805223880198L;
    private final String name;
    private final double costPerTimeInterval;
    private final double speedup;
    private final boolean isDummy;
    private final int myIndex;
    private static int index = 0;

    static InstanceSize createDummy() {
        InstanceSize dummy = new InstanceSize("DUMMY", 0., 0., true);
        return dummy;
    }

    InstanceSize(String name, double speedup, double costsPerTimeInterval) {
        this(name, speedup, costsPerTimeInterval, false);
    }

    private InstanceSize(String name, double speedup, double costsPerTimeInterval, boolean isDummy) {
        this.name = name;
        this.speedup = speedup;
        this.costPerTimeInterval = costsPerTimeInterval;
        this.isDummy = isDummy;
        this.myIndex = index++;
    }

    @Override
    public String toString() {
        return "InstanceSize{" + "name=" + name + ", costPerTimeInterval=" + costPerTimeInterval + ", speedup=" + speedup + ", myIndex=" + myIndex + '}';
    }

    

    public String getName() {
        return name;
    }

    public double getCostPerTimeInterval() {
        return costPerTimeInterval;
    }

    public double getSpeedup() {
        return speedup;
    }

    @Override
    public int compareTo(InstanceSize o) {
        return name.compareTo(o.name);
    }

    public boolean isFaster(InstanceSize other) {
        return speedup > other.speedup;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.myIndex;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InstanceSize other = (InstanceSize) obj;
        if (this.myIndex != other.myIndex) {
            return false;
        }
        return true;
    }

    public boolean isDummy() {
        return isDummy;
    }

}
