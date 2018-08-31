package reality;

import java.util.List;
import java.util.SortedMap;

import cloud.Instance;

public interface RealResourceManager {

	
	void deactivateResource(String name);
	
	void activateResource(Instance resource);

	void goRunning(String name);

	void goIdling(String name);

	void goReady(String name);

	void goTerminate(String name, double time);

	List<RealResource> getInstanceReady();

	List<RealResource> getInstanceIdling();

	SortedMap<String, RealResource> getInstanceAll();

	List<RealResource> getInstanceRunning();

	List<RealResource> getInstanceCompleted();

	List<RealResource> getInstanceReadyToBoot();

	
}
