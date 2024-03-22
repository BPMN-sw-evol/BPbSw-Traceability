package com.msgfoundation.xmltracer;

import com.google.gson.JsonObject;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class FlowSequenceDetails {

    public static JsonObject processSequenceFlow(SequenceFlow sequenceFlow) {
        JsonObject sequenceFlowDetails = new JsonObject();

        sequenceFlowDetails.addProperty("taskID", sequenceFlow.getId());
        sequenceFlowDetails.addProperty("taskName", sequenceFlow.getId());
        sequenceFlowDetails.addProperty("taskType", "Sequence Flow");

        if (sequenceFlow.getConditionExpression().getTextContent().isEmpty()) {
            sequenceFlowDetails.addProperty("taskImplementationType", "Script");
            sequenceFlowDetails.addProperty("taskReferenceOrImplementation",
                    sequenceFlow.getConditionExpression().getCamundaResource());
        } else {
            sequenceFlowDetails.addProperty("taskImplementationType", "Expression");
            sequenceFlowDetails.addProperty("variables",
                    sequenceFlow.getConditionExpression().getTextContent().split("\\{")[1].split("\\=")[0]
                            .trim());
        }

        return sequenceFlowDetails;
    }
}