package com.xmltracer.Event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xmltracer.Event.IntermediateEvents.MessageIntermediateEvent;
import com.xmltracer.Interface.IEventDetailsStrategy;
import org.camunda.bpm.model.bpmn.instance.*;
import com.xmltracer.Event.IntermediateEvents.TimerIntermediateEvent;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputOutput;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputParameter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaOutputParameter;

import java.util.Collection;

public class IntermediateCatchEventDetailsStrategy implements IEventDetailsStrategy {

    @Override
    public JsonObject getEventDetails(Event event) {
        if (!(event instanceof IntermediateCatchEvent intermediateEvent)) {
            throw new IllegalArgumentException("Event must be an instance of IntermediateCatchEvent");
        }

        EventDefinition eventDefinition = intermediateEvent.getEventDefinitions().iterator().next();

        JsonObject eventDetails = new JsonObject();
        eventDetails.addProperty("taskID", intermediateEvent.getId());
        eventDetails.addProperty("taskName", intermediateEvent.getName());

        if (eventDefinition instanceof TimerEventDefinition) {
            getTimerIntermediateEventDetails((TimerEventDefinition) eventDefinition, eventDetails);
        } else if (eventDefinition instanceof MessageEventDefinition) {
            getMessageIntermediateEventDetails((MessageEventDefinition) eventDefinition, eventDetails);
        }

        addEventInputsAndOutputsAsVariables(eventDetails, intermediateEvent);

        return eventDetails;
    }

    private void getTimerIntermediateEventDetails(TimerEventDefinition timerEventDefinition, JsonObject eventDetails) {

        // Aquí creas una instancia de TimerIntermediateEvent y obtienes los detalles del evento
        TimerIntermediateEvent timerIntermediateEvent = new TimerIntermediateEvent(timerEventDefinition);

        // Agregar los detalles específicos para Timer Event
        eventDetails.addProperty("taskType", timerIntermediateEvent.getTaskType());
        eventDetails.addProperty("taskImplementationType", timerIntermediateEvent.getTaskImplementationType());
        eventDetails.addProperty("taskReferenceOrImplementation", timerIntermediateEvent.getTaskReferenceOrImplementation());
    }

    private void getMessageIntermediateEventDetails(MessageEventDefinition messageEventDefinition, JsonObject eventDetails) {

        // Aquí creas una instancia de MessageIntermediateEvent y obtienes los detalles del evento
        MessageIntermediateEvent messageIntermediateEvent = new MessageIntermediateEvent(messageEventDefinition);

        // Agregar los detalles específicos para Message Event
        eventDetails.addProperty("taskType", messageIntermediateEvent.getTaskType());
        eventDetails.addProperty("taskImplementationType", messageIntermediateEvent.getTaskImplementationType());
        eventDetails.addProperty("taskReferenceOrImplementation", messageIntermediateEvent.getTaskReferenceOrImplementation());
    }

    private void addEventInputsAndOutputsAsVariables(JsonObject eventDetails, IntermediateCatchEvent intermediateEvent) {
            ExtensionElements extensionElements = intermediateEvent.getExtensionElements();
            if (extensionElements != null) {
                Collection<CamundaInputOutput> inputOutputs = extensionElements
                        .getChildElementsByType(CamundaInputOutput.class);

                if (!inputOutputs.isEmpty()) {
                    JsonArray variablesArray = new JsonArray();
                    for (CamundaInputOutput inputOutput : inputOutputs) {
                        for (CamundaInputParameter inputParameter : inputOutput.getCamundaInputParameters()) {
                            variablesArray.add(inputParameter.getCamundaName());
                        }
                        for (CamundaOutputParameter outputParameter : inputOutput.getCamundaOutputParameters()) {
                            variablesArray.add(outputParameter.getCamundaName());
                        }
                    }
                    eventDetails.add("variables", variablesArray);

                }
            }
    }
}
