package dynamic.main;

import java.util.Locale;

import dynamic.main.config.StartParameters;
import dynamic.main.config.StartParametersImpl;
import dynamic.main.result.analyzer.ExcelResultAnalyzer;

public class ScheduleFrameworkMain {

    public static void main(String[] args) throws Throwable {
        Locale.setDefault(Locale.ENGLISH);

        if (args.length == 0) {
            throw new IllegalArgumentException("Input Data needed");
        }

        StartParameters start = new StartParametersImpl(args[0]);
        ScheduleTestFramework framework = new ScheduleTestFramework();
        framework.setStartParameters(start);
        framework.doIt();
        
//        String d = start.getStorageDirectory();
//        String f = start.getResultOutFileName();
//        ExcelResultAnalyzer.main(new String[]{d,f});

    } // main

}
