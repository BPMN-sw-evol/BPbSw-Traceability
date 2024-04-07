package Interfaces;

import com.Traza.Controller.TraceabilityController;
import com.Traza.LoadTraza.DataLoad;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Traceability extends javax.swing.JFrame {

    private TraceabilityController controller;

    public Traceability() {

        initComponents();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                updateImageIcon();
            }
        });
        controller = new TraceabilityController(this);

        HISTORY.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.handleHistorySelection();
            }
        });
        VARIABLES.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.handleVariableSelection();
                controller.handleElementSelection();
            }
        });

        LPROJECTS.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    controller.handleProjectSelection();
                }
            }
        });

        LCLASSES.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    controller.handleClassSelection();
                }
            }
        });

        LOAD.addActionListener(e -> {
            if (new DataLoad().dataProcessor()) {
                controller.loadHistory();
            }
        });

    }

 // -----------------------------

    public void setVariable(String variable) {
        // Seleccionar el último elemento en HISTORY
        int lastIndexHistory = HISTORY.getItemCount() - 1;
        if (lastIndexHistory >= 0) {
            Object lastItemHistory = HISTORY.getItemAt(lastIndexHistory);
            HISTORY.setSelectedItem(lastItemHistory);
        }

        // Buscar la variable en VARIABLES
        int itemCount = VARIABLES.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            String item = VARIABLES.getItemAt(i);
            if (item.equals(variable)) {
                // Seleccionar la variable si se encuentra
                VARIABLES.setSelectedIndex(i);
                LPROJECTS.setSelectedIndex(0);
                LCLASSES.setSelectedIndex(0);
                return; // Salir del método una vez que se encuentra la variable
            }
        }

        // Si la variable no se encuentra, mostrar un mensaje de error
        int opcion = JOptionPane.showConfirmDialog(null, "La variable seleccionada no se encuentra trazada, ¿Desea generar una traza?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            new DataLoad().dataProcessor();
            controller.loadHistory();
        }
    }


    public void resetVariablesComboBox() {
        VARIABLES.removeAllItems();
        VARIABLES.addItem("Choose a version");
    }

    // Metodos para obtener el elemento
    public Object getSelectedHistory() {
        return (HISTORY.getSelectedItem() != null) ? (HISTORY.getSelectedItem()) : null;
    }

    public String getSelectedVariable() {
        return (String) VARIABLES.getSelectedItem();
    }

    public String getSelectedProject() {
        return LPROJECTS.getSelectedValue();
    }

    public String getSelectedClass() {
        return LCLASSES.getSelectedValue();
    }


    // Métodos para establecer la visibilidad de los botones BImage y BDiagram
    public void setBImageVisible(boolean visible) {
        BIMAGE.setEnabled(visible);
    }

    public void setBDiagramVisible(boolean visible) {
        BDIAGRAM.setEnabled(visible);
    }

    // Metodos para actualizar los diferentes campos en la vista
    public void updateComboBoxHistory(List<Timestamp> items) {
        HISTORY.removeAllItems();
        HISTORY.addItem("Select a Version");
        items.forEach(HISTORY::addItem);
    }

    public void updateComboBoxVariables(List<String> items) {
        VARIABLES.removeAllItems();
        items.forEach(VARIABLES::addItem);
    }

    public void updateProcessName(String processName) {
        PROCESS.setText(processName != null && !processName.isEmpty() ? processName : "Process name not found or error in the search.");
    }

    public void updateParticipant(String participant) {
        PARTICIPANT.setText(participant != null && !participant.isEmpty() ? participant : "Participant name not found or error in the search.");
    }

    public void updateProjectCount(String count) {
        CountProjects.setText(count);
    }

    public void updateClassCount(String count) {
        CountClasses.setText(count);
    }

    public void updateMethodCount(String count) {
        CountMethods.setText(count);
    }

    public void updateImageIcon() {
        JMODEL.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images.png"))));
    }

    public void updateContainerText(String containerName) {
        CONTAINER.setText(containerName != null && !containerName.isEmpty() ? containerName : "Variable not selected");
    }

    public void updateProjectsList(List<String> projectNames) {
        if (projectNames != null && !projectNames.isEmpty()) {
            DefaultListModel<String> model = new DefaultListModel<>();
            projectNames.forEach(model::addElement);
            LPROJECTS.setModel(model);
            updateProjectCount(Integer.toString(projectNames.size()));
            LPROJECTS.setSelectedIndex(0);
        } else {
            DefaultListModel<String> defaultModel = new DefaultListModel<>();
            defaultModel.addElement("Select a variable.");
            LPROJECTS.setModel(defaultModel);
        }
    }

    public void updateClassList(List<String> classNames) {
        if (classNames != null && !classNames.isEmpty()) {
            DefaultListModel<String> model = new DefaultListModel<>();
            classNames.forEach(model::addElement);
            LCLASSES.setModel(model);
            updateClassCount(Integer.toString(classNames.size()));
            LCLASSES.setSelectedIndex(0);
        } else {
            DefaultListModel<String> defaultModel = new DefaultListModel<>();
            defaultModel.addElement("Project not selected");
            LCLASSES.setModel(defaultModel);
        }
    }

    public void updateMethodList(List<String> methodNames) {
        if (methodNames != null && !methodNames.isEmpty()) {
            DefaultListModel<String> model = new DefaultListModel<>();
            methodNames.forEach(model::addElement);
            LMETHODS.setModel(model);
            updateMethodCount(Integer.toString(methodNames.size()));
        } else {
            DefaultListModel<String> defaultModel = new DefaultListModel<>();
            defaultModel.addElement("Class not selected");
            LMETHODS.setModel(defaultModel);
        }
    }

    public void displayImage(String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);

        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(JMODEL.getWidth(), JMODEL.getHeight(), Image.SCALE_SMOOTH);

        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JMODEL.setIcon(scaledIcon);
    }

    public void showProgressBar (){
// Establecer colores de fondo y de la barra de progreso
        PROGRESSBAR.setForeground(Color.red); // Color de la barra de progreso
        PROGRESSBAR.setVisible(true);
    }

    public void hideProgressBar (){
        PROGRESSBAR.setVisible(false);
    }

    public void addOpenImageListener(ActionListener listener) {
        BIMAGE.addActionListener(listener);
    }

    public void addOpenDiagramListener(ActionListener listener) {
        BDIAGRAM.addActionListener(listener);
    }

    public void displayErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        CONTENEDOR = new javax.swing.JPanel();
        PARTICIPANT = new javax.swing.JLabel();
        PROCESS = new javax.swing.JLabel();
        PPROJECTS = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        LPROJECTS = new javax.swing.JList<>();
        CountProjects = new javax.swing.JLabel();
        PCLASSES = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        LCLASSES = new javax.swing.JList<>();
        CountClasses = new javax.swing.JLabel();
        PROGRESSBAR = new javax.swing.JProgressBar();
        jLabel4 = new javax.swing.JLabel();
        CONTAINER = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        VARIABLES = new javax.swing.JComboBox<>();
        LOAD = new javax.swing.JButton();
        BIMAGE = new javax.swing.JButton();
        BDIAGRAM = new javax.swing.JButton();
        HISTORY = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        JMODEL = new javax.swing.JLabel();
        PMETHODS = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        LMETHODS = new javax.swing.JList<>();
        CountMethods = new javax.swing.JLabel();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        CONTENEDOR.setBackground(new java.awt.Color(255, 255, 255));
        CONTENEDOR.setEnabled(false);
        CONTENEDOR.setFocusable(false);
        CONTENEDOR.setMaximumSize(new java.awt.Dimension(1133, 706));
        CONTENEDOR.setMinimumSize(new java.awt.Dimension(1133, 706));
        CONTENEDOR.setPreferredSize(new java.awt.Dimension(1133, 697));
        CONTENEDOR.setRequestFocusEnabled(false);
        CONTENEDOR.setVerifyInputWhenFocusTarget(false);

        PARTICIPANT.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        PARTICIPANT.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PARTICIPANT.setText("Select a variable");

        PROCESS.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        PROCESS.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PROCESS.setText("Select a variable");

        PPROJECTS.setBackground(new java.awt.Color(255, 255, 255));
        PPROJECTS.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PPROJECTS.setMinimumSize(new java.awt.Dimension(300, 40));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("PROJECTS");

        LPROJECTS.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        LPROJECTS.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        LPROJECTS.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Select a variable" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        LPROJECTS.setVisibleRowCount(100);
        jScrollPane1.setViewportView(LPROJECTS);

        CountProjects.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        CountProjects.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CountProjects.setText("0");
        CountProjects.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CountProjects.setEnabled(false);
        CountProjects.setFocusable(false);

        javax.swing.GroupLayout PPROJECTSLayout = new javax.swing.GroupLayout(PPROJECTS);
        PPROJECTS.setLayout(PPROJECTSLayout);
        PPROJECTSLayout.setHorizontalGroup(
            PPROJECTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PPROJECTSLayout.createSequentialGroup()
                .addGroup(PPROJECTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PPROJECTSLayout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(76, 76, 76)
                        .addComponent(CountProjects, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PPROJECTSLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        PPROJECTSLayout.setVerticalGroup(
            PPROJECTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PPROJECTSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PPROJECTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(CountProjects))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PCLASSES.setBackground(new java.awt.Color(255, 255, 255));
        PCLASSES.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PCLASSES.setMinimumSize(new java.awt.Dimension(300, 40));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("CLASSES");

        LCLASSES.setBorder(null);
        LCLASSES.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        LCLASSES.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Select a project" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(LCLASSES);

        CountClasses.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        CountClasses.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CountClasses.setText("0");
        CountClasses.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CountClasses.setEnabled(false);
        CountClasses.setFocusable(false);

        javax.swing.GroupLayout PCLASSESLayout = new javax.swing.GroupLayout(PCLASSES);
        PCLASSES.setLayout(PCLASSESLayout);
        PCLASSESLayout.setHorizontalGroup(
            PCLASSESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PCLASSESLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PCLASSESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addGroup(PCLASSESLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(117, 117, 117)
                        .addComponent(CountClasses, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        PCLASSESLayout.setVerticalGroup(
            PCLASSESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PCLASSESLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PCLASSESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(CountClasses))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PROGRESSBAR.setFocusable(false);
        PROGRESSBAR.setIndeterminate(true);
        PROGRESSBAR.setMaximumSize(new java.awt.Dimension(792, 9));
        PROGRESSBAR.setMinimumSize(new java.awt.Dimension(792, 9));
        PROGRESSBAR.setPreferredSize(new java.awt.Dimension(792, 9));
        PROGRESSBAR.setRequestFocusEnabled(false);
        PROGRESSBAR.setVerifyInputWhenFocusTarget(false);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("CONTAINER");
        jLabel4.setFocusable(false);

        CONTAINER.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        CONTAINER.setText("Select a project");

        VARIABLES.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        VARIABLES.setMaximumRowCount(100);
        VARIABLES.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select a version" }));
        VARIABLES.setBorder(null);
        VARIABLES.setName("Select a variable"); // NOI18N

        LOAD.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        LOAD.setText("Generate New Trace");
        LOAD.setBorder(null);
        LOAD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LOAD.setPreferredSize(new java.awt.Dimension(80, 26));

        BIMAGE.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        BIMAGE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image.png"))); // NOI18N
        BIMAGE.setText("Open Image");
        BIMAGE.setBorder(null);
        BIMAGE.setPreferredSize(new java.awt.Dimension(150, 35));

        BDIAGRAM.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        BDIAGRAM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/diagram.png"))); // NOI18N
        BDIAGRAM.setText("Open Diagram");
        BDIAGRAM.setBorder(null);
        BDIAGRAM.setPreferredSize(new java.awt.Dimension(150, 35));

        HISTORY.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        HISTORY.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select a Version" }));
        HISTORY.setSelectedItem("Select a Version");
        HISTORY.setBorder(null);
        HISTORY.setMinimumSize(new java.awt.Dimension(40, 31));
        HISTORY.setName(""); // NOI18N
        HISTORY.setPreferredSize(new java.awt.Dimension(40, 31));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Version");

        JMODEL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        JMODEL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images.png"))); // NOI18N
        JMODEL.setFocusable(false);
        JMODEL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        JMODEL.setInheritsPopupMenu(false);
        JMODEL.setMinimumSize(new java.awt.Dimension(792, 396));
        JMODEL.setPreferredSize(new java.awt.Dimension(792, 396));
        JMODEL.setRequestFocusEnabled(false);
        JMODEL.setVerifyInputWhenFocusTarget(false);

        PMETHODS.setBackground(new java.awt.Color(255, 255, 255));
        PMETHODS.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PMETHODS.setMinimumSize(new java.awt.Dimension(300, 40));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("METHODS");

        LMETHODS.setBorder(null);
        LMETHODS.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        LMETHODS.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Select a class" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        LMETHODS.setVisibleRowCount(100);
        jScrollPane3.setViewportView(LMETHODS);

        CountMethods.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        CountMethods.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CountMethods.setText("0");
        CountMethods.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CountMethods.setEnabled(false);
        CountMethods.setFocusable(false);

        javax.swing.GroupLayout PMETHODSLayout = new javax.swing.GroupLayout(PMETHODS);
        PMETHODS.setLayout(PMETHODSLayout);
        PMETHODSLayout.setHorizontalGroup(
            PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PMETHODSLayout.createSequentialGroup()
                .addGroup(PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PMETHODSLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3))
                    .addGroup(PMETHODSLayout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CountMethods, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        PMETHODSLayout.setVerticalGroup(
            PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PMETHODSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PMETHODSLayout.createSequentialGroup()
                        .addComponent(CountMethods)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout CONTENEDORLayout = new javax.swing.GroupLayout(CONTENEDOR);
        CONTENEDOR.setLayout(CONTENEDORLayout);
        CONTENEDORLayout.setHorizontalGroup(
            CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CONTENEDORLayout.createSequentialGroup()
                .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(CONTENEDORLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CONTENEDORLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(HISTORY, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(VARIABLES, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(PARTICIPANT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(PROCESS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(LOAD, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(CONTENEDORLayout.createSequentialGroup()
                                .addComponent(PPROJECTS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(PCLASSES, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(CONTENEDORLayout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(27, 27, 27)
                                        .addComponent(CONTAINER)))
                                .addGap(55, 55, 55)
                                .addComponent(PMETHODS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(CONTENEDORLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PROGRESSBAR, javax.swing.GroupLayout.PREFERRED_SIZE, 792, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(CONTENEDORLayout.createSequentialGroup()
                                .addComponent(JMODEL, javax.swing.GroupLayout.PREFERRED_SIZE, 792, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(81, 81, 81)
                                .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(BIMAGE, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(BDIAGRAM, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(14, 14, 14)))
                .addGap(7, 7, 7))
        );
        CONTENEDORLayout.setVerticalGroup(
            CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CONTENEDORLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CONTENEDORLayout.createSequentialGroup()
                        .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(HISTORY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(VARIABLES))
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CONTENEDORLayout.createSequentialGroup()
                        .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(PROCESS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PARTICIPANT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CONTENEDORLayout.createSequentialGroup()
                        .addComponent(LOAD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CONTENEDORLayout.createSequentialGroup()
                        .addGap(149, 149, 149)
                        .addComponent(BIMAGE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(51, 51, 51)
                        .addComponent(BDIAGRAM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(150, 150, 150))
                    .addGroup(CONTENEDORLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(JMODEL, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(PROGRESSBAR, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CONTENEDORLayout.createSequentialGroup()
                        .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(CONTENEDORLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(CONTAINER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(190, 190, 190))
                    .addGroup(CONTENEDORLayout.createSequentialGroup()
                        .addGroup(CONTENEDORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(PMETHODS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PCLASSES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PPROJECTS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        HISTORY.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CONTENEDOR, javax.swing.GroupLayout.PREFERRED_SIZE, 1132, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CONTENEDOR, javax.swing.GroupLayout.PREFERRED_SIZE, 705, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static Traceability instance;

    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Traceability.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>


        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (instance==null){
                    instance = new Traceability();
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton BDIAGRAM;
    public javax.swing.JButton BIMAGE;
    public javax.swing.JLabel CONTAINER;
    public javax.swing.JPanel CONTENEDOR;
    public javax.swing.JLabel CountClasses;
    public javax.swing.JLabel CountMethods;
    public javax.swing.JLabel CountProjects;
    public javax.swing.JComboBox HISTORY;
    public javax.swing.JLabel JMODEL;
    public javax.swing.JList<String> LCLASSES;
    public javax.swing.JList<String> LMETHODS;
    public javax.swing.JButton LOAD;
    public javax.swing.JList<String> LPROJECTS;
    public javax.swing.JLabel PARTICIPANT;
    public javax.swing.JPanel PCLASSES;
    public javax.swing.JPanel PMETHODS;
    private javax.swing.JPanel PPROJECTS;
    private javax.swing.JLabel PROCESS;
    private javax.swing.JProgressBar PROGRESSBAR;
    public javax.swing.JComboBox<String> VARIABLES;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    public javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
