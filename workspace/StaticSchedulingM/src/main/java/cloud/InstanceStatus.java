package cloud;

public enum InstanceStatus {

	
	OFFLINE, // instance is not started, first task is not ready (parents are still running) 
	READY_TO_START, // instance not started, first task is ready
	RUNNING,  // instance executes tasks
	IDLING, // instance completed execution, next task is not ready (parents are still running) -> terminated
	READY,  // instance completed execution, next task is ready -> running
	TERMINATED; // instance is shut down
}
