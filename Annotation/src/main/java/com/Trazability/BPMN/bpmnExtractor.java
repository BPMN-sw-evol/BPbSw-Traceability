package com.Trazability.BPMN;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputOutput;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputParameter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaOutputParameter;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.Trazability.BPMN.Formats;

public class bpmnExtractor {

    private static BpmnModelInstance modelInstance = null;

    public static JsonObject bpmnProcessor() {
        JsonObject bpmnElements = new JsonObject();
        String bpmnFilePath = getFileFromUser();

        if (bpmnFilePath.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Operación cancelada por el usuario, modelo BPMN no analizado.",
                    "Error", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        modelInstance = Bpmn.readModelFromFile(new File(bpmnFilePath));

        Collection<StartEvent> startEvents = modelInstance.getModelElementsByType(StartEvent.class);
        for (StartEvent startEvent : startEvents) {
            Set<String> visitedNodes = new HashSet<>();
            elementInfo(startEvent, visitedNodes, bpmnElements);
        }

        return bpmnElements;
    }

    private static String getFileFromUser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos BPMN", "bpmn"));
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }

        return "";
    }

    private static void elementInfo(FlowNode currentNode, Set<String> visitedNodes,
                                    JsonObject bpmnElements) {

        JsonObject bpmnDetail = new JsonObject();

        if (visitedNodes.contains(currentNode.getId())) {
            return;
        }

        visitedNodes.add(currentNode.getId());

        if (currentNode.getName() != null && !currentNode.getName().isEmpty()) {
            // Aquí agregamos la información del lane si el elemento está dentro de uno
            Lane lane = findLaneOfFlowNode(currentNode);
            
            String concatenatedName = Formats.formatLaneName(lane.getName())+ "_" + Formats.formatElementName(currentNode.getName());
            bpmnDetail.addProperty("name", currentNode.getName());
            bpmnDetail.addProperty("type", currentNode.getElementType().getTypeName());
            if(currentNode.getExtensionElements() != null && currentNode.getExtensionElements().getElementsQuery()
            .filterByType(CamundaFormData.class).count() > 0){
                addFormFields(bpmnDetail, currentNode);
            }else{
                addTaskInputsAndOutputs(bpmnDetail, currentNode);
            }
            bpmnElements.add(concatenatedName, bpmnDetail);
        }

        Collection<SequenceFlow> outgoingFlows = currentNode.getOutgoing();
        for (SequenceFlow sequenceFlow : outgoingFlows) {
            FlowNode targetNode = sequenceFlow.getTarget();
            if (!visitedNodes.contains(targetNode.getId())) {
                elementInfo(targetNode, visitedNodes, bpmnElements);
            }
        }
    }


    private static Lane findLaneOfFlowNode(FlowNode flowNode) {
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);
        for (Lane lane : lanes) {
            for (FlowNode laneFlowNode : lane.getFlowNodeRefs()) {
                if (laneFlowNode.getId().equals(flowNode.getId())) {
                    return lane;
                }
            }
        }
        return null;
    }

    private static void addTaskInputsAndOutputs(JsonObject jsonObject, FlowNode element) {
        ExtensionElements extensionElements = element.getExtensionElements();
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
                jsonObject.add("variables", variablesArray);

            }
        }
    }

    private static void addFormFields(JsonObject jsonObject, FlowNode element) {
        CamundaFormData formData = element.getExtensionElements().getElementsQuery()
                .filterByType(CamundaFormData.class).singleResult();
        JsonArray formFieldsArray = new JsonArray();
        if (formData != null) {
            formData.getCamundaFormFields().forEach(field -> formFieldsArray.add(field.getCamundaId()));
        }
        jsonObject.add("variables", formFieldsArray);
    }

}
