package generator.main;

import generator.ExecutionProfileGenerator_Quantile;

import java.io.File;
import java.util.Locale;

public class GenerateAll_Quantiles {

    public static void main(String[] args) throws Throwable {
        Locale.setDefault(Locale.ENGLISH);
        String dirPath = "C:/SVNneu/development/scheduling/data/all workflow graphs/";
        int epiSize = 24;
        int inspiSize = 30;
        int siphtSize = 30;
        int cyberSize = 30;
        int montageSize = 25;

        int fileNumber = 3;

        String profilePath = "C:/SVNneu/development/scheduling/data/executionprofiles/";
        String csvFileName = "data_profile_quantile_65.csv";
        String profileName = "generated_profile_quantile_65.xml";
        boolean deleteFiles = false;
        boolean generateFiles = false;
        boolean generateProfiles = false;

        // String dirPath =
        // "C:/SVNneu/development/scheduling/data/largeexecutiontimes/";
        // int epiSize = 997;
        // int inspiSize = 1000;
        // int siphtSize = 1000;
        // int cyberSize = 1000;
        // int montageSize = 1000;
        //
        // int fileNumber = 20;
        //
        // String profilePath = null;
        // String csvFileName = null;
        // String profileName = null;
        // boolean deleteFiles = false;
        // boolean generateFiles = true;
        // boolean generateProfiles = false;
//		String dirPath = "C:/SVNneu/development/scheduling/data/trainingset/";
//		int epiSize = 997;
//		int inspiSize = 1000;
//		int siphtSize = 1000;
//		int cyberSize = 1000;
//		int montageSize = 1000;
//
//		int fileNumber = 20;
//
//		String profilePath = "C:/SVNneu/development/scheduling/data/executionprofiles/";
//		String csvFileName = "trainingsetdata_profile_mean_var.csv";
//		String profileName = "trainingsetdata_profile_meav_var.xml";
//		boolean deleteFiles = false;
//		boolean generateFiles = false;
//		boolean generateProfiles = true;
//        String dirPath = "C:/SVNneu/development/scheduling/data/largeexecutiontimes/";
        // config ends here
        System.out.println("delete files");
        MainUtil.deleteFile(dirPath, deleteFiles);

        System.out.println("generate files");
        if (generateFiles) {
            System.out.println("create in " + dirPath);
            MainUtil.generateFile(dirPath, epiSize, "Epigenomics_", fileNumber);
            MainUtil.generateFile(dirPath, inspiSize, "Inspiral_", fileNumber);
            MainUtil.generateFile(dirPath, siphtSize, "Sipht_", fileNumber);
            MainUtil.generateFile(dirPath, cyberSize, "Cybershake_", fileNumber);
            MainUtil.generateFile(dirPath, montageSize, "Montage_", fileNumber);
        }
        System.out.println("generate profilesfiles");
        if (generateProfiles) {
            File dir = new File(dirPath);
            File csvFile = new File(profilePath + csvFileName);
            File profFile = new File(profilePath + profileName);
//            ExecutionProfileGenerator_MeanVar profile = new ExecutionProfileGenerator_MeanVar();
//            ExecutionProfileGenerator_Quantile profile = new ExecutionProfileGenerator_Quantile(1);
            ExecutionProfileGenerator_Quantile profile = new ExecutionProfileGenerator_Quantile(0.65);
            profile.readAllJobsFromDirectory(dir);
            profile.calcValues(null);
            profile.printExecutionProfile(profFile);
        }
        System.out.println("THE END");
    }

}
