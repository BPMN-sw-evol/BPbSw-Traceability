package com.BPMNTracer.Event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.BPMNTracer.Interface.IEventDetailsStrategy;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;

public class StartEventDetailsStrategy implements IEventDetailsStrategy {

    private StartEvent startEvent;

    @Override
    public JsonObject getEventDetails(Event event) {
        if (!(event instanceof StartEvent)) {
            throw new IllegalArgumentException("Event must be an instance of StartEvent");
        }


        startEvent = (StartEvent) event;

        JsonObject eventDetails = new JsonObject();
        eventDetails.addProperty("taskID", startEvent.getId());
        eventDetails.addProperty("taskName", startEvent.getName());
        eventDetails.addProperty("taskType", "Start Event");

        String userTaskLink = determineStartEventImplementation(startEvent);
        addTaskImplementationDetails(eventDetails, userTaskLink);

        if ("Generated Task Form".equals(userTaskLink)) {
            eventDetails.add("Form Fields", getFormFields(startEvent));
        }

        return eventDetails;
    }

    private String determineStartEventImplementation(StartEvent startEvent) {
        return startEvent.getCamundaFormKey() != null ? "Embedded or External Task Form"
                : startEvent.getCamundaFormRef() != null ? "Camunda Form"
                : hasGeneratedTaskForm(startEvent) ? "Generated Task Form"
                : "None";
    }

    private void addTaskImplementationDetails(JsonObject jsonObject, String taskType) {
        jsonObject.addProperty("taskImplementationType", taskType);
        String implementation = getStartEventLinkValue(taskType);
        if (implementation != null) {
            jsonObject.addProperty("taskReferenceOrImplementation", implementation);
        }
    }

    private String getStartEventLinkValue(String startEventLink) {
        if ("Embedded or External Task Form".equals(startEventLink)) {
            return startEvent.getCamundaFormKey();
        } else if ("Camunda Form".equals(startEventLink)) {
            return startEvent.getCamundaFormRef();
        } else if ("Generated Task Form".equals(startEventLink)) {
            return "Have a Fields Form";
        } else if ("None".equals(startEventLink)) {
            return "None";
        }
        return null;
    }

    private boolean hasGeneratedTaskForm(StartEvent startEvent) {
        ExtensionElements extensionElements = startEvent.getExtensionElements();
        return extensionElements != null && extensionElements.getElementsQuery()
                .filterByType(CamundaFormData.class).count() > 0;
    }

    private JsonArray getFormFields(StartEvent startEvent) {
        JsonArray formFields = new JsonArray();
        CamundaFormData formData = startEvent.getExtensionElements().getElementsQuery()
                .filterByType(CamundaFormData.class).singleResult();
        if (formData != null) {
            for (CamundaFormField field : formData.getCamundaFormFields()) {
                formFields.add(field.getCamundaId());
            }
        }
        return formFields;
    }
}
