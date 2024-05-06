package com.Biptrac;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Formats {
    public static String formatMethodName(MethodDeclaration method, String annotationName) {
        String methodName = method.getNameAsString();
        if (annotationName.toLowerCase().contains("setter")) {
            return methodName + " (setter)";
        } else if (annotationName.toLowerCase().contains("getter")) {
            return methodName + " (getter)";
        }
        return methodName;
    }

    public static String formatFieldName(FieldDeclaration field, String annotationName) {
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

}
