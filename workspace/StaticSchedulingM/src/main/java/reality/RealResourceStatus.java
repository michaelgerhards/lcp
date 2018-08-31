package reality;

public enum RealResourceStatus {


	
	OFFLINE, // resource is not started 
	RUNNING,  // resource executes tasks
	IDLING, // resource executes no task, terminated not called, resource had executed task in past
	TERMINATED; // resource is shut down

	
}
