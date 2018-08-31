package statics.initialization.impl;

import java.io.File;

import cloud.Cloud;
import cloud.CloudFactory;
import statics.initialization.DependencyGraph;
import statics.initialization.SchedulingInformation;
import statics.initialization.WorkflowTemplate;
import statics.initialization.impl.scaler.DaxScaler;
import statics.initialization.impl.scaler.ProfileScaler;
import statics.util.Debug;

public class WorkflowTemplateFactory {

    private Cloud cloud;
    private DependencyGraph graph;
    private HomogenRuntimeProfilePerId id;
    private HomogenRuntimeProfilePerType type;
    private SchedulingInformation information;

    public Cloud getCloud() {
        return cloud;
    }

    public void setCloud(Cloud cloud) {
        if (this.cloud != null) {
            throw new RuntimeException();
        }
        Debug.INSTANCE.println(Integer.MAX_VALUE, "set Cloud to ", cloud);
        this.cloud = cloud;
        initInformation();
    }

    public DependencyGraph getGraph() {
        return graph;
    }

    public void setGraph(DependencyGraph graph) {
        if (this.graph != null) {
            throw new RuntimeException();
        }
        Debug.INSTANCE.println(Integer.MAX_VALUE, "set Graph to ", graph);
        this.graph = graph;
    }

    public void setHomogenRuntimeProfilePerId(HomogenRuntimeProfilePerId id) {
        if (this.id != null || this.type != null) {
            throw new RuntimeException();
        }
        Debug.INSTANCE.println(Integer.MAX_VALUE, "set homogen runtime profiles per Id to ", id);
        this.id = id;
        initInformation();
    }

    public void setHomogenRuntimeProfilePerType(HomogenRuntimeProfilePerType type) {
        if (this.id != null || this.type != null) {
            throw new RuntimeException();
        }
        Debug.INSTANCE.println(Integer.MAX_VALUE, "set homogen runtime profiles per type to ", type);
        this.type = type;
        initInformation();
    }

    public SchedulingInformation getInformation() {
        return information;
    }

    private void setInformation(SchedulingInformation information) {
        Debug.INSTANCE.println(Integer.MAX_VALUE, "set scheduling information to ", information);
        this.information = information;
    }

    public Cloud readCloud(String path) {
        Cloud cloud = CloudFactory.createFromFile(new File(path));
        setCloud(cloud);
        return cloud;
    }

    public DependencyGraph readGraph(String path) {
        DependencyGraph graph = DependencyGraphFactory.create(new File(path));
        setGraph(graph);
        return graph;
    }

    public HomogenRuntimeProfilePerId readHomogenRuntimeProfilePerIdFromDAX_Use_Concrete(String path) {
        HomogenRuntimeProfilePerId id = HomogenRuntimeProfilesFactory.readHomogenRuntimeProfilesFromDaxPerId(new File(path));
        setHomogenRuntimeProfilePerId(id);
        return id;
    }

    public HomogenRuntimeProfilePerId readHomogenRuntimeProfilePerIdFromDAX_Use_Concrete(String path, DaxScaler scaler) {
        HomogenRuntimeProfilePerId id = HomogenRuntimeProfilesFactory.readHomogenRuntimeProfilesFromDaxPerId(new File(path), scaler);
        setHomogenRuntimeProfilePerId(id);
        return id;
    }

    public HomogenRuntimeProfilePerType readHomogenRuntimeProfilePerTypeFromDAX_Use_Means(String path) {
        HomogenRuntimeProfilePerType type = HomogenRuntimeProfilesFactory.readHomogenRuntimeProfilesFromDaxPerType_Use_Means(new File(path));
        setHomogenRuntimeProfilePerType(type);
        return type;
    }

    public HomogenRuntimeProfilePerType readHomogenRuntimeProfilePerTypeFromRuntimeProfiles(String path) {
        HomogenRuntimeProfilePerType type = HomogenRuntimeProfilesFactory.readHomogenRuntimeProfilesFromExecutionProfilePerType(new File(path));
        setHomogenRuntimeProfilePerType(type);
        return type;
    }

    public HomogenRuntimeProfilePerType readHomogenRuntimeProfilePerTypeFromRuntimeProfiles(String path, ProfileScaler scaler) {
        HomogenRuntimeProfilePerType type = HomogenRuntimeProfilesFactory.readHomogenRuntimeProfilesFromExecutionProfilePerType(new File(path), scaler);
        setHomogenRuntimeProfilePerType(type);
        return type;
    }

    private void initInformation() {
        if (cloud != null) {
            if (type != null) {
                setInformation(new SchedulingInformationImplPerType(cloud, type));
            } else if (id != null) {
                setInformation(new SchedulingInformationImplPerId(cloud, id));
            }
        }
    }

    public WorkflowTemplate create() {
        if (graph == null || information == null) {
            throw new RuntimeException();
        }
        WorkflowTemplate template = new WorkflowTemplateImpl(graph, information);
        Debug.INSTANCE.println(Integer.MAX_VALUE, "create template ", template);
        return template;

    }

    @Override
    public String toString() {
        String s1 = cloud == null ? "cloud missing" : cloud.toString();
        String s2 = graph == null ? "graph missing" : graph.toString();
        String s3 = information == null ? "information missing" : information.toString();
        String result = String.format("%s%n%s%n%s", s1, s2, s3);
        return result;
    }

}
