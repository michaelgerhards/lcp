package dynamic.reality;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import cloud.Instance;
import reality.RealResource;
import reality.RealResourceManager;
import statics.initialization.WorkflowInstance;

public class RealResourceManagerImpl implements RealResourceManager {

	private final SortedMap<String, RealResourceImpl> instanceAll = new TreeMap<String, RealResourceImpl>();

	// TODO replace with TreeSEt (sorted set?)
	private final List<RealResourceImpl> instanceReadyToBoot = new LinkedList<RealResourceImpl>();
	private final List<RealResourceImpl> instanceIdling = new LinkedList<RealResourceImpl>();
	private final List<RealResourceImpl> instanceReady = new LinkedList<RealResourceImpl>();
	private final List<RealResourceImpl> instanceRunning = new LinkedList<RealResourceImpl>();
	private final List<RealResourceImpl> instanceCompleted = new LinkedList<RealResourceImpl>();
	private final SortedMap<String, RealResourceImpl>instanceAllView = Collections
			.unmodifiableSortedMap(instanceAll);
	private final List<RealResourceImpl> instanceReadyView = Collections
			.unmodifiableList(instanceReady);
	private final List<RealResourceImpl> instanceIdlingView = Collections
			.unmodifiableList(instanceIdling);
	private final List<RealResourceImpl> instanceRunningView = Collections
			.unmodifiableList(instanceRunning);
	private final List<RealResourceImpl> instanceCompletedView = Collections
			.unmodifiableList(instanceCompleted);
	private final List<RealResourceImpl> instanceReadyToBootView = Collections
			.unmodifiableList(instanceReadyToBoot);

	private final WorkflowInstance workflow;

	
	
	public RealResourceManagerImpl(WorkflowInstance workflow) {
		this.workflow = workflow;

	}

	@Override
	public void deactivateResource(String name) {
		RealResourceImpl resource = instanceAll.get(name);
		boolean exist2 = instanceReadyToBoot.remove(resource);
		instanceAll.remove(name);
		if (!exist2) {
			throw new RuntimeException(name);
		}
	}
	
	@Override
	public void activateResource(Instance resource) {
		RealResourceImpl used = new RealResourceImpl(resource,workflow);
		instanceAll.put(used.getName(), used);
		instanceReadyToBoot.add(used);
	}

	@Override
	public void goRunning(String name) {
		RealResourceImpl resource = instanceAll.get(name);
		boolean exist1 = instanceReady.remove(resource);
		boolean exist2 = instanceReadyToBoot.remove(resource);
		if (!exist1 && !exist2) {
			throw new RuntimeException(name);
		}
		instanceRunning.add(resource);
	}

	@Override
	public void goIdling(String name) {
		RealResourceImpl resource = instanceAll.get(name);
		boolean exist = instanceRunning.remove(resource);
		if (!exist) {
			throw new RuntimeException(name);
		}
		instanceIdling.add(resource);
	}

	@Override
	public void goReady(String name) {
		RealResourceImpl resource = instanceAll.get(name);
		boolean exist = instanceIdling.remove(resource);
		if (!exist) {
			throw new RuntimeException(name);
		}
		instanceReady.add(resource);
	}

	@Override
	public void goTerminate(String name, double time) {
		RealResourceImpl resource = instanceAll.get(name);
		boolean exist = instanceIdling.remove(resource);
		if (!exist) {
			throw new RuntimeException(name);
		}
		instanceCompleted.add(resource);
		resource.terminate(time);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<RealResource> getInstanceReady() {
		return (List)instanceReadyView;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<RealResource> getInstanceIdling() {
		return (List)instanceIdlingView;
	}

	@Override
	public SortedMap<String, RealResource> getInstanceAll() {
		return (SortedMap)instanceAllView;
	}

	@Override
	public List<RealResource> getInstanceRunning() {
		return (List)instanceRunningView;
	}

	@Override
	public List<RealResource> getInstanceCompleted() {
		return (List)instanceCompletedView;
	}

	@Override
	public List<RealResource> getInstanceReadyToBoot() {
		return (List)instanceReadyToBootView;
	}
}
