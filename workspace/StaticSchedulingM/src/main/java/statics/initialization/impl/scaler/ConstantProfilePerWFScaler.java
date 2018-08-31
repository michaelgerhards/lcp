package statics.initialization.impl.scaler;

import executionprofile.generated.Executionprofile;


/**
 *
 * @author mike
 */
public class ConstantProfilePerWFScaler implements ProfileScaler {

    
    
    private final double epi;
    private final double inspi;
    private final double sipht;
    private final double cybershake;
    private final double montage;

    public ConstantProfilePerWFScaler(double epi, double inspi, double sipht, double cybershake, double montage) {
        this.epi = epi;
        this.inspi = inspi;
        this.sipht = sipht;
        this.cybershake = cybershake;
        this.montage = montage;
    }

    private double getFactorNamespace(String ns) {
        switch (ns) {
            case "Genome":
                return epi;
            case "LIGO":
                return inspi;
            case "SIPHT":
                return sipht;
            case "CyberShake":
                return cybershake;
            case "Montage":
                return montage;
            default:
                throw new RuntimeException("unknown job namespace: " + ns);
        }
    }

    @Override
    public double getExecutionTime(Executionprofile.Workflows.Workflow.Jobs.Job job, double value) {
        String ns = getNameSpace(job.getName());
        double factor = getFactorNamespace(ns);
        return factor * value;
    }

    @Override
    public double getVarTime(Executionprofile.Workflows.Workflow.Jobs.Job job, double value) {
        String ns = getNameSpace(job.getName());
        double factor = getFactorNamespace(ns);
        return factor * value;
    }

    private String getNameSpace(String name) {
        return statics.initialization.WorkflowInstance.getWorkflowName(name);
    }

}
