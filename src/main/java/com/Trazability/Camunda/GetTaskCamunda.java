package com.Trazability.Camunda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;

public class GetTaskCamunda {

    private static JsonArray sequenceDetailsArray = new JsonArray();

    public static void listActivitiesFromStartEvent(BpmnModelInstance modelInstance, FlowNode currentNode,
            Set<String> visitedNodes, JsonObject bpmnDetails) {

// Obtener el array JSON existente o crear uno nuevo si no existe
        JsonArray traceArray = bpmnDetails.has("trace") ? bpmnDetails.getAsJsonArray("trace") : new JsonArray();

        if (visitedNodes.contains(currentNode.getId())) {
            return;
        }

        visitedNodes.add(currentNode.getId());
        if (currentNode.getName() != null && !currentNode.getName().isEmpty()) {
            printElementDetails(currentNode, bpmnDetails, traceArray);
        }

        Collection<SequenceFlow> outgoingFlows = currentNode.getOutgoing();
        // Antes de comenzar a procesar los SequenceFlows, crea un conjunto para
        // realizar un seguimiento de los IDs ya procesados
        Set<String> sequenceFlowIdsProcesados = new HashSet<>();

        for (SequenceFlow sequenceFlow : outgoingFlows) {
            FlowNode targetNode = sequenceFlow.getTarget();

            if (sequenceFlow.getConditionExpression() != null
                    && !sequenceFlowIdsProcesados.contains(sequenceFlow.getId())) {
                // Agrega el ID a la lista de procesados
                sequenceFlowIdsProcesados.add(sequenceFlow.getId());

                // Crea un JsonObject para almacenar los detalles de este SequenceFlow
                JsonObject sequenceFlowDetails = new JsonObject();

                sequenceFlowDetails.addProperty("taskID", sequenceFlow.getId());
                sequenceFlowDetails.addProperty("taskName", sequenceFlow.getId());
                sequenceFlowDetails.addProperty("taskType", "Sequence Flow");

                if (sequenceFlow.getConditionExpression().getTextContent().isEmpty()) {
                    sequenceFlowDetails.addProperty("taskImplementationType",
                            "Script");
//                    sequenceFlowDetails.addProperty("sequenceFlowFormat",
//                            sequenceFlow.getConditionExpression().getLanguage());
                    sequenceFlowDetails.addProperty("taskReferenceOrImplementation",
                            sequenceFlow.getConditionExpression().getCamundaResource());
                } else {
                    sequenceFlowDetails.addProperty("taskImplementationType",
                            "Expression");
                    sequenceFlowDetails.addProperty("variables",
                            sequenceFlow.getConditionExpression().getTextContent().split("\\{")[1].split("\\=")[0].trim());
                }
                // Agrega el JsonObject a la colección de detalles de SequenceFlows
                if (!traceArray.contains(sequenceFlowDetails)) {
                    traceArray.remove(sequenceFlowDetails);
                    traceArray.add(sequenceFlowDetails);
                }
            }

            // Imprimir la información de la compuerta
            // if (currentNode instanceof Gateway) {
            // System.out.println("Flujo desde la compuerta: " + currentNode.getName());
            // }
            // Si la actividad actual no ha sido visitada, seguir recursivamente
            if (!visitedNodes.contains(targetNode.getId())) {
                listActivitiesFromStartEvent(modelInstance, targetNode, visitedNodes, bpmnDetails);
            }
        }
    }

    public static void printElementDetails(FlowNode flowNode, JsonObject bpmnDetails, JsonArray traceArray) {
        JsonObject taskDetails = new JsonObject();

        if (flowNode instanceof StartEvent) {
            StartEvent startEvent = (StartEvent) flowNode;
            taskDetails = EventTaskDetails.getStartEventDetails(startEvent);
        } else if (flowNode instanceof UserTask) {
            UserTask userTask = (UserTask) flowNode;
            taskDetails = UserTaskDetails.getUserTaskDetails(userTask);
        } else if (flowNode instanceof Gateway && flowNode.getName() != null && !flowNode.getName().isEmpty()) {
            taskDetails.addProperty("taskID", flowNode.getId());
            taskDetails.addProperty("taskName", flowNode.getName());
            taskDetails.addProperty("taskType", "Gateway");
            taskDetails.addProperty("taskImplementationType", "None");
            taskDetails.addProperty("taskReferenceOrImplementation", "None");
        } else if (flowNode instanceof ServiceTask) {
            ServiceTask serviceTask = (ServiceTask) flowNode;
            taskDetails = ServiceTaskDetails.getServiceTaskDetails(serviceTask);
        } else if (flowNode instanceof SendTask) {
            SendTask sendTask = (SendTask) flowNode;
            taskDetails = SendTaskDetails.getSendTaskDetails(sendTask);
        } else if (flowNode instanceof EndEvent) {
            taskDetails.addProperty("taskID", flowNode.getId());
            taskDetails.addProperty("taskName", flowNode.getName());
            taskDetails.addProperty("taskType", "End Event");
            taskDetails.addProperty("taskImplementationType", "None");
            taskDetails.addProperty("taskReferenceOrImplementation", "None");
        }

        // Agregar los detalles de la tarea al array
        traceArray.add(taskDetails);

        // Colocar el array de traza actualizado en bpmnDetails
        bpmnDetails.add("trace", traceArray);
    }

    public static String getParticipant(BpmnModelInstance modelInstance) {
        String processName = "";
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);
        for (Participant participant : participants) {
            processName = participant.getName();
        }
        return processName;
    }

    public static String formatJson(String jsonString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(gson.fromJson(jsonString, Object.class));
    }

    public static void saveJsonToFile(String fileName, String json) {
        try {
            // Obtener la ruta del directorio actual del proyecto
            String currentDirectory = System.getProperty("user.dir");

            // Crear el directorio "output" si no existe
            String outputDirectory = currentDirectory + File.separator + "output";
            Path outputPath = Paths.get(outputDirectory);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
                System.out.println("Directorio 'output' creado en: " + outputPath);
            }

            // Crear un FileWriter en el directorio "output" con el nombre del archivo
            // original y extensión .json
            String filePath = outputDirectory + File.separator + fileName + ".json";
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                fileWriter.write(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
