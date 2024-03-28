package com.XMLtracer.Interface;

import org.camunda.bpm.model.bpmn.instance.Event;

public interface IEventDetailsFactory {
    IEventDetailsStrategy createStrategy(Event event);

}
