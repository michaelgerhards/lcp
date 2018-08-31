package generator.main;

import java.io.File;




import simulation.generator.RunAll;
import simulation.generator.app.AbstractApplication;
import simulation.generator.app.Application;
import simulation.generator.app.CyberShake;
import simulation.generator.app.Genome;
import simulation.generator.app.LIGO;
import simulation.generator.app.Montage;
import simulation.generator.app.SIPHT;

public class MainUtil {

    private MainUtil() {
        // private
    }

    public static void deleteFile(String dirPath, boolean deleteFiles) {
        if (deleteFiles) {
            File dir = new File(dirPath);
            String[] files = dir.list();
            System.out.println("delte in " + dirPath);
            for (String filename : files) {
                System.out.println("delete: " + filename);
                File f = new File(dirPath + filename);
                f.delete();
            }
        }
    }

    public static void generateFile(String dirPath, int size, String name, int fileNumber) throws Exception {
        if (size <= 0) {
            return;
        }
        for (int i = 0; i < fileNumber; ++i) {
            String fileName = String.format("%s%s_%02d.xml", name, String.valueOf(size), i);
            File file = new File(dirPath + fileName);
            if (!file.exists()) {
                int offset = 0;
                while (true) {
                    try {
                        System.out.print("create: " + fileName + " ... ");
                        Application app = nameToApp(name);
                        String sizeStr = String.valueOf(size - offset);
                        RunAll.run(app, file, "-n", sizeStr);
                        System.out.println(" ... done");
                        break;
                    } catch (Exception ex) {
                        System.out.println(" ... failed: " + ex.getMessage());
                        offset++;
                    }
                }
            } else {
                System.out.println("existed: " + fileName);
            }
        }
    }

    public static Application nameToApp(String name) {
        if (name.equals("Epigenomics_")) {
            return new Genome();
        } else if (name.equals("Inspiral_")) {
            return new LIGO();
        } else if (name.equals("Sipht_")) {
            return new SIPHT();
        } else if (name.equals("Cybershake_")) {
            return new CyberShake();
        } else if (name.equals("Montage_")) {
            return new Montage();
        } else {
            throw new RuntimeException("name not found " + name);
        }
    }

}
