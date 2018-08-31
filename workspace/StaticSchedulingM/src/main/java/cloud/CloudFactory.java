package cloud;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXB;

public class CloudFactory {

    public static final int HOUR_IN_SECONDS = 3600;
    public static final int MEGABYTE_IN_BYTE_20 = 20000000;

    private CloudFactory() {
        // nothing
    }

//	public static Cloud createSingleInstanceCloud() {
//		InstanceSize size = new InstanceSize("0", 1, 1);
//		List<InstanceSize> sizes = new ArrayList<InstanceSize>();
//		sizes.add(size);
//		sizes.add(InstanceSize.createDummy());
//		Cloud cloud = new Cloud(HOUR_IN_SECONDS, sizes);
//		return cloud;
//	}
//	public static Cloud create3Linear() {
//		List<InstanceSize> sizes = new ArrayList<InstanceSize>();
//		sizes.add(InstanceSize.createDummy());
//		int max = 3;
//		for (int i = 0; i < max; ++i) {
//			InstanceSize size = new InstanceSize("" + i, max - i, max - i);
//			sizes.add(size);
//		}
//		Cloud cloud = new Cloud(HOUR_IN_SECONDS, sizes);
//		return cloud;
//	}
    public static Cloud createFromFile(File f) {
        statics.generated.Cloud cloud = JAXB.unmarshal(f, statics.generated.Cloud.class);
        double atuLength = cloud.getAtulength();
        List<statics.generated.Cloud.Instancesizes.Instancesize> sizes = cloud.getInstancesizes().getInstancesize();
        List<InstanceSize> cloudsizes = new ArrayList<>();
        for (statics.generated.Cloud.Instancesizes.Instancesize size : sizes) {
            String name = size.getName();
            float costperatu = size.getCostperatu();
            float speedup = size.getSpeedup();

            InstanceSize cloudsize = new InstanceSize(name, speedup, costperatu);
            cloudsizes.add(cloudsize);
        }
        cloudsizes.add(InstanceSize.createDummy());
        Cloud result = new Cloud(atuLength, cloudsizes);
        return result;
    }

}
