package com.XMLtracer.Event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.XMLtracer.Interface.IEventDetailsStrategy;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;

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
