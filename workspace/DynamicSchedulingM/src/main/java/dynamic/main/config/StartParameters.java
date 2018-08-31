package dynamic.main.config;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public interface StartParameters {

    public static final String copyOperation = "copy";
    public static final String moveOperation = "move";
    public static final String noOperation = "no";
    public static final String systemout = "system.out";

    String getSuffix();
    
    Map<String, String> getParameters();
    
    double getScale();
    
    String getCloudFile();

    String getExecutionProfileFile();

    List<Double> getDeadlineFactors();

    List<Double> getAlphas();

    String getDependencyGraphFile();

    List<String> getExecutionTimesFiles();

    String getAlgorithmName();

    PrintStream getStdOut() throws FileNotFoundException;

    void closeStdOut();

    String getStorageDirectory();

    void setStorageDirectory(String value);

    String getCloudFileTransfer();

    String getExecutionProfileFileTransfer();

    String getDependencyGraphFileTransfer();

    String getExecutionTimesFilesTransfer();

    String getResultOutFileName();

}
