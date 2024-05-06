package com.Biptrac;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotationAnalyzer {

    // Crear una lista para almacenar los directorios
    public static List<String> directories = new ArrayList<>();

    public static void analyzeAnnotationsInProject(String projectPath, String outputFileName,  ObjectNode result) {
        File projectDirectory = new File(projectPath);
        String projectName = projectDirectory.getName();

        if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
            System.err.println("Invalid project path.");
            return;
        }

        List<String> customAnnotations = Arrays.asList("BPMNTask", "BPMNGetVariables", "BPMNSetVariables",
                "BPMNGetterVariables", "BPMNSetterVariables");

        if (!directories.contains(projectPath)) {
            directories.add(projectPath);
        }
        ProcessAnnotations.processJavaFiles(projectDirectory, customAnnotations, outputFileName, projectName, result, directories);
    }



    public static void saveJsonToFile(String fileName, String json) {
        try {
            // Obtener la ruta del directorio actual del proyecto
            String currentDirectory = System.getProperty("user.dir");

            // Crear el directorio "output" si no existe
            String outputDirectory = currentDirectory + File.separator + "biptrac/JSONResult";
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
