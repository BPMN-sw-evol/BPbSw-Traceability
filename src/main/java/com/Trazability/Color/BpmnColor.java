package com.Trazability.Color;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.UserTask;

import java.io.*;
import java.util.*;
import org.camunda.bpm.model.bpmn.instance.Participant;

public class BpmnColor {

    private static final String bpmnFilePath = "C:\\Users\\SOPORTES JPVM\\Desktop\\MSGF-Test.bpmn";
    private static final File archivoTemp = new File(bpmnFilePath + ".bpmn");
    private static final String stroke = "#0d4372";
    private static final String fill = "#ffe0b2";
    private static final String background = "#ffe0b2";
    private static final String border = "#6b3c00";

    public void modifyActivityColors(List<String> activityNames) throws IOException {
        List<String> activityIds = findActivityIds(activityNames);
        modifyColor(activityIds);
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
    
    public String findParticipantName() {
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(bpmnFilePath));

        for (Participant participant : modelInstance.getModelElementsByType(Participant.class)) {
            return participant.getName();
        }

        return null;
    }
    
}
