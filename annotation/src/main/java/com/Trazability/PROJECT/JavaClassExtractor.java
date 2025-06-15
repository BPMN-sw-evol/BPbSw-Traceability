package com.Trazability.PROJECT;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaClassExtractor {

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
            JOptionPane.showMessageDialog(null, "Operación cancelada por el usuario, proyectos no analizados", "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return null; // Indica que la operación fue cancelada
        }
    }

    private List<File> listJavaFiles(String directoryPath) {
        List<File> javaFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                } else if (file.isDirectory()) {
                    javaFiles.addAll(listJavaFiles(file.getAbsolutePath()));
                }
            }
        }
        return javaFiles;
    }

    public void javaProcessor(JsonObject bpmnElements) {
        JsonObject projectInfo = new JsonObject();
        List<String> dtoFiles = new ArrayList<>();
        List<String> projectDirectories = Arrays.asList(getProjectPathsFromUserInput());

        projectDirectories.forEach(projectDirectory -> {
            JsonObject projectClasses = new JsonObject();
            listJavaFiles(projectDirectory).forEach(javaFile -> {
                if (javaFile.isFile() && javaFile.getName().contains("DTO")) {
                    dtoFiles.add(javaFile.getName());
                }
                Map<String, String> mostMatchingActivity = findMostMatchingActivity(javaFile.getName(), bpmnElements);
                if (mostMatchingActivity != null) {
                    JsonObject activityObject = new JsonObject();
                    JsonArray variables = new JsonArray();
                    mostMatchingActivity.forEach((key, value) -> {
                        if (!key.equals("variables")) {
                            activityObject.addProperty(key, value);
                        } else {
                            variables.add(value);
                        }
                    });

                    addMethodsAndVariables(javaFile, activityObject, variables, dtoFiles);
                    if (activityObject.size() > 0) {
                        try {
                            Annotate.annotateClassWithBPMNTaskAndMethods(javaFile.getAbsolutePath(), mostMatchingActivity,
                                    activityObject);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        projectClasses.add(javaFile.getName(), activityObject);
                    }
                }
            });
            if (projectClasses.size() > 0) {
                projectInfo.add(projectDirectory, projectClasses);
            }
        });
    }

    private void addMethodsAndVariables(File javaFile, JsonObject activityObject, JsonArray variables, List<String> dtoFiles) {
        try {
            Map<String, Map<String, String>> methodsAndVariables = analyzeJavaFile(javaFile, dtoFiles);
            methodsAndVariables.forEach((methodName, methodVariables) -> {
                if (!methodVariables.isEmpty()) {
                    JsonObject methodObject = new JsonObject();
                    methodVariables.forEach((variableName, variableValue) -> {
                        variables.forEach(variableElement -> {
                            String variable = variableElement.getAsString();
                            String[] words = variable.split(",");
                            Arrays.stream(words)
                                    .filter(word -> variableName.trim().contains(word.trim()))
                                    .forEach(word -> methodObject.addProperty(variableName, variableName));
                        });
                    });
                    if (!methodObject.entrySet().isEmpty()) {
                        activityObject.add(methodName, methodObject);
                    }
                }
            });
        } catch (IOException e) {
            System.out.println("Error al analizar el archivo Java: " + e.getMessage());
        }
    }

    private Map<String, String> findMostMatchingActivity(String className, JsonObject bpmnElements) {
        for (Map.Entry<String, JsonElement> entry : bpmnElements.entrySet()) {
            if (className.split("\\.")[0].contains(entry.getKey())) {
                JsonObject activityObject = entry.getValue().getAsJsonObject();
                Map<String, String> result = new HashMap<>();
                result.put("name", activityObject.get("name").getAsString());
                result.put("type", activityObject.get("type").getAsString());
                if (activityObject.has("variables")) {
                    JsonArray variablesArray = activityObject.getAsJsonArray("variables");
                    List<String> variables = new ArrayList<>();
                    for (JsonElement variable : variablesArray) {
                        variables.add(variable.getAsString());
                    }
                    result.put("variables", String.join(",", variables));
                }
                return result;
            }
        }
        return null;
    }

    private Map<String, Map<String, String>> analyzeJavaFile(File javaFile, List<String> dtoFiles) throws IOException {
        Map<String, Map<String, String>> methods = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(javaFile))) {
            String line;
            String currentMethod = null;
            boolean insideMethod = false;
            int braceCount = 0;

            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("public") || line.trim().startsWith("private")
                        || line.trim().startsWith("protected")) {
                    String[] parts = line.trim().split("\\(");
                    String methodSignature = parts[0].trim();
                    String[] methodParts = methodSignature.split("\\s+");
                    String methodName = methodParts[methodParts.length - 1];
                    String container = "";
                    for (String dtoFile : dtoFiles) {
                        if (line.contains(dtoFile.split("\\.")[0])) {
                            container = dtoFile;
                        }
                    }
                    currentMethod = methodName+"_"+container;
                    insideMethod = true;
                    braceCount = 1;
                } else if (insideMethod && line.contains("{")) {
                    braceCount++;
                } else if (insideMethod && line.contains("}")) {
                    braceCount--;
                    if (braceCount == 0) {
                        currentMethod = null;
                        insideMethod = false;
                    }
                } else if (insideMethod
                        && (line.contains(".setVariable") || line.contains(".getVariable") || line.contains(".get")
                                || line.contains(".getOrDefault") || line.contains(".put"))
                        && !line.contains("context")) {

                    boolean isSetVariable = line.contains(".setVariable") || line.contains(".put");
                    boolean isGetVariable = line.contains(".getVariable") || line.contains(".get")
                            || line.contains(".getOrDefault");
                    Pattern pattern = Pattern.compile(
                            "\\.getVariable\\(\"(.*?)\"\\)|\\.setVariable\\(\".*?\",\\s*(.*?)\\)|\\.get\\(\"(.*?)\"\\)|\\.getOrDefault\\(\"(.*?)\",\\s*(.*?)\\)|\\.put\\(\"(.*?)\",\\s*(.*?)\\)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String variableName;
                        if (matcher.group(1) != null) {
                            variableName = matcher.group(1);
                        } else if (matcher.group(2) != null) {
                            variableName = matcher.group(2);
                        } else if (matcher.group(3) != null) {
                            variableName = matcher.group(3);
                        } else if (matcher.group(4) != null) {
                            variableName = matcher.group(4);
                        } else if (matcher.group(5) != null) {
                            variableName = matcher.group(5);
                        } else if (matcher.group(6) != null) {
                            variableName = matcher.group(6);
                        } else {
                            variableName = "";
                        }
                        if (!variableName.isEmpty()) {
                            String prefix = isSetVariable ? " (set)" : isGetVariable ? " (get)" : "";
                            if (!currentMethod.contains("(get)") && !currentMethod.contains("(set)")) {
                                currentMethod += prefix;
                            }
                            methods.computeIfAbsent(currentMethod, k -> new HashMap<>()).put(variableName, "");
                        }
                    }
                }
            }
        }
        return methods;
    }

}
