package com.Traza.LoadTraza;

import com.DataBase.Processor.DataProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import javax.swing.JOptionPane;
import java.io.IOException;


public class DataLoad {

    public boolean dataProcessor() {

        BpmnProcessor bpmnProcessor = new BpmnProcessor();
        ProjectProcessor projectProcessor = new ProjectProcessor();

        String bpmnFilePath = bpmnProcessor.selectBpmnFile();
        String[] projectPaths = projectProcessor.getProjectPathsFromUserInput();

        if (bpmnFilePath == null || projectPaths == null) {
            // El usuario canceló la selección del archivo BPMN o de proyectos
            return false;
        }

        JsonObject successBpmn = bpmnProcessor.processBpmnModel(bpmnFilePath);

        ObjectNode successProject = projectProcessor.processProject("MSG-Foundation", projectPaths);

        if (!successBpmn.isEmpty() && !successProject.isEmpty()) {
            try {

                new DataProcessor(new JSONObject(new ObjectMapper().writeValueAsString(successProject)), new JSONObject(successBpmn.toString()), "MSG-Foundation");

                // Si llegamos aquí, significa que no hubo excepciones durante el proceso
                String successMessage = "La información se ha guardado con éxito.";
                JOptionPane.showMessageDialog(null, successMessage, "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                // Si hay una excepción, se captura aquí y se muestra un mensaje de error
                String errorMessage = "Error al guardar la información: " + e.getMessage();
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }

            return true;
        } else {
            String errorMessage = "Hubo un problema al procesar y guardar la información de BPMN y/o proyectos.";
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        return false;
    }







}
