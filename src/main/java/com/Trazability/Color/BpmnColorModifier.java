package com.Trazability.Color;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.ModelInstance;

public class BpmnColorModifier {

private static String bpmnFilePath = "C:\\Users\\SOPORTES JPVM\\Desktop\\MSGF-Test.bpmn";
private static File archivoTemp = new File(bpmnFilePath + ".bpmn");
    private static String stroke = "#0d4372";
    private static String fill = "#ffe0b2";
    private static String background = "#ffe0b2";
    private static String border = "#6b3c00";
    List<String> modifiedLines = new ArrayList<>();

    public void modifyActivityColors(List<String> activityNames) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTemp));

        for (String activityName : activityNames) {
            // Obtener los IDs de la actividad desde el método modificado
            String[] activityIds = buscarIdsActividad(activityName);

            if (activityIds != null) {
                // Llamar al método modifyColor con la lista de IDs
modifyColor(Arrays.asList(activityIds));
            }

        }

        boolean definitionsTagFound = false;
        boolean biocNamespaceFound = false;
        boolean colorNamespaceFound = false;

        try (bw) {
            for (String line : modifiedLines) {
                if (line.contains("<bpmn:definitions")) {
                    definitionsTagFound = true;

                    // Verificamos si las líneas ya existen
                    if (!biocNamespaceFound) {
                        bw.write("    xmlns:bioc=\"http://bpmn.io/schema/bpmn/biocolor/1.0\"");
                        bw.newLine();
                        biocNamespaceFound = true;
                    }

                    if (!colorNamespaceFound) {
                        bw.write("    xmlns:color=\"http://www.omg.org/spec/BPMN/non-normative/color/1.0\"");
                        bw.newLine();
                        colorNamespaceFound = true;
                    }
                } else if (definitionsTagFound && line.contains("</bpmn:definitions>")) {
                    // Verificamos si las líneas ya existen antes de cerrar la etiqueta
                    if (!biocNamespaceFound) {
                        bw.write("    xmlns:bioc=\"http://bpmn.io/schema/bpmn/biocolor/1.0\"");
                        bw.newLine();
                    }

                    if (!colorNamespaceFound) {
                        bw.write("    xmlns:color=\"http://www.omg.org/spec/BPMN/non-normative/color/1.0\"");
                        bw.newLine();
                    }

                    definitionsTagFound = false;
                }

                // Escribimos la línea en el nuevo archivo
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(BpmnColorModifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[] buscarIdsActividad(String nombreActividad) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(bpmnFilePath));

        List<String> ids = new ArrayList<>();

        // Buscar UserTask
        for (UserTask userTask : modelInstance.getModelElementsByType(UserTask.class)) {
            String name = userTask.getName();
            if (nombreActividad.equals(name)) {
                ids.add(userTask.getId());
            }
        }
        return ids.isEmpty() ? null : ids.toArray(new String[0]);
    }

 public void modifyColor(List<String> activityIds) {
        Set<String> idsModificados = new HashSet<>(activityIds);

        // Modificar las líneas en memoria
        List<String> lineasModificadas = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(bpmnFilePath))) {
            String linea;

            // Leer y modificar cada línea del archivo original
            while ((linea = br.readLine()) != null) {
                if (linea.contains("bpmndi:BPMNShape") && linea.contains("bpmnElement")) {
                    // Extraer el valor de bpmnElement
                    String bpmnElement = obtenerValorAtributo(linea, "bpmnElement");
                    if (bpmnElement != null && idsModificados.contains(bpmnElement)) {
                        // Modificar la línea y agregar a la lista de modificadas
                        lineasModificadas.add(modificarLinea(linea));
                        idsModificados.remove(bpmnElement);
                        continue; // Saltar la escritura de esta línea al archivo
                    }
                }

                // Agregar la línea original a la lista de modificadas o no modificadas
                lineasModificadas.add(linea);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Escribir las líneas modificadas al archivo original
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTemp))) {
            System.out.println("aqui "+lineasModificadas);
            for (String lineaModificada : lineasModificadas) {
                bw.write(lineaModificada);
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String obtenerValorAtributo(String linea, String atributo) {
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


    private String modificarLinea(String linea) {
        // Modificar la línea
        return linea.replace(">", "")
                + " bioc:stroke=\"" + stroke + "\" bioc:fill=\"" + fill + "\" "
                + "color:background-color=\"" + background + "\" color:border-color=\"" + border + "\">";
    }
}
