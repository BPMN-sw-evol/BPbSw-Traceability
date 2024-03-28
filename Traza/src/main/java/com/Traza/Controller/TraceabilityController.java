package com.Traza.Controller;

import com.DataBase.DAO.*;
import Interfaces.Traceability;
import com.Traza.ImageGenerate.BpmnColor;
import com.Traza.ImageGenerate.ImageCapture;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TraceabilityController {
    private final Traceability view;
    private final HistoryDAO historyDAO;
    private final VariableDAO variableDAO;
    private final ContainerDAO containerDAO;
    private final ProjectDAO projectDAO;
    private final ProcessDAO processDAO;
    private final ClassDAO classDAO;
    private final MethodDAO methodDAO;
    private final ElementDAO elementDAO;
    private final PathDAO pathDAO;
    private int selectedVariableId;

    public TraceabilityController(Traceability view) {
        this.view = view;

        // Obtener la instancia del DAOManager
        DAOManager daoManager = DAOManager.getInstance();

        // Obtener intancias de los DAO's
        this.historyDAO = daoManager.getHistoryDAO();
        this.variableDAO = daoManager.getVariableDAO();
        this.containerDAO = daoManager.getContainerDAO();
        this.projectDAO = daoManager.getProjectDAO();
        this.processDAO = daoManager.getProcessDAO();
        this.classDAO = daoManager.getClassDAO();
        this.methodDAO = daoManager.getMethodDAO();
        this.elementDAO = daoManager.getElementDAO();
        this.pathDAO = daoManager.getPathDAO();

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
            List<Integer> historyIDs = historyDAO.getAllHistorys();
            historyIDs.addFirst(0);
            if (!historyIDs.isEmpty()) {
                view.updateComboBoxHistory(historyIDs);
            } else {
                throw new RuntimeException("No se encontraron nombres de variables.");
            }
        } catch (RuntimeException e) {
            handleException(e);
        }
    }


    private void loadVariableNames(int history) {
        try {
            List<String> variableNames = variableDAO.getAllVariableNames(history);
            variableNames.addFirst("Choose a variable");

            if (!variableNames.isEmpty()) {
                view.updateComboBoxVariables(variableNames);
            } else {
                throw new RuntimeException("No se encontraron nombres de variables.");
            }
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    public void handleHistorySelection() {
        int selectedHistory = view.getSelectedHistory();

        if (selectedHistory != 0) {
            loadVariableNames(selectedHistory);
        } else {
            view.resetVariablesComboBox();

        }
    }

    public void handleVariableSelection() {
        String selectedVariable = view.getSelectedVariable();

        if (!"Choose a variable".equals(selectedVariable) && !"Choose a version".equals(selectedVariable)) {
            selectedVariableId = variableDAO.searchVariableByName(selectedVariable);

            List<String> projectNames = projectDAO.searchProjectByVariableId(selectedVariableId);
            Map<String, String> processInfo = processDAO.searchProcessByVariableId(selectedVariableId);

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

            view.setBImageVisible(true);
            view.setBDiagramVisible(true);
        } else {
            view.updateProjectsList(null);
            view.updateProcessName("Select a variable");
            view.updateParticipant("Select a variable");
            view.updateImageIcon();
            view.setBImageVisible(false);
            view.setBDiagramVisible(false);
        }
    }

    public void handleProjectSelection() {
        String selectedProject = view.getSelectedProject();
        int projectId = projectDAO.searchProject(selectedProject);

        String containerName = containerDAO.searchContainerName(projectId, selectedVariableId);
        view.updateContainerText(containerName);

        List<String> classNames = classDAO.searchClassById(projectId, selectedVariableId);
        view.updateClassList(classNames);

        if (classNames == null || classNames.isEmpty()) {
            view.updateClassCount("0");
        }
    }

    public void handleClassSelection() {
        String selectedClass = view.getSelectedClass();
        int classId = classDAO.searchClass(selectedClass);

        List<String> methodNames = methodDAO.searchMethodById(classId, selectedVariableId);
        view.updateMethodList(methodNames);

        if (methodNames == null || methodNames.isEmpty()) {
            view.updateMethodCount("0");
        }
    }


    public void handleElementSelection() {
        view.updateImageIcon();
        if (selectedVariableId <= 0) {
            // Manejar el caso de un ID de variable no válido
            return;
        }
        // Ejecutar la lógica relacionada con la selección de elementos en un hilo de fondo
        new Thread(() -> {
            List<String> usedElementNames = elementDAO.searchElementsUsed(selectedVariableId);
            String path = pathDAO.getModelBPMNPath(selectedVariableId);

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
        String rutaImagen = Paths.get(System.getProperty("user.dir"), "Traza/output", "MSGF-Test-Color.png").toString();
        view.displayImage(rutaImagen);
    }

    private void openImage() {
        view.addOpenImageListener(e -> {
            String rutaImagen = Paths.get(System.getProperty("user.dir"), "Traza/output", "MSGF-Test-Color.png").toString();
            openFile(rutaImagen);
        });
    }

    private void openDiagram() {
        view.addOpenDiagramListener(e -> {
            String rutaDiagram = Paths.get(System.getProperty("user.dir"), "Traza/output", "ColorModel.bpmn").toString();
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
