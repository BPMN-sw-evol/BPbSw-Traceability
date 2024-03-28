package com.XMLtracer.Flow;

import com.google.gson.JsonObject;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.HashSet;
import java.util.Set;

public class FlowSequence {
    private static final JsonObject sequenceFlowDetails = new JsonObject();

    public static JsonObject processSequenceFlow(SequenceFlow sequenceFlow) {
        Set<String> sequenceFlowIdsProcesados = new HashSet<>();

        if (!sequenceFlowIdsProcesados.contains(sequenceFlow.getId())) {

            sequenceFlowIdsProcesados.add(sequenceFlow.getId());

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

        return null;
    }
}
