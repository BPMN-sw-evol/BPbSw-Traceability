package Interfaces;

import com.Traza.Controller.TraceabilityController;
import com.Traza.LoadTraza.DataLoad;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

    public void resetVariablesComboBox() {
        VARIABLES.removeAllItems();
        VARIABLES.addItem("Choose a version");
    }

    // Metodos para obtener el elemento
    public int getSelectedHistory() {
        return (HISTORY.getSelectedItem() != null) ? ((Integer) HISTORY.getSelectedItem()) : 0;
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
        BIMAGE.setVisible(visible);
    }

    public void setBDiagramVisible(boolean visible) {
        BDIAGRAM.setVisible(visible);
    }

    // Metodos para actualizar los diferentes campos en la vista
    public void updateComboBoxHistory(List<Integer> items) {
        HISTORY.removeAllItems();
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
        int width = JMODEL.getWidth();
        int height = JMODEL.getHeight();

        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JMODEL.setIcon(scaledIcon);
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

        JFrame jFrame1 = new JFrame();
        JPanel jPanel1 = new JPanel();
        PARTICIPANT = new javax.swing.JLabel();
        PROCESS = new javax.swing.JLabel();
        JPanel PPROJECTS = new JPanel();
        jLabel6 = new javax.swing.JLabel();
        JScrollPane jScrollPane1 = new JScrollPane();
        LPROJECTS = new javax.swing.JList<>();
        CountProjects = new javax.swing.JLabel();
        PCLASSES = new javax.swing.JPanel();
        JLabel jLabel7 = new JLabel();
        JScrollPane jScrollPane2 = new JScrollPane();
        LCLASSES = new javax.swing.JList<>();
        CountClasses = new javax.swing.JLabel();
        PMETHODS = new javax.swing.JPanel();
        JLabel jLabel8 = new JLabel();
        JScrollPane jScrollPane3 = new JScrollPane();
        LMETHODS = new javax.swing.JList<>();
        CountMethods = new javax.swing.JLabel();
        JLabel jLabel4 = new JLabel();
        CONTAINER = new javax.swing.JLabel();
        JSeparator jSeparator1 = new JSeparator();
        VARIABLES = new javax.swing.JComboBox<>();
        LOAD = new javax.swing.JButton();
        JMODEL = new javax.swing.JLabel();
        BIMAGE = new javax.swing.JButton();
        BDIAGRAM = new javax.swing.JButton();
        HISTORY = new javax.swing.JComboBox<>();
        JLabel jLabel1 = new JLabel();

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

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        PARTICIPANT.setFont(new java.awt.Font("Segoe UI", Font.BOLD | Font.ITALIC, 14)); // NOI18N
        PARTICIPANT.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PARTICIPANT.setText("Select a variable");

        PROCESS.setFont(new java.awt.Font("Segoe UI", Font.BOLD | Font.ITALIC, 14)); // NOI18N
        PROCESS.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PROCESS.setText("Select a variable");

        PPROJECTS.setBackground(new java.awt.Color(255, 255, 255));
        PPROJECTS.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 18)); // NOI18N
        jLabel6.setText("PROJECTS");

        LPROJECTS.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        LPROJECTS.setFont(new java.awt.Font("Segoe UI", Font.ITALIC, 14)); // NOI18N
        LPROJECTS.setModel(new javax.swing.AbstractListModel<String>() {
            final String[] strings = { "Select a variable" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        LPROJECTS.setVisibleRowCount(100);
        jScrollPane1.setViewportView(LPROJECTS);

        CountProjects.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12)); // NOI18N
        CountProjects.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CountProjects.setText("0");
        CountProjects.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CountProjects.setEnabled(false);
        CountProjects.setFocusable(false);

        javax.swing.GroupLayout PPROJECTSLayout = new javax.swing.GroupLayout(PPROJECTS);
        PPROJECTS.setLayout(PPROJECTSLayout);
        PPROJECTSLayout.setHorizontalGroup(
            PPROJECTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PPROJECTSLayout.createSequentialGroup()
                .addGroup(PPROJECTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PPROJECTSLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(PPROJECTSLayout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                        .addGap(82, 82, 82)
                        .addComponent(CountProjects, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        PPROJECTSLayout.setVerticalGroup(
            PPROJECTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PPROJECTSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PPROJECTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CountProjects)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        PCLASSES.setBackground(new java.awt.Color(255, 255, 255));
        PCLASSES.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 18)); // NOI18N
        jLabel7.setText("CLASSES");

        LCLASSES.setBorder(null);
        LCLASSES.setFont(new java.awt.Font("Segoe UI", Font.ITALIC, 14)); // NOI18N
        LCLASSES.setModel(new javax.swing.AbstractListModel<String>() {
            final String[] strings = { "Select a project" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(LCLASSES);

        CountClasses.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12)); // NOI18N
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
                        .addGap(0, 175, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(117, 117, 117)
                        .addComponent(CountClasses, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        PCLASSESLayout.setVerticalGroup(
            PCLASSESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PCLASSESLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PCLASSESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(CountClasses))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        PMETHODS.setBackground(new java.awt.Color(255, 255, 255));
        PMETHODS.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 18)); // NOI18N
        jLabel8.setText("METHODS");

        LMETHODS.setBorder(null);
        LMETHODS.setFont(new java.awt.Font("Segoe UI", Font.ITALIC, 14)); // NOI18N
        LMETHODS.setModel(new javax.swing.AbstractListModel<String>() {
            final String[] strings = { "Select a class" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        LMETHODS.setVisibleRowCount(100);
        jScrollPane3.setViewportView(LMETHODS);

        CountMethods.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12)); // NOI18N
        CountMethods.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CountMethods.setText("0");
        CountMethods.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        CountMethods.setEnabled(false);
        CountMethods.setFocusable(false);

        javax.swing.GroupLayout PMETHODSLayout = new javax.swing.GroupLayout(PMETHODS);
        PMETHODS.setLayout(PMETHODSLayout);
        PMETHODSLayout.setHorizontalGroup(
            PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PMETHODSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PMETHODSLayout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                        .addComponent(CountMethods, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        PMETHODSLayout.setVerticalGroup(
            PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PMETHODSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CountMethods))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 18)); // NOI18N
        jLabel4.setText("CONTAINER");
        jLabel4.setFocusable(false);

        CONTAINER.setFont(new java.awt.Font("Segoe UI", Font.ITALIC, 14)); // NOI18N
        CONTAINER.setText("Select a project");

        VARIABLES.setFont(new java.awt.Font("Segoe UI", Font.PLAIN, 18)); // NOI18N
        VARIABLES.setMaximumRowCount(100);
        VARIABLES.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select a version" }));
        VARIABLES.setName("Select a variable"); // NOI18N

        LOAD.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14)); // NOI18N
        LOAD.setText("Generate New Trace");
        LOAD.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        LOAD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LOAD.setPreferredSize(new java.awt.Dimension(80, 26));

        JMODEL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        JMODEL.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/images.png")))); // NOI18N
        JMODEL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        JMODEL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        BIMAGE.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14)); // NOI18N
        BIMAGE.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/image.png")))); // NOI18N
        BIMAGE.setText("Open Image");
        BIMAGE.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        BIMAGE.setPreferredSize(new java.awt.Dimension(150, 35));

        BDIAGRAM.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14)); // NOI18N
        BDIAGRAM.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource("/diagram.png")))); // NOI18N
        BDIAGRAM.setText("Open Diagram");
        BDIAGRAM.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        BDIAGRAM.setPreferredSize(new java.awt.Dimension(150, 35));

        HISTORY.setFont(new java.awt.Font("Segoe UI", Font.PLAIN, 18)); // NOI18N
        HISTORY.setSelectedIndex(-1);
        HISTORY.setMinimumSize(new java.awt.Dimension(40, 31));
        HISTORY.setName(""); // NOI18N
        HISTORY.setPreferredSize(new java.awt.Dimension(40, 31));

        jLabel1.setFont(new java.awt.Font("Segoe UI", Font.PLAIN, 18)); // NOI18N
        jLabel1.setText("Version #");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addComponent(JMODEL, javax.swing.GroupLayout.PREFERRED_SIZE, 765, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BIMAGE, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BDIAGRAM, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HISTORY, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(VARIABLES, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(68, 68, 68)
                        .addComponent(PARTICIPANT, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(PROCESS, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(LOAD, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(PPROJECTS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CONTAINER, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(PCLASSES, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(36, 36, 36)
                        .addComponent(PMETHODS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(PROCESS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PARTICIPANT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LOAD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(VARIABLES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(HISTORY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(BIMAGE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(BDIAGRAM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(JMODEL, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CONTAINER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PCLASSES, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(PPROJECTS, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PMETHODS, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        HISTORY.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    public javax.swing.JLabel CountClasses;
    public javax.swing.JLabel CountMethods;
    public javax.swing.JLabel CountProjects;
    public javax.swing.JComboBox<Integer> HISTORY;
    public javax.swing.JLabel JMODEL;
    public javax.swing.JList<String> LCLASSES;
    public javax.swing.JList<String> LMETHODS;
    public javax.swing.JButton LOAD;
    public javax.swing.JList<String> LPROJECTS;
    public javax.swing.JLabel PARTICIPANT;
    public javax.swing.JPanel PCLASSES;
    public javax.swing.JPanel PMETHODS;
    private javax.swing.JLabel PROCESS;
    public javax.swing.JComboBox<String> VARIABLES;
    public javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables
}
