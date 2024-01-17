package com.Trazability.Color;

import com.Trazability.Main;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import java.io.*;
import java.util.*;
import org.camunda.bpm.model.bpmn.instance.Participant;
import java.io.File;
import java.io.IOException;

public class BpmnColor {

    private final String bpmnFilePath = Main.getBpmnFilePath();
    private File archivoTemp = null;
    private final String stroke = "#0d4372";
    private final String fill = "#ffe0b2";
    private final String background = "#ffe0b2";
    private final String border = "#6b3c00";

    public void modifyActivityColors(List<String> activityNames) {

        // Obtener la ruta del directorio "output" en la raíz del proyecto
        String outputDirPath = System.getProperty("user.dir") + File.separator + "output";

        // Crear el directorio si no existe
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Crear el archivo temporal en el directorio "output"
        archivoTemp = new File(outputDir, "ColorModel.bpmn");

        List<String> activityIds = findActivityIds(activityNames);
        modifyColor(activityIds);

        // Generar imagen del modelo resultante
        generateImage();
    }

    private List<String> findActivityIds(List<String> activityNames) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(bpmnFilePath));
        List<String> activityIds = new ArrayList<>();

        for (String activityName : activityNames) {
            // Obtener los IDs de la actividad desde el método modificado
            String id = findActivityId(modelInstance, activityName);

            if (id != null) {
                activityIds.add(id);
            }
        }
        return activityIds;
    }

    private String findActivityId(BpmnModelInstance modelInstance, String nombreActividad) {
        for (UserTask userTask : modelInstance.getModelElementsByType(UserTask.class)) {
            String name = userTask.getName();
            if (nombreActividad.equals(name)) {
                return userTask.getId();
            }
        }
        return null;
    }

    private void modifyColor(List<String> activityIds) {
        Set<String> idsModificados = new HashSet<>(activityIds);
        List<String> lineasModificadas = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(bpmnFilePath))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                if (linea.contains("<bpmn:definitions") && !linea.contains("xmlns:bioc=\"http://bpmn.io/schema/bpmn/biocolor/1.0\"") && !linea.contains("xmlns:color=\"http://www.omg.org/spec/BPMN/non-normative/color/1.0\"")) {
                    lineasModificadas.add(modifyLine2(linea));
                    continue;

                }

                if (linea.contains("bpmndi:BPMNShape") && linea.contains("bpmnElement")) {
                    String bpmnElement = getAttributeValue(linea, "bpmnElement");
                    if (bpmnElement != null && idsModificados.contains(bpmnElement)) {
                        lineasModificadas.add(modifyLine(linea));
                        idsModificados.remove(bpmnElement);
                        continue;
                    }
                }
                lineasModificadas.add(linea);

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTemp))) {
            for (String lineaModificada : lineasModificadas) {
                bw.write(lineaModificada);
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getAttributeValue(String linea, String atributo) {
        String patron = atributo + "=\"";
        int startIndex = linea.indexOf(patron);
        if (startIndex != -1) {
            startIndex += patron.length();
            int endIndex = linea.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return linea.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    private String modifyLine(String linea) {
        return linea.replace(">", "")
                + " bioc:stroke=\"" + stroke + "\" bioc:fill=\"" + fill + "\" "
                + "color:background-color=\"" + background + "\" color:border-color=\"" + border + "\">";
    }

    private String modifyLine2(String linea) {
        return linea.replace(">", "")
                + " xmlns:bioc=\"http://bpmn.io/schema/bpmn/biocolor/1.0\" xmlns:color=\"http://www.omg.org/spec/BPMN/non-normative/color/1.0\" >";
    }

    public String findParticipantName() {
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(bpmnFilePath));
        for (Participant participant : modelInstance.getModelElementsByType(Participant.class)) {
            return participant.getName();
        }

        return null;
    }

    public void generateImage() {

    }

}
