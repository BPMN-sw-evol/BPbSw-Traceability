package com.xmltracer.Interface;

import com.google.gson.JsonObject;
import org.camunda.bpm.model.bpmn.instance.Event;

public interface IEventDetailsStrategy {
    JsonObject getEventDetails(Event event);

}
