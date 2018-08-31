package dynamic.scheduling.engine.eventhandler;

import reality.EventHandler;
import reality.QueueEvent;
import dynamic.reality.QueueEventImpl;
import reality.Time;
import statics.util.Debug;
import static dynamic.scheduling.engine.WorkflowEngineImpl.*;

public class WorkflowCompletedHandler implements EventHandler {

	@Override
	public void handleEvent(QueueEvent evt) {
		Time time = Time.getInstance();
		Debug.INSTANCE.println(DEBUG_COMPLETE, "Queued but unprocessed events start");
		while (time.hasFurtherEvents()) {
			QueueEvent pollNextEvent = time.pollNextEvent();
//			String s = pollNextEvent.toString();
			Debug.INSTANCE.println(DEBUG_COMPLETE, pollNextEvent);
			// TODO check events!
		}
		Debug.INSTANCE.println(DEBUG_COMPLETE, "Queued but unprocessed events end");
	}

}
