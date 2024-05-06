package com.Traza.LoadTraza;

import com.google.gson.JsonObject;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.BPMNTracer.CamundaElement.*;
import static com.BPMNTracer.CamundaElement.formatJson;

public class BpmnProcessor {

    public String selectBpmnFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos BPMN", "bpmn"));

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada por el usuario, modelo BPMN no analizado.", "Error", JOptionPane.INFORMATION_MESSAGE);
            return null; // Indica que la operación fue cancelada
        }
    }

    public JsonObject processBpmnModel(String bpmnFilePath) {
        JsonObject bpmnDetails = new JsonObject();

        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(bpmnFilePath));
            File file = new File(bpmnFilePath);
            String fileNameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.'));

            Collection<StartEvent> startEvents = modelInstance.getModelElementsByType(StartEvent.class);
            for (StartEvent startEvent : startEvents) {
                Set<String> visitedNodes = new HashSet<>();
                listActivitiesFromStartEvent(startEvent, visitedNodes, bpmnDetails);
            }

            JsonObject bpmnWithBpmName = new JsonObject();
            bpmnWithBpmName.addProperty("bpmPath", bpmnFilePath);
            bpmnWithBpmName.addProperty("bpmNameFile", file.getName());
            bpmnWithBpmName.addProperty("bpmNameProcess", getParticipant(modelInstance));
            bpmnWithBpmName.add("trace", bpmnDetails.getAsJsonArray("trace"));

            // Guardar el JSON con el campo "bpmName"
            saveJsonToFile(fileNameWithoutExtension, formatJson(bpmnWithBpmName.toString()));

            return bpmnWithBpmName; // Se completó exitosamente
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al procesar el modelo BPMN.", "Error", JOptionPane.ERROR_MESSAGE);
            return null; // Hubo un problema
        }
    }
}