package statics.initialization.impl;

import java.io.File;

import javax.xml.bind.JAXB;

import executionprofile.generated.Adag;
import executionprofile.generated.Adag.Child;
import executionprofile.generated.Adag.Child.Parent;
import executionprofile.generated.Adag.Job;
import statics.initialization.DependencyGraph;
import static statics.initialization.WorkflowInstance.*;

public class DependencyGraphFactory {

    public static DependencyGraph create(File file) {
        Adag adag = JAXB.unmarshal(file, Adag.class);
        DependencyGraphImpl graph = new DependencyGraphImpl(file.getName());

        for (Job job : adag.getJob()) {
            graph.createTask(jobIDToInt(job.getId()), jobNameToInt(job.getName()));
        }

        for (Child child : adag.getChild()) {
            String childId = child.getRef();
            for (Parent parent : child.getParent()) {
                String parentId = parent.getRef();
                graph.createDependency(jobIDToInt(parentId), jobIDToInt(childId));

            }
        }

        graph.createArtificialTasks();
        graph.cap();
        graph.calcAnchestorsAndDescendants();
        graph.doFinalize();
        return graph;
    }

}
