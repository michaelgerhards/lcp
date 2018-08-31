package simulation.generator;

import java.io.File;
import java.util.Locale;
import static simulation.generator.RunAll.run;
import simulation.generator.app.CyberShake;

import simulation.generator.app.Genome;
import simulation.generator.app.LIGO;
import simulation.generator.app.Montage;
import simulation.generator.app.SIPHT;

public class MyGenerate {

    public static void main(String[] args) throws Throwable {
        Locale.setDefault(Locale.ENGLISH);

        for (int i = 0; i < 2; ++i) {
            run(new CyberShake(), new File("CyberShake_30_" + String.format("%02d", i) + ".xml"), "-n", "30");
            run(new Montage(), new File("Montage_25_" + String.format("%02d", i) + ".xml"), "-n", "25");
            run(new Genome(), new File("Epigenomics_24_" + String.format("%02d", i) + ".xml"), "-n", "24");
            run(new LIGO(), new File("Inspiral_30_" + String.format("%02d", i) + ".xml"), "-n", "30");
            run(new SIPHT(), new File("Sipht_30_" + String.format("%02d", i) + ".xml"), "-n", "30");
        }
//        for (int i = 0; i < 20; ++i) {
//            run(new Genome(), new File("Epigenomics_100_" + String.format("%02d", i) + ".xml"), "-n", "100");
//            run(new CyberShake(), new File("CyberShake_100_" + String.format("%02d", i) + ".xml"), "-n", "100");
//            run(new Montage(), new File("Montage_100_" + String.format("%02d", i) + ".xml"), "-n", "100");
//            run(new LIGO(), new File("Inspiral_100_" + String.format("%02d", i) + ".xml"), "-n", "100");
//            run(new SIPHT(), new File("Sipht_100_" + String.format("%02d", i) + ".xml"), "-n", "100");
//        }
//        for (int i = 0; i < 10; ++i) {
//            run(new CyberShake(), new File("CyberShake_1000_" + String.format("%02d", i) + ".xml"), "-n", "1000");
//            run(new Montage(), new File("Montage_1000_" + String.format("%02d", i) + ".xml"), "-n", "1000");
//            run(new Genome(), new File("Epigenomics_997_" + String.format("%02d", i) + ".xml"), "-n", "997");
//            run(new LIGO(), new File("Inspiral_1000_" + String.format("%02d", i) + ".xml"), "-n", "1000");
//            run(new SIPHT(), new File("Sipht_1000_" + String.format("%02d", i) + ".xml"), "-n", "1000");
//        }
        for (int i = 0; i < 10; ++i) {
//            run(new CyberShake(), new File("CyberShake_10000_" + String.format("%02d", i) + ".xml"), "-n", "10000");
//            run(new Montage(), new File("Montage_10000_" + String.format("%02d", i) + ".xml"), "-n", "10000");
//            run(new Genome(), new File("Epigenomics_9970_" + String.format("%02d", i) + ".xml"), "-n", "9970");
//            run(new LIGO(), new File("Inspiral_10000_" + String.format("%02d", i) + ".xml"), "-n", "10000");
//            run(new SIPHT(), new File("Sipht_10000_" + String.format("%02d", i) + ".xml"), "-n", "10000");
        }
    }
}
