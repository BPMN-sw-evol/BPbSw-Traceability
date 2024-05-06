package com.Traza.Controller;

import com.DataBase.DAO.DAOManager;
import Interfaces.Traceability;
import com.Traza.ImageGenerate.BpmnColor;
import com.Traza.ImageGenerate.ImageCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TraceabilityController {
    private final Traceability view;
    private final DAOManager daoManager;
    private int selectedVariableId;

    public TraceabilityController(Traceability view) {
        this.view = view;

        // Obtener la instancia del DAOManager
        this.daoManager = DAOManager.getInstance();

        initView();
    }

    private void initView() {
        view.setResizable(false);
        view.setTitle("TRACEABILITY");
        view.setBImageVisible(false);
        view.setBDiagramVisible(false);

        loadHistory();
        openImage();
        openDiagram();
        // Agrega otros listeners aquí si es necesario
    }

    public void loadHistory() {
        try {
            List<Timestamp> historyIDs = daoManager.getHistoryDAO().getAllHistorys();
                view.updateComboBoxHistory(historyIDs);


        } catch (RuntimeException e) {
            handleException(e);
        }
    }


    private void loadVariableNames(Timestamp history) {
        try {
            List<String> variableNames = daoManager.getVariableDAO().getAllVariableNames(history);
            view.updateComboBoxVariables(variableNames);

        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    public void handleHistorySelection() {
        Object selectedHistory = view.getSelectedHistory();

        if (!selectedHistory.toString().equals("Select a Version")) {
            loadVariableNames((Timestamp) selectedHistory);
        } else {
            view.resetVariablesComboBox();
            view.setBImageVisible(false);
            view.setBDiagramVisible(false);
            view.updateImageIcon("/images.png");

        }
    }

    public void handleVariableSelection() {
        String selectedVariable = view.getSelectedVariable();

        if (!"Select a variable".equals(selectedVariable) && !"Select a version".equals(selectedVariable)) {
            selectedVariableId = daoManager.getVariableDAO().searchVariableByName(selectedVariable);

            List<String> projectNames = daoManager.getProjectDAO().searchProjectByVariableId(selectedVariableId);
            Map<String, String> processInfo = daoManager.getProcessDAO().searchProcessByVariableId(selectedVariableId);

            if (!processInfo.containsKey("error")) {
                view.updateProcessName(processInfo.get("model_name"));
                view.updateParticipant(processInfo.get("process_name"));
            }

            view.updateProjectsList(projectNames);

            if (projectNames == null || projectNames.isEmpty()) {
                view.updateProjectCount("0");
                view.updateClassCount("0");
                view.updateMethodCount("0");

            }
        } else {
            view.updateProjectsList(null);
            view.updateProcessName("Select a variable");
            view.updateParticipant("Select a variable");
            view.updateImageIcon("/images.png");
        }
    }

    public void handleProjectSelection() {
        String selectedProject = view.getSelectedProject();
        int projectId = daoManager.getProjectDAO().searchProject(selectedProject);

        String containerName = daoManager.getContainerDAO().searchContainerName(projectId, selectedVariableId);
        view.updateContainerText(containerName);

        List<String> classNames = daoManager.getClassDAO().searchClassById(projectId, selectedVariableId);
        view.updateClassList(classNames);

        if (classNames == null || classNames.isEmpty()) {
            view.updateClassCount("0");
        }
    }

    public void handleClassSelection() {
        String selectedClass = view.getSelectedClass();
        int classId = daoManager.getClassDAO().searchClass(selectedClass);

        List<String> methodNames = daoManager.getMethodDAO().searchMethodById(classId, selectedVariableId);
        view.updateMethodList(methodNames);

        if (methodNames == null || methodNames.isEmpty()) {
            view.updateMethodCount("0");
        }
    }

    public void handleElementSelection() {
        if (selectedVariableId <= 0 || Objects.equals(view.getSelectedVariable(), "Select a variable")) {
            // Manejar el caso de un ID de variable no válido
            view.updateImageIcon("/images.png");
            view.setBImageVisible(false);
            view.setBDiagramVisible(false);
            return;
        }
        view.updateImageIcon("/sandClock.gif");

        // Ejecutar la lógica relacionada con la selección de elementos en un hilo de fondo
        new Thread(() -> {
            List<String> usedElementNames = daoManager.getElementDAO().searchElementsUsed(selectedVariableId);
            String path = daoManager.getPathDAO().getModelBPMNPath(selectedVariableId);

            if (!usedElementNames.isEmpty() && !usedElementNames.get(0).equals("Elemento no encontrado")) {
                BpmnColor.getInstance().modifyActivityColors(usedElementNames, path);

                // Captura la imagen
                new ImageCapture();


                // Cargar la imagen en la vista dentro del EDT
                loadImage();

            } else {
                System.out.println("No se encontraron elementos usados para la variable con ID " + selectedVariableId);
                // Ocultar la barra de progreso en caso de que no se encuentren elementos
            }
        }).start();
    }

    private void loadImage() {
        String rutaImagen = Paths.get(System.getProperty("user.dir"), "output", "MSGF-Test-Color.png").toString();
        view.displayImage(rutaImagen);
        view.setBImageVisible(true);
        view.setBDiagramVisible(true);
    }

    private void openImage() {
        view.addOpenImageListener(e -> {
            String rutaImagen = Paths.get(System.getProperty("user.dir"), "output", "MSGF-Test-Color.png").toString();
            openFile(rutaImagen);
        });
    }

    private void openDiagram() {
        view.addOpenDiagramListener(e -> {
            String rutaDiagram = Paths.get(System.getProperty("user.dir"), "output", "ColorModel.bpmn").toString();
            openFile(rutaDiagram);
        });
    }

    private void openFile(String filePath) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            File file = new File(filePath);

            if (file.exists()) {
                try {
                    desktop.open(file);
                } catch (IOException ex) {
                    view.displayErrorMessage("Error al abrir el archivo: " + ex.getMessage());
                }
            } else {
                view.displayErrorMessage("El archivo no existe.");
            }
        }
    }

    private void handleException(Exception e) {
        Logger.getLogger(TraceabilityController.class.getName()).log(Level.SEVERE, null, e);
    }

}
