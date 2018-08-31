package dynamic.main.config;

import dynamic.generated.Parameterlist;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import statics.util.Util;

public class StartParametersImpl implements StartParameters {

    private final Parameterlist list;
    private PrintStream writer;
    private boolean fileWriter;

    public StartParametersImpl(String configXml) {
        list = JAXB.unmarshal(new File(configXml), Parameterlist.class);
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> result = new HashMap<>();
        if (list.getParameters() != null) {
            list.getParameters().getParameter().forEach((parameter) -> {
                result.put(parameter.getKey(), parameter.getValue());
            });
        }
        return result;
    }

    @Override
    public String getSuffix() {
        return list.getSuffix() != null ? list.getSuffix() : "";
    }

    @Override
    public double getScale() {
        return this.list.getScale() != null ? this.list.getScale() : 1.;
    }

    @Override
    public String getCloudFile() {
        return this.list.getCloudFile().getValue().trim();
    }

    @Override
    public String getCloudFileTransfer() {
        return this.list.getCloudFile().getTransfer();
    }

    @Override
    public String getExecutionProfileFile() {
        return this.list.getProfilefile().getValue().trim();
    }

    @Override
    public String getExecutionProfileFileTransfer() {
        return this.list.getProfilefile().getTransfer();
    }

    private static List<Double> toRoundedDoubleList(List<Double> input) {
        List<Double> result = new ArrayList<Double>(input.size());
        for (Double f : input) {
            double v = Util.round2Digits(f);
            result.add(v);
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<Double> getDeadlineFactors() {
        return toRoundedDoubleList(this.list.getDeadLineFactors().getFactor());
    }

    @Override
    public List<Double> getAlphas() {
        return toRoundedDoubleList(this.list.getAlphas().getAlpha());
    }

    @Override
    public String getDependencyGraphFile() {
        return this.list.getDependencyGraphFile().getValue().trim();
    }

    @Override
    public String getDependencyGraphFileTransfer() {
        return this.list.getDependencyGraphFile().getTransfer();
    }

    @Override
    public List<String> getExecutionTimesFiles() {
        return this.list.getExecutionTimesFiles().getExcecutionTimesFile();
    }

    @Override
    public String getExecutionTimesFilesTransfer() {
        return this.list.getExecutionTimesFiles().getTranfer();
    }

    @Override
    public String getAlgorithmName() {
        return this.list.getAlgorithmName().trim();
    }

    @Override
    public PrintStream getStdOut() throws FileNotFoundException {
        if (writer == null) {
            if (this.list.getStdout().trim().toLowerCase().equals(systemout) || this.list.getStdout() == null) {
                fileWriter = false;
                writer = new PrintStream(System.out, true);
            } else {
                fileWriter = true;
                File result;

                result = new File(this.getStorageDirectory() + "\\" + this.list.getStdout().trim());
                if (!result.exists()) {
                    try {
                        result.createNewFile();
                        writer = new PrintStream(new FileOutputStream(result.getAbsolutePath()), true);
                    } catch (IOException e) {
                        System.err.println("Couldn't write new File for result");
                    }
                } else {
                    try {
                        writer = new PrintStream(new FileOutputStream(result.getAbsolutePath(), true));
                    } catch (IOException e) {
                        try {
                            result.createNewFile();
                        } catch (IOException i) {
                            System.err.println("Couldn't write new File for result");
                        }
                        System.err.println("File not found,new has been created");
                    }
                }
            }

        }
        return writer;
    }

    @Override
    public String getStorageDirectory() {
        return this.list.getOutputpath().trim();
    }

    @Override
    public void closeStdOut() {
        if (writer != null) {
            if (fileWriter) {
                writer.close();
            } else {
                writer.flush();
            }
        }
    }

    @Override
    public void setStorageDirectory(String path) {
        this.list.setOutputpath(path);
    }

    @Override
    public String toString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JAXB.marshal(list, baos);
        String s = baos.toString();
        return s;
    }

    @Override
    public String getResultOutFileName() {
        return list.getResultout().trim();
    }

}
