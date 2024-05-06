package com.BPMNTracer.Event;

import com.google.gson.JsonObject;
import com.BPMNTracer.Interface.IEventDetailsStrategy;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Event;

public class EndEventDetailsStrategy implements IEventDetailsStrategy {
    private EndEvent endEvent;

    @Override
    public JsonObject getEventDetails(Event event) {
        if (!(event instanceof EndEvent)) {
            throw new IllegalArgumentException("Event must be an instance of StartEvent");
        }

        endEvent = (EndEvent) event;

        JsonObject eventDetails = new JsonObject();
        eventDetails.addProperty("taskID", endEvent.getId());
        eventDetails.addProperty("taskName", endEvent.getName());
        eventDetails.addProperty("taskType", "End Event");
        eventDetails.addProperty("taskImplementationType", "None");
        eventDetails.addProperty("taskReferenceOrImplementation", "None");

        return eventDetails;
    }
}
