package statics.initialization;

import java.util.List;
import java.util.Map;

import algorithm.WorkflowContainer;
import cloud.Instance;
import cloud.InstanceSize;
import java.util.HashMap;
import java.util.Set;
import org.apache.log4j.Logger;
import statics.initialization.impl.Lane;
import statics.util.BillingUtil;
import statics.util.Util;

public interface WorkflowInstance extends Cloneable, WorkflowContainer {

    public static boolean performanceMode = true;

    double getAtuLength();

    List<InstanceSize> getInstanceSizes();

    double getDeadline();

    String getWorkflowName();

    Set<SchedulingTask> getTasks();

    SchedulingTask getEntry();

    SchedulingTask getExit();

    Lane instantiate(InstanceSize instanceSize);

    Map<String, Instance> getInstances();

    Instance getDummyInstance();

    double getMakespan();

    double getTotalCost();

    boolean existsLane(Lane lane);

    Set<Lane> getLanes();

    String getAlgorithmName();

    void setAlgorithmName(String name);

    void repairExit();

    WorkflowInstance clone();
    
    void reset();

//    byte[] serialize();

    @Override
    default WorkflowInstance getWorkflow() {
        return this;
    }

    default int getEdgeCount() {
        return getTasks().stream().map((task) -> task.getChildren().size()).reduce(0, Integer::sum);
    }

    default void logStatus(Logger logger) {
        Map<InstanceSize, Integer> atusCombinedPerSize = new HashMap<>();
        Map<InstanceSize, Integer> countPerSize = new HashMap<>();
        getInstanceSizes().stream().filter((size) -> !(size.isDummy())).forEach((size) -> {
            atusCombinedPerSize.put(size, 0);
            countPerSize.put(size, 0);
        });

        for (Lane lane : getLanes()) {
            InstanceSize size = lane.getInstanceSize();
            int atus = BillingUtil.getInstance().getUsedATUs(lane.getDuration());

            int comAtus = atusCombinedPerSize.get(size);
            comAtus += atus;
            atusCombinedPerSize.put(size, comAtus);

            int comCount = countPerSize.get(size);
            comCount++;
            countPerSize.put(size, comCount);
        }
        getInstanceSizes().stream().filter((size) -> !(size.isDummy())).forEach((size) -> {
            int atus = atusCombinedPerSize.get(size);
            int count = countPerSize.get(size);
            logger.info(String.format("size:\t%s\tcount:\t%d\tatus:\t%d", size.toString(), count, atus));
        });
    }

    default void logGaps(Logger logger) {
        int gapResources = 0;
        for (Lane l : getLanes()) {
            int gapCount = 0;
            double totalGapSize = 0.;
            for (int i = 0; i < l.getTasksCount() - 1; ++i) {
                SchedulingTask pred = l.getUmodTasks().get(i);
                SchedulingTask succ = l.getUmodTasks().get(i + 1);
                double end = pred.getEndTime();
                double start = succ.getStartTime();

                if (start - end > Util.DOUBLE_THRESHOLD) {
                    ++gapCount;
                    totalGapSize += start - end;
                }
            }
            if (gapCount > 0) {
                ++gapResources;
                logger.info(String.format("gapCount=\t%d\ttotalGapSize=\t%.2f", gapCount, totalGapSize));
            }
        }
        logger.info("gapResources:\t" + gapResources + "\ttotalResources:\t" + getLanes().size());

    }

    public static int jobIDToInt(String jobId) {
        String number = jobId.substring(2); // ID00000
        return Integer.parseInt(number);
    }

    public static int jobNameToInt(String jobName) {
        return jobNameToInt.get(jobName);
    }

    public static String intToJobName(int i) {
        return intTojobName.get(i);
    }

    static final Map<String, Integer> jobNameToInt = new HashMap<>(); // private not allowed in interface
    static final Map<Integer, String> intTojobName = new HashMap<>(); // private not allowed in interface

    static final Map<String, String> nameToNamespace = getWorkflowNameInstance();
    
    static void jobNameToIntInit(String jobName) {
        Integer i = jobNameToInt.get(jobName);
        if (i == null) {
            i = jobNameToInt.size() + 1;
            jobNameToInt.put(jobName, i);
            intTojobName.put(i, jobName);
        } else {
            throw new RuntimeException();
        }
    }

    public static String getWorkflowName(String tasktype) {
        return nameToNamespace.get(tasktype);
    }

    public static Map<String, String> getWorkflowNameInstance() {
        Map<String, String> map = new HashMap<>();
        map.put("filterContams_chr21", "Genome");
        map.put("maqindex_chr21", "Genome");
        map.put("mapMerge_chr21", "Genome");
        map.put("fastq2bfq_chr21", "Genome");
        map.put("pileup_chr21", "Genome");
        map.put("map_chr21", "Genome");
        map.put("fastqSplit_chr21", "Genome");
        map.put("sol2sanger_chr21", "Genome");

        map.put("Inspiral", "LIGO");
        map.put("Thinca", "LIGO");
        map.put("TrigBank", "LIGO");
        map.put("TmpltBank", "LIGO");

        map.put("Blast_synteny", "SIPHT");
        map.put("Transterm", "SIPHT");
        map.put("FFN_Parse", "SIPHT");
        map.put("Blast_QRNA", "SIPHT");
        map.put("Blast", "SIPHT");
        map.put("RNAMotif", "SIPHT");
        map.put("Findterm", "SIPHT");
        map.put("Patser", "SIPHT");
        map.put("SRNA_annotate", "SIPHT");
        map.put("Blast_candidate", "SIPHT");
        map.put("Blast_paralogues", "SIPHT");
        map.put("Patser_concate", "SIPHT");
        map.put("SRNA", "SIPHT");

        map.put("SeismogramSynthesis", "CyberShake");
        map.put("ZipPSA", "CyberShake");
        map.put("ZipSeis", "CyberShake");
        map.put("ExtractSGT", "CyberShake");
        map.put("PeakValCalcOkaya", "CyberShake");

        map.put("mImgTbl", "Montage");
        map.put("mAdd", "Montage");
        map.put("mConcatFit", "Montage");
        map.put("mShrink", "Montage");
        map.put("mBgModel", "Montage");
        map.put("mDiffFit", "Montage");
        map.put("mProjectPP", "Montage");
        map.put("mBackground", "Montage");
        map.put("mJPEG", "Montage");

        jobNameToIntInit("filterContams_chr21");
        jobNameToIntInit("maqindex_chr21");
        jobNameToIntInit("mapMerge_chr21");
        jobNameToIntInit("fastq2bfq_chr21");
        jobNameToIntInit("pileup_chr21");
        jobNameToIntInit("map_chr21");
        jobNameToIntInit("fastqSplit_chr21");
        jobNameToIntInit("sol2sanger_chr21");

        jobNameToIntInit("Inspiral");
        jobNameToIntInit("Thinca");
        jobNameToIntInit("TrigBank");
        jobNameToIntInit("TmpltBank");

        jobNameToIntInit("Blast_synteny");
        jobNameToIntInit("Transterm");
        jobNameToIntInit("FFN_Parse");
        jobNameToIntInit("Blast_QRNA");
        jobNameToIntInit("Blast");
        jobNameToIntInit("RNAMotif");
        jobNameToIntInit("Findterm");
        jobNameToIntInit("Patser");
        jobNameToIntInit("SRNA_annotate");
        jobNameToIntInit("Blast_candidate");
        jobNameToIntInit("Blast_paralogues");
        jobNameToIntInit("Patser_concate");
        jobNameToIntInit("SRNA");

        jobNameToIntInit("SeismogramSynthesis");
        jobNameToIntInit("ZipPSA");
        jobNameToIntInit("ZipSeis");
        jobNameToIntInit("ExtractSGT");
        jobNameToIntInit("PeakValCalcOkaya");

        jobNameToIntInit("mImgTbl");
        jobNameToIntInit("mAdd");
        jobNameToIntInit("mConcatFit");
        jobNameToIntInit("mShrink");
        jobNameToIntInit("mBgModel");
        jobNameToIntInit("mDiffFit");
        jobNameToIntInit("mProjectPP");
        jobNameToIntInit("mBackground");
        jobNameToIntInit("mJPEG");

        return map;
    }

}
