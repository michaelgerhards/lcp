package cloud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Cloud implements Serializable {

    private static final long serialVersionUID = -132900123735408683L;
    private final List<InstanceSize> sizes;
    private final double atuLength;

    Cloud(double atuLength, List<InstanceSize> sizes) {
        if (atuLength <= 0) {
            throw new IllegalArgumentException("atu length must be > 0 but was " + atuLength);
        }
        this.atuLength = atuLength;
        List<InstanceSize> sorted = new ArrayList<>(sizes);
        Collections.sort(sorted);
        this.sizes = Collections.unmodifiableList(sorted);
    }

    public List<InstanceSize> getSizes() {
        return sizes;
    }

    public double getAtuLength() {
        return atuLength;
    }

    @Override
    public String toString() {
        return "Cloud [sizes=" + Arrays.toString(sizes.toArray()) + ", atuLength=" + atuLength + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(atuLength);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((sizes == null) ? 0 : sizes.hashCode());
        return result;
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
        Cloud other = (Cloud) obj;
        if (Double.doubleToLongBits(atuLength) != Double
                .doubleToLongBits(other.atuLength)) {
            return false;
        }
        if (sizes == null) {
            if (other.sizes != null) {
                return false;
            }
        } else if (!sizes.equals(other.sizes)) {
            return false;
        }
        return true;
    }

}
