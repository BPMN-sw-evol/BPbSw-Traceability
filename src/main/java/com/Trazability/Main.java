package com.Trazability;

import static com.Trazability.Camunda.GetTaskCamunda.*;

import com.Trazability.DataBase.Data;
import com.Trazability.Projects.AnnotationAnalyzer;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

public class Main {

    private static String bpmnFilePath = "";

    public static String getBpmnFilePath() {
        return bpmnFilePath;
    }

    public static void main(String[] args) throws IOException {
        bpmnFilePath = selectBpmnFile();

        BpmnModelInstance modelInstance = null;
        String fileNameWithoutExtension = "";
        try {
            modelInstance = Bpmn.readModelFromFile(new File(bpmnFilePath));
            File file = new File(bpmnFilePath);
            String fileNameWithExtension = file.getName();
            fileNameWithoutExtension = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar el archivo BPMN: " + e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JsonObject bpmnDetails = new JsonObject();
        boolean successBpmn = processBpmnModel(modelInstance, bpmnDetails, fileNameWithoutExtension);

        String[] projectPaths = getProjectPathsFromUserInput();

        boolean successProject = processProjectActions("MSG-Foundation", projectPaths);

        if (successBpmn && successProject) {
            Data data = new Data("output/MSG-Foundation.json",
                    "output/MSGF-Test.json",
                    "MSG-Foundation");

            if (data.isDataInitialized()) {
                String successMessage = "La informacion se ha guardado con exito.";
                JOptionPane.showMessageDialog(null, successMessage, "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } else {
                String ErrorMessage = "Error al guardar la informacion.";
                JOptionPane.showMessageDialog(null, ErrorMessage, "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            String errorMessage = "Hubo un problema al procesar y guardar la información de BPMN y/o proyectos.";
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    private static String selectBpmnFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos BPMN", "bpmn"));

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada por el usuario.", "Error", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
            return null; // Esto nunca debería ocurrir debido a System.exit(0), solo para satisfacer el compilador
        }
    }

    private static boolean processBpmnModel(BpmnModelInstance modelInstance, JsonObject bpmnDetails,
            String fileNameWithoutExtension) {
        try {
            Collection<StartEvent> startEvents = modelInstance.getModelElementsByType(StartEvent.class);
            for (StartEvent startEvent : startEvents) {
                Set<String> visitedNodes = new HashSet<>();
                listActivitiesFromStartEvent(modelInstance, startEvent, visitedNodes, bpmnDetails);
            }

            JsonObject bpmnWithBpmName = new JsonObject();
            bpmnWithBpmName.addProperty("bpmName", fileNameWithoutExtension);
            bpmnWithBpmName.add("trace", bpmnDetails.getAsJsonArray("trace"));

            // Guardar el JSON con el campo "bpmName"
            saveJsonToFile(fileNameWithoutExtension, formatJson(bpmnWithBpmName.toString()));

            return true; // Se completó exitosamente
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Hubo un problema
        }
    }

    private static String[] getProjectPathsFromUserInput() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setMultiSelectionEnabled(true); // Permitir selección múltiple

        int result = dirChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedDirectories = dirChooser.getSelectedFiles();
            String[] projectPaths = new String[selectedDirectories.length];

            for (int i = 0; i < selectedDirectories.length; i++) {
                projectPaths[i] = selectedDirectories[i].getAbsolutePath();
            }

            return projectPaths;
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada por el usuario.", "Error", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
            return null; // Esto nunca debería ocurrir debido a System.exit(0), solo para satisfacer el compilador
        }
    }

    private static boolean processProjectActions(String outputFileName, String[] projectPaths) {
        try {
            for (String path : projectPaths) {
                AnnotationAnalyzer.analyzeAnnotationsInProject(path, outputFileName);
            }
            return true; // Se completó exitosamente
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Hubo un problema
        }
    }

}
