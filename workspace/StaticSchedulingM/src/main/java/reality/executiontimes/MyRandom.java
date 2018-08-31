//package reality.executiontimes;
//
//import java.util.Random;
//
//public class MyRandom {
//
//    private MyRandom() {
//        // private
//    }
//
//    private static final Random RANDOM = new Random();
//
//    private static boolean used = false;
//
//    public static void setSeedOnce(long seed) {
//        if (used) {
//            throw new RuntimeException();
//        }
//        RANDOM.setSeed(seed);
//    }
//
//    static double nextGaussian(double y, double o) {
//        used = true;
//        double val = RANDOM.nextGaussian();
//        double result = val * o + y;
//        return result;
//    }
//
//}
