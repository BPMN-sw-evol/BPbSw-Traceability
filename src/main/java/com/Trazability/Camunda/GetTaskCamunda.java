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
import java.util.Set;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;

public class GetTaskCamunda {

    public static void listActivitiesFromStartEvent(BpmnModelInstance modelInstance, FlowNode currentNode,
            Set<String> visitedNodes, JsonObject bpmnDetails) {
        if (visitedNodes.contains(currentNode.getId())) {
            return;
        }

        visitedNodes.add(currentNode.getId());
        if (currentNode.getName() != null && !currentNode.getName().isEmpty()) {
            printElementDetails(currentNode, bpmnDetails);
        }
        Collection<SequenceFlow> outgoingFlows = currentNode.getOutgoing();

        for (SequenceFlow sequenceFlow : outgoingFlows) {
            FlowNode targetNode = sequenceFlow.getTarget();

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

    public static void printElementDetails(FlowNode flowNode, JsonObject bpmnDetails) {
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

        // Obtener el array JSON existente o crear uno nuevo si no existe
        JsonArray traceArray = bpmnDetails.has("trace") ? bpmnDetails.getAsJsonArray("trace") : new JsonArray();

        // Agregar los detalles de la tarea al array
        traceArray.add(taskDetails);

        // Colocar el array de traza actualizado en bpmnDetails
        bpmnDetails.add("trace", traceArray);
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
