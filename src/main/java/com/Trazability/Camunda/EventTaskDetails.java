package com.Trazability.Camunda;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;

public class EventTaskDetails {

    public static JsonObject getStartEventDetails(StartEvent startEvent) {
        JsonObject eventDetails = new JsonObject();
        eventDetails.addProperty("taskID", startEvent.getId());
        eventDetails.addProperty("taskName", startEvent.getName());
        eventDetails.addProperty("taskType", "Start Event");

        String userTaskLink = determineStartEventImplementation(startEvent);
        addTaskImplementationDetails(eventDetails, userTaskLink, startEvent);

        if ("Generated Task Form".equals(eventDetails.get("taskImplementationType").getAsString())) {
            eventDetails.add("Form Fields", getFormFields(startEvent));
        }

        return eventDetails;
    }

    public static String determineStartEventImplementation(StartEvent startEvent) {
        return startEvent.getCamundaFormKey() != null ? "Embedded or External Task Form"
                : startEvent.getCamundaFormRef() != null ? "Camunda Form"
                : hasGeneratedTaskForm(startEvent) ? "Generated Task Form"
                : "None";
    }

     private static void addTaskImplementationDetails(JsonObject jsonObject, String taskType, StartEvent startEvent) {
        jsonObject.addProperty("taskImplementationType", taskType);
        String implementation = getStartEventLinkValue(taskType, startEvent);
        if (implementation != null) {
            jsonObject.addProperty("taskReferenceOrImplementation", implementation);
        }
    }

    private static String getStartEventLinkValue(String startEventLink, StartEvent startEvent) {
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

    private static boolean hasGeneratedTaskForm(StartEvent startEvent) {
        ExtensionElements extensionElements = startEvent.getExtensionElements();
        return extensionElements != null && extensionElements.getElementsQuery()
                .filterByType(CamundaFormData.class).count() > 0;
    }

    private static JsonArray getFormFields(StartEvent startEvent) {
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
