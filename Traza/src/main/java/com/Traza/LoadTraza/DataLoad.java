package com.Traza.LoadTraza;

import com.DataBase.Processor.DataProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Timestamp;


public class DataLoad {

    private final DataProcessor dataProcessor = new DataProcessor();
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
            this.insertData(successBpmn, successProject);
            return true;

        } else {
            String errorMessage = "Hubo un problema al procesar y guardar la información de BPMN y/o proyectos.";
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        return false;
    }

    private void insertData(JsonObject successBpmn, ObjectNode successProject){
        // Mostrar el cuadro de diálogo
        final JDialog dialog = new JDialog();

        new Thread(() -> {
            try {
                dataProcessor.executeAllExtractors(new JSONObject(new ObjectMapper().writeValueAsString(successProject)), new JSONObject(successBpmn.toString()), "MSG-Foundation");
                dialog.dispose();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).start();

        final JOptionPane optionPane = new JOptionPane("Cargando.....", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);

        dialog.setTitle("Cargando");
        dialog.setContentPane(optionPane);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);


        // Si llegamos aquí, significa que no hubo excepciones durante el proceso
        String successMessage = "La información se ha guardado con éxito.";
        JOptionPane.showMessageDialog(null, successMessage, "Éxito", JOptionPane.INFORMATION_MESSAGE);

    }

    public void deleteData(Timestamp data){
        dataProcessor.executeAllDeletes(data);
    }



}
