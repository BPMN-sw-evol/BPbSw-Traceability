package com.XMLtracer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.XMLtracer.Factory.EventDetailsFactory;
import com.XMLtracer.Factory.TaskDetailsFactory;
import com.XMLtracer.Flow.FlowSequence;
import com.XMLtracer.Interface.IEventDetailsStrategy;
import com.XMLtracer.Interface.ITaskDetailsStrategy;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;
import java.util.Set;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CamundaElement {

    public static void listActivitiesFromStartEvent(FlowNode currentNode,
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

        for (SequenceFlow sequenceFlow : outgoingFlows) {
            FlowNode targetNode = sequenceFlow.getTarget();

            // Si la actividad actual no ha sido visitada, seguir recursivamente
            if (!visitedNodes.contains(targetNode.getId())) {
                listActivitiesFromStartEvent(targetNode, visitedNodes, bpmnDetails);
            }
        }
    }

    public static void printElementDetails(FlowNode flowNode, JsonObject bpmnDetails, JsonArray traceArray) {
        JsonObject elementDetails = new JsonObject();

        if (flowNode instanceof Activity activity) {
            ITaskDetailsStrategy taskDetailsStrategy = TaskDetailsFactory.getInstance().createStrategy(activity);
            elementDetails = taskDetailsStrategy.getTaskDetails(activity);
        } else
        if (flowNode instanceof Event event) {
            IEventDetailsStrategy eventDetailsStrategy = EventDetailsFactory.getInstance().createStrategy(event);
            elementDetails = eventDetailsStrategy.getEventDetails((Event) flowNode);
        } else if (flowNode instanceof SequenceFlow) {
            if(((SequenceFlow) flowNode).getConditionExpression() != null){
                JsonObject sequenceFlowDetails = FlowSequence.processSequenceFlow((SequenceFlow) flowNode);
                if (!traceArray.contains(sequenceFlowDetails)) {
                    traceArray.add(sequenceFlowDetails);
                }
            }
            return; // No es necesario continuar procesando las secuencias de flujo
        }

        // Agregar los detalles del elemento al array
        if (elementDetails != null && !elementDetails.isJsonNull() && !elementDetails.entrySet().isEmpty()) {
            traceArray.add(elementDetails);
        }

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
            String outputDirectory = currentDirectory + File.separator + "BPMN-tracer/JSONResult";
            Path outputPath = Paths.get(outputDirectory);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
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
