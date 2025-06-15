package com.Trazability.PROJECT;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Annotate {
    public static void annotateClassWithBPMNTaskAndMethods(String className, Map<String, String> mostMatchingActivity,
                                                           JsonObject activityObject) throws IOException {
        annotateClassWithBPMNTask(className, mostMatchingActivity);

        activityObject.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("name") && !entry.getKey().equals("type"))
                .forEach(entry -> {
                    String methodName = entry.getKey();
                    String methodType = methodName.split(" ")[1];
                    String[] containerParts = methodName.split(" ")[0].split("_");
                    String container = containerParts.length > 1 ? containerParts[1] : "";
                    if (container != null || !container.isEmpty()){
                        JsonElement methodValue = entry.getValue();
                        if (methodValue.isJsonObject()) {
                            JsonObject methodParamsObject = methodValue.getAsJsonObject();
                            String[] variables = methodParamsObject.keySet().toArray(new String[0]);
                            String methodAnnotation = buildMethodAnnotation(variables, methodType, container);
                            try {
                                annotateMethod(className, methodName.split(" ")[0].split("_")[0], methodAnnotation);
                            } catch (IOException e) {
                                System.err.println("Error al anotar el método: " + e.getMessage());
                            }
                        } else {
                            System.err.println("El valor del método no es un JSON Object: " + methodValue);
                        }
                    }

                });
    }

    private static void annotateClassWithBPMNTask(String className, Map<String, String> mostMatchingActivity)
            throws IOException {
        String type = mostMatchingActivity.get("type");
        String name = mostMatchingActivity.get("name");
        String annotation = String.format("@BPMNTask(type = \"%s\", name = \"%s\")", type, name);

        File classFile = new File(className);
        if (!classFile.exists()) {
            throw new FileNotFoundException("El archivo de clase no existe: " + classFile.getAbsolutePath());
        }
        String classContent = new String(Files.readAllBytes(classFile.toPath()), StandardCharsets.UTF_8);

        // Verificar si la anotación ya existe en la clase
        if (classContent.contains(annotation)) {
            return; // Si la anotación ya está presente, no hacemos nada
        }

        // Encuentra la posición para insertar la anotación justo encima de la
        // declaración de la clase
        int classDeclarationIndex = classContent.indexOf("public class");
        if (classDeclarationIndex == -1) {
            throw new IOException(
                    "No se encontró la declaración de la clase en el archivo: " + classFile.getAbsolutePath());
        }

        // Inserta la anotación encima de la declaración de la clase
        classContent = classContent.substring(0, classDeclarationIndex) +
                annotation + "\n" +
                classContent.substring(classDeclarationIndex);

        // Escribe el contenido modificado de vuelta al archivo
        Files.write(classFile.toPath(), classContent.getBytes(StandardCharsets.UTF_8));
    }

    private static void annotateMethod(String className, String methodName, String methodAnnotation) throws IOException {
        File classFile = new File(className);
        if (!classFile.exists()) {
            throw new FileNotFoundException("El archivo de clase no existe: " + classFile.getAbsolutePath());
        }
        String classContent = new String(Files.readAllBytes(classFile.toPath()), StandardCharsets.UTF_8);
        String methodDeclarationRegex = "\\b" + methodName + "\\b";
        Pattern pattern = Pattern.compile(methodDeclarationRegex);
        Matcher matcher = pattern.matcher(classContent);
        if (!matcher.find()) {
            throw new IOException("No se encontró el método en el archivo: " + methodName);
        }
        int methodStartIndex = classContent.lastIndexOf("\n", matcher.start());
        int methodEndIndex = classContent.indexOf("\n", matcher.end());
        String methodContent = classContent.substring(classContent.lastIndexOf("\n", methodStartIndex - 1) + 1, methodStartIndex).trim(); // Incluye una línea
        // adicional antes del
        // método
        String methodContentOriginal = classContent.substring(methodStartIndex, methodEndIndex); // No incluye una
        // línea
        // adicional antes
        // del
        // método

        // Verificar si la anotación ya existe en la línea anterior al método
        if (methodContent.contains(methodAnnotation)) {
            return; // Si la anotación ya está presente, no hacemos nada
        }
        // Insertamos la nueva anotación en la línea anterior al método
        String updatedMethodContent = methodAnnotation + methodContentOriginal;
        // Actualizamos el contenido del método en el archivo de clase
        classContent = classContent.substring(0, methodStartIndex) +
                "\n" + "    " + updatedMethodContent.trim() +
                classContent.substring(methodEndIndex);
        Files.write(classFile.toPath(), classContent.getBytes(StandardCharsets.UTF_8));
    }


    private static String buildMethodAnnotation(String[] variables, String methodType, String container) {
        StringBuilder annotationBuilder = new StringBuilder();

        if ("(get)".equals(methodType)) {
            annotationBuilder.append("@BPMNGetterVariables(");
        } else if ("(set)".equals(methodType)) {
            annotationBuilder.append("@BPMNSetterVariables(");
        } else {
            throw new IllegalArgumentException("Tipo de método no válido: " + methodType);
        }
        if (!container.isEmpty()){
            annotationBuilder.append("container = \"").append(container.split("\\.")[0]).append("\", variables = { ");
            annotationBuilder.append(
                    Arrays.stream(variables).map(variable -> "\"" + variable + "\"").collect(Collectors.joining(", ")));
            annotationBuilder.append(" })");
        }else {

        annotationBuilder.append(" variables = { ").append(
                Arrays.stream(variables).map(variable -> "\"" + variable + "\"").collect(Collectors.joining(", ")));
        annotationBuilder.append(" })");
        }
        return annotationBuilder.toString();
    }
}
