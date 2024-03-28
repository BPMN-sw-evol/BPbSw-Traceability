package com.Traza.LoadTraza;

import com.Biptrac.AnnotationAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.io.File;


public class ProjectProcessor {
    public String[] getProjectPathsFromUserInput() {
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

    public ObjectNode processProject(String outputFileName, String[] projectPaths) {
        try {
            ObjectNode result = new ObjectMapper().createObjectNode();

            for (String path : projectPaths) {
                AnnotationAnalyzer.analyzeAnnotationsInProject(path, outputFileName, result);
            }

            if (!result.isEmpty()) {
                AnnotationAnalyzer.saveJsonToFile(outputFileName, AnnotationAnalyzer.formatJson(result.toString()));
            }
            return result; // Se completó exitosamente
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al procesar los proyectos.", "Error", JOptionPane.ERROR_MESSAGE);
            return null; // Hubo un problema
        }
    }
}
