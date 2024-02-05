package com.Trazability;

import com.Trazability.DataBase.Data;
import com.Trazability.Projects.AnnotationAnalyzer;
import com.google.gson.JsonObject;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.Trazability.Camunda.GetTaskCamunda.*;

public class Main {

    public static BpmnModelInstance modelInstance = null;

    public static boolean  main(String[] args) throws IOException {
        String bpmnFilePath = selectBpmnFile();
        if (bpmnFilePath == null) {
            // El usuario canceló la selección del archivo BPMN
            return false;
        }

        String[] projectPaths = getProjectPathsFromUserInput();
        if (projectPaths == null) {
            // El usuario canceló la selección de directorios de proyecto
            return false;
        }

        JsonObject bpmnDetails = new JsonObject();
        boolean successBpmn = processBpmnModel(bpmnFilePath, bpmnDetails);
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
        return successBpmn && successProject;
    }

    private static String selectBpmnFile() {
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

    private static boolean processBpmnModel(String bpmnFilePath, JsonObject bpmnDetails) {
        try {
            modelInstance = Bpmn.readModelFromFile(new File(bpmnFilePath));
            File file = new File(bpmnFilePath);
            String fileNameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.'));

            Collection<StartEvent> startEvents = modelInstance.getModelElementsByType(StartEvent.class);
            for (StartEvent startEvent : startEvents) {
                Set<String> visitedNodes = new HashSet<>();
                listActivitiesFromStartEvent(modelInstance, startEvent, visitedNodes, bpmnDetails);
            }

            JsonObject bpmnWithBpmName = new JsonObject();
            bpmnWithBpmName.addProperty("bpmPath", bpmnFilePath);
            bpmnWithBpmName.addProperty("bpmNameFile", file.getName());
            bpmnWithBpmName.addProperty("bpmNameProcess", getParticipant(modelInstance));
            bpmnWithBpmName.add("trace", bpmnDetails.getAsJsonArray("trace"));

            // Guardar el JSON con el campo "bpmName"
            saveJsonToFile(fileNameWithoutExtension, formatJson(bpmnWithBpmName.toString()));

            return true; // Se completó exitosamente
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al procesar el modelo BPMN.", "Error", JOptionPane.ERROR_MESSAGE);
            return false; // Hubo un problema
        }
    }

    private static String[] getProjectPathsFromUserInput() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setMultiSelectionEnabled(true);

        int result = dirChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedDirectories = dirChooser.getSelectedFiles();
            String[] projectPaths = new String[selectedDirectories.length];

            for (int i = 0; i < selectedDirectories.length; i++) {
                projectPaths[i] = selectedDirectories[i].getAbsolutePath();
            }

            return projectPaths;
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada por el usuario, proyectos no analizados", "Error", JOptionPane.INFORMATION_MESSAGE);
            return null; // Indica que la operación fue cancelada
        }
    }

    private static boolean processProjectActions(String outputFileName, String[] projectPaths) {
        try {
            for (String path : projectPaths) {
                AnnotationAnalyzer.analyzeAnnotationsInProject(path, outputFileName);
            }
            return true; // Se completó exitosamente
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al procesar los proyectos.", "Error", JOptionPane.ERROR_MESSAGE);
            return false; // Hubo un problema
        }
    }
}
