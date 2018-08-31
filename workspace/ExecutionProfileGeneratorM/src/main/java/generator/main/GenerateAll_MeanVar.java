package generator.main;

import generator.ExecutionProfileGenerator_MeanVar;
import generator.ExecutionProfileGenerator_Quantile;

import java.io.File;
import java.util.Locale;

public class GenerateAll_MeanVar {

    public static void main(String[] args) throws Throwable {
        Locale.setDefault(Locale.ENGLISH);
        String dirPath = "C:/SVNneu/development/scheduling/data/all workflow graphs/smalltest/";
        int epiSize = 9970;
        int inspiSize = 0;
        int siphtSize = 0; // 30
        int cyberSize = 0; // 30
        int montageSize = 0; // 25

        int fileNumber = 1;

        String profilePath = "C:/SVNneu/development/scheduling/data/executionprofiles/";
        String csvFileName = "data_profile_meanvar_smalltest.csv";
        String profileName = "generated_profile_meanvar_smalltest.xml";
        boolean deleteFiles = true;
        boolean generateFiles = true;
        boolean generateProfiles = true;

        
        
        
        
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
            ExecutionProfileGenerator_MeanVar profile = new ExecutionProfileGenerator_MeanVar();
            profile.readAllJobsFromDirectory(dir);
            profile.calcValues(csvFile);
            profile.printExecutionProfile(profFile);
        }
        System.out.println("THE END");
    }

}
