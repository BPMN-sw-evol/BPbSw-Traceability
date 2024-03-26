package com.xmltracer.Event.IntermediateEvents;

import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;

public class MessageIntermediateEvent {
    private MessageEventDefinition messageEvent;

    public MessageIntermediateEvent(MessageEventDefinition messageEvent) {
        this.messageEvent = messageEvent;
    }

    public String getTaskType() {
        return "Message Intermediate Event";
    }

    public String getTaskImplementationType() {
        return messageEvent.getMessage().getName();
    }

    public String getTaskReferenceOrImplementation() {
        return messageEvent.getMessage().getName();
    }
}
