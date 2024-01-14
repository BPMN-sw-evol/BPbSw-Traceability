package com.Trazability;

import static com.Trazability.Camunda.GetTaskCamunda.*;

import com.Trazability.DataBase.Data;
import com.Trazability.Projects.AnnotationAnalyzer;
import com.google.gson.JsonObject;

import Interfaces.Traceability;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

public class Main {

    public static void main(String[] args) throws IOException {
        String bpmnFilePath = loadBpmnModelInstance();

        BpmnModelInstance modelInstance = null;
        String fileNameWithoutExtension = "";
        try {
            modelInstance = Bpmn.readModelFromFile(new File(bpmnFilePath));
            File file = new File(bpmnFilePath);
            String fileNameWithExtension = file.getName();
            fileNameWithoutExtension = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'));
        } catch (Exception e) {
            System.err.println("Error al cargar el archivo BPMN: " + e.getMessage());
            return;
        }

        JsonObject bpmnDetails = new JsonObject();
        boolean successBpmn = processBpmnModel(modelInstance, bpmnDetails, fileNameWithoutExtension);

        String outputFileName = "";
        String[] projectPaths = new String[0];

        Scanner scanner = new Scanner(System.in);
        projectPaths = getProjectPathsFromUserInput(scanner);
        outputFileName = "MSG-Foundation";
        scanner.close();

        boolean successProject = processProjectActions(outputFileName, projectPaths);

        if (successBpmn && successProject) {
            System.out.println(
                    "Tanto la información de BPMN como la de proyectos se procesaron y guardaron exitosamente.");
            Data data = new Data("D:\\Laboral\\BPbSw-Traceability\\output\\MSG-Foundation.json", 
                         "D:\\Laboral\\BPbSw-Traceability\\output\\MSGF-Test.json", 
                         "MSG-Foundation");
            
            if (data.isDataInitialized()) {
                System.out.println("La informacion se ha guardado con exito.");
                
                /* Create and display the form */
               java.awt.EventQueue.invokeLater(new Runnable() {
           public void run() {
               new Traceability().setVisible(true);
           }
       });
            } else {
                System.err.println("Error al guardar la informacion.");
            }
        } else {
            System.out.println("Hubo un problema al procesar y guardar la información de BPMN y/o proyectos.");
        }
    }

    private static String loadBpmnModelInstance() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la ruta del archivo BPMN (.bpmn): ");
        String bpmnFilePath = scanner.nextLine();
        return bpmnFilePath;
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

    private static String[] getProjectPathsFromUserInput(Scanner scanner) {
        System.out.print("Ingrese la cantidad de proyectos a procesar: ");
        int numberOfProjects = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        String[] projectPaths = new String[numberOfProjects];
        for (int i = 0; i < numberOfProjects; i++) {
            System.out.print("Ingrese la ruta del proyecto #" + (i + 1) + ": ");
            projectPaths[i] = scanner.nextLine();
        }
        return projectPaths;
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
