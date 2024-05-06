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
import com.github.javaparser.ast.expr.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessAnnotations {
    public static void processJavaFiles(File directory, List<String> customAnnotations, String outputFileName,
                                         String projectName, ObjectNode result, List<String> directories) {

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
                processJavaFiles(file, customAnnotations, outputFileName, projectName, result, directories);
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
                                    "Method: " + Formats.formatMethodName(method, annotation.getNameAsString()));
                        }
                    }
                }

                List<FieldDeclaration> fields = type.getFields();
                for (FieldDeclaration field : fields) {
                    List<AnnotationExpr> fieldAnnotations = field.getAnnotations();
                    for (AnnotationExpr annotation : fieldAnnotations) {
                        if (customAnnotations.contains(annotation.getNameAsString())) {
                            processAnnotations(annotation, classNode,
                                    "Field: " + Formats.formatFieldName(field, annotation.getNameAsString()));
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
}
