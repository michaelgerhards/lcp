package statics.initialization.impl.scaler;

import executionprofile.generated.Adag;

/**
 *
 * @author mike
 */
public class ConstantDaxPerWFScaler implements DaxScaler {

    private final double epi;
    private final double inspi;
    private final double sipht;
    private final double cybershake;
    private final double montage;

    public ConstantDaxPerWFScaler(double epi, double inspi, double sipht, double cybershake, double montage) {
        this.epi = epi;
        this.inspi = inspi;
        this.sipht = sipht;
        this.cybershake = cybershake;
        this.montage = montage;
    }

    @Override
    public double getExecutionTime(Adag.Job job, double value) {
        String ns = job.getNamespace();
        double factor = getFactorNamespace(ns);
        return factor * value;
    }

    @Override
    public double getVarTime(Adag.Job job, double value) {
        String ns = job.getNamespace();
        double factor = getFactorNamespace(ns);
        return factor * value;
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

}
