package com.Biptrac;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
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
        processJavaFiles(projectDirectory, customAnnotations, outputFileName, projectName, result);
    }

    private static void processJavaFiles(File directory, List<String> customAnnotations, String outputFileName,
            String projectName,  ObjectNode result) {

        // Crear un nodo de array para almacenar múltiples directorios
        ArrayNode directoriesArray = new ObjectMapper().createArrayNode();

         // Agregar cada directorio a la lista del array
         for (String directoryPath : directories) {
            directoriesArray.add(directoryPath);
        }

        // Agregar el array de directorios al nodo de objeto bajo la propiedad
        // "ProjectPath"
        result.set("ProjectPath", directoriesArray);

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                processJavaFiles(file, customAnnotations, outputFileName, projectName, result);
            } else if (file.getName().endsWith(".java")) {
                processJavaFile(file, customAnnotations, result, projectName);
            }
        }
    }

    private static void processJavaFile(File file, List<String> customAnnotations, ObjectNode result,
            String projectName) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            List<TypeDeclaration<?>> types = cu.getTypes();

            for (TypeDeclaration<?> type : types) {
                ObjectNode classNode = new ObjectMapper().createObjectNode();
                List<AnnotationExpr> classAnnotations = type.getAnnotations();

                for (AnnotationExpr annotation : classAnnotations) {
                    if (customAnnotations.contains(annotation.getNameAsString())) {
                        processAnnotations(annotation, classNode, "Class: " + type.getNameAsString());
                    }
                }

                List<MethodDeclaration> methods = type.getMethods();
                for (MethodDeclaration method : methods) {
                    List<AnnotationExpr> methodAnnotations = method.getAnnotations();
                    for (AnnotationExpr annotation : methodAnnotations) {
                        if (customAnnotations.contains(annotation.getNameAsString())) {
                            processAnnotations(annotation, classNode,
                                    "Method: " + formatMethodName(method, annotation.getNameAsString()));
                        }
                    }
                }

                List<FieldDeclaration> fields = type.getFields();
                for (FieldDeclaration field : fields) {
                    List<AnnotationExpr> fieldAnnotations = field.getAnnotations();
                    for (AnnotationExpr annotation : fieldAnnotations) {
                        if (customAnnotations.contains(annotation.getNameAsString())) {
                            processAnnotations(annotation, classNode,
                                    "Field: " + formatFieldName(field, annotation.getNameAsString()));
                        }
                    }
                }

                if (classNode.size() > 0) {
                    ObjectNode projectNode = result.has(projectName) ? (ObjectNode) result.get(projectName)
                            : result.putObject(projectName);
                    projectNode.set("BPM Class: " + type.getNameAsString(), classNode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processAnnotations(AnnotationExpr annotation, ObjectNode classNode, String elementName) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode annotationNode = objectMapper.createObjectNode();

        if (annotation.isNormalAnnotationExpr()) {
            NormalAnnotationExpr normalAnnotation = annotation.asNormalAnnotationExpr();

            for (MemberValuePair pair : normalAnnotation.getPairs()) {
                String key = pair.getNameAsString();
                JsonNode valueNode;

                if (pair.getValue().isArrayInitializerExpr()) {
                    ArrayInitializerExpr arrayInitializer = pair.getValue().asArrayInitializerExpr();
                    ArrayNode variablesArray = objectMapper.createArrayNode();

                    for (Expression arrayValue : arrayInitializer.getValues()) {
                        if (arrayValue.isStringLiteralExpr()) {
                            String value = arrayValue.asStringLiteralExpr().getValue();
                            variablesArray.add(value);
                        }
                    }

                    valueNode = variablesArray;
                } else {
                    String value = pair.getValue().toString().replaceAll("\"", "");
                    valueNode = objectMapper.valueToTree(value);
                }

                annotationNode.set(key, valueNode);
            }

            if (annotationNode.size() > 0) {
                classNode.set("BPM " + elementName, annotationNode);
            } else {
                classNode.put("BPM " + elementName, "No additional information");
            }
        } else {
            classNode.put("BPM " + elementName, "No additional information");
        }
    }

    private static String formatMethodName(MethodDeclaration method, String annotationName) {
        String methodName = method.getNameAsString();
        if (annotationName.toLowerCase().contains("setter")) {
            return methodName + " (setter)";
        } else if (annotationName.toLowerCase().contains("getter")) {
            return methodName + " (getter)";
        }
        return methodName;
    }

    private static String formatFieldName(FieldDeclaration field, String annotationName) {
        String fieldName = field.getVariable(0).getNameAsString();
        if (annotationName.toLowerCase().contains("set")) {
            return fieldName + " (set)";
        } else if (annotationName.toLowerCase().contains("get")) {
            return fieldName + " (get)";
        }
        return fieldName;
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
            String outputDirectory = currentDirectory + File.separator + "biptrac/output";
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
