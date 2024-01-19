package Interfaces;

import com.Trazability.Color.BpmnColor;
import com.Trazability.DataBase.Connections;
import com.Trazability.Main;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

public class Traceability extends javax.swing.JFrame {

    private Connections con = new Connections();
    private int projectId, selectedVariableId;

    public Traceability() {
        initComponents();
        initializeFrame();
                
    }

    private void initializeFrame() {
        this.setResizable(false);
        this.setTitle("TRACEABILITY");
        loadVariableNames(); // Cargar nombres de variables al iniciar
        addVariableSelectionListener(); // Agrega proyectos según la variable
        getProjectSelectionListener(); // Obtener el id según el proyecto
        getMethodSelectionListener(); // Obtener el id según la clase
        loadData();
        loadImage();
    }

    private void loadVariableNames() {
        try {
            List<String> variableNames = con.getAllVariableNames();

            // Agregar la opción predeterminada
            variableNames.add(0, "Choose a variable");

            if (!variableNames.isEmpty()) {
                updateComboBox(VARIABLES, variableNames);
            } else {
                throw new RuntimeException("No se encontraron nombres de variables.");
            }
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    private void updateComboBox(JComboBox<String> comboBox, List<String> items) {
        comboBox.removeAllItems();
        items.forEach(comboBox::addItem);
    }

    private void addVariableSelectionListener() {
        VARIABLES.addActionListener(e -> {
            try {
                handleVariableSelection();
            } catch (IOException ex) {
                handleException(ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Traceability.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void handleVariableSelection() throws IOException, InterruptedException {
        String selectedVariable = getSelectedVariable();
        selectedVariableId = con.searchVariableByName(selectedVariable);

        List<String> projectNames = con.searchProjectByVariableId(selectedVariableId);
        String processName = con.searchProcessByVariableId(selectedVariableId);
        String participant = new BpmnColor().findParticipantName();

        updateProjectsList(projectNames);
        updateProcessName(processName);
        updateParticipant(participant);

        // Restablecer a cero los campos de conteo en caso de que no haya proyectos
        if (projectNames == null || projectNames.isEmpty()) {
            CountProjects.setText("0");
            CountClasses.setText("0");
            CountMethods.setText("0");
        }

        getUsedElement(selectedVariableId);
    }

    private String getSelectedVariable() {
        return (String) VARIABLES.getSelectedItem();
    }

    private void updateProjectsList(List<String> projectNames) {
        if (projectNames != null && !projectNames.isEmpty()) {
            DefaultListModel<String> model = new DefaultListModel<>();
            projectNames.forEach(model::addElement);
            LPROJECTS.setModel(model);
            CountProjects.setText(Integer.toString(projectNames.size()));
        } else {
            DefaultListModel<String> defaultModel = new DefaultListModel<>();
            defaultModel.addElement("No projects found for this variable.");
            LPROJECTS.setModel(defaultModel);
            CountProjects.setText("0");
        }
    }

    private void updateProcessName(String processName) {
        if (processName != null && !processName.isEmpty()) {
            PROCESS.setText(processName+".bpmn");
        } else {
            PROCESS.setText("Process name not found or error in the search.");
        }
    }

    private void updateParticipant(String participant) {
        if (participant != null && !participant.isEmpty()) {
            PARTICIPANT.setText(participant);
        }
    }

    private void handleException(Exception e) {
        Logger.getLogger(Traceability.class.getName()).log(Level.SEVERE, null, e);
    }

    private void getProjectSelectionListener() {
        LPROJECTS.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleProjectSelection();
            }
        });
    }

    private void handleProjectSelection() {
        String selectedProject = LPROJECTS.getSelectedValue();
        projectId = con.searchProject(selectedProject);

        String containerName = con.searchContainerName(projectId,selectedVariableId);
        CONTAINER.setText(containerName != null && !containerName.isEmpty() ? containerName : "variable not selected");

        List<String> classNames = con.searchClassById(projectId,selectedVariableId);
        updateList(LCLASSES, classNames, CountClasses, "Project not selected");

        // Restablecer a cero los campos de conteo en caso de que no haya Clases
        if (classNames == null || classNames.isEmpty()) {
            CountClasses.setText("0");
            CountMethods.setText("0");
        }
    }

    private void getMethodSelectionListener() {
        LCLASSES.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleClassSelection();
            }
        });
    }

    private void handleClassSelection() {
        String selectedClass = LCLASSES.getSelectedValue();
        int classId = con.searchClass(selectedClass);

        List<String> methodNames = con.searchMethodById(classId,selectedVariableId);
        updateList(LMETHODS, methodNames, CountMethods, "Class not selected");

        // Restablecer a cero los campos de conteo en caso de que no haya Metodos
        if (methodNames == null || methodNames.isEmpty()) {
            CountMethods.setText("0");
        }
    }

    private void updateList(JList<String> list, List<String> items, JLabel countLabel, String emptyMessage) {
        if (items != null && !items.isEmpty()) {
            DefaultListModel<String> model = new DefaultListModel<>();
            items.forEach(model::addElement);
            list.setModel(model);
            countLabel.setText(Integer.toString(items.size()));
        } else {
            DefaultListModel<String> defaultModel = new DefaultListModel<>();
            defaultModel.addElement(emptyMessage);
            list.setModel(defaultModel);
        }
    }

    private void getUsedElement(int selectedVariableId) throws IOException, InterruptedException {
        BpmnColor modifier = new BpmnColor();
        if (selectedVariableId <= 0) {
            System.err.println("Error: ID de variable no válido.");
            return;
        }

        List<String> usedElementNames = con.searchElementsUsed(selectedVariableId);

        if (!usedElementNames.isEmpty() && !usedElementNames.get(0).equals("Elemento no encontrado")) {
            modifier.modifyActivityColors(usedElementNames);
        } else {
            System.out.println("No se encontraron elementos usados para la variable con ID " + selectedVariableId);
        }
    }

    private void loadData() {
        LOAD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Llamar al método main de la clase Main
                    Main.main(new String[]{});

                    // Recargar datos en la instancia actual
                    loadVariableNames();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void loadImage() {
        // Ruta de la imagen
        String rutaImagen = "C:\\Users\\SOPORTES JPVM\\Desktop\\MSGF-Test.bpmn.png";

        // Crear un ImageIcon desde la imagen en la ruta
        ImageIcon icono = new ImageIcon(rutaImagen);

        // Obtener la imagen del ImageIcon
        Image imagen = icono.getImage();

        // Escalar la imagen para que se ajuste al tamaño del JLabel
        Image imagenEscalada = imagen.getScaledInstance(JMODEL.getWidth(), JMODEL.getHeight(), Image.SCALE_SMOOTH);

        // Crear un nuevo ImageIcon con la imagen escalada
        ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);

        // Asignar el ImageIcon al icono del JLabel (asumiendo que JMODEL es un JLabel)
        JMODEL.setIcon(iconoEscalado);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
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
        PMETHODS = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        LMETHODS = new javax.swing.JList<>();
        CountMethods = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        CONTAINER = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        VARIABLES = new javax.swing.JComboBox<>();
        LOAD = new javax.swing.JButton();
        JMODEL = new javax.swing.JLabel();

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        PARTICIPANT.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        PARTICIPANT.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PARTICIPANT.setText("Select a variable");

        PROCESS.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        PROCESS.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        PROCESS.setText("Select a variable");

        PPROJECTS.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("PROJECTS");

        LPROJECTS.setBackground(new java.awt.Color(242, 242, 242));
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(72, 72, 72)
                .addComponent(CountProjects, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(PPROJECTSLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
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

        PCLASSES.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("CLASSES");

        LCLASSES.setBackground(new java.awt.Color(242, 242, 242));
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
            .addGroup(PCLASSESLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(70, 70, 70)
                .addComponent(CountClasses, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(PCLASSESLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        PCLASSESLayout.setVerticalGroup(
            PCLASSESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PCLASSESLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PCLASSESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(CountClasses))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PMETHODS.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("METHODS");

        LMETHODS.setBackground(new java.awt.Color(242, 242, 242));
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(73, 73, 73)
                .addComponent(CountMethods, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(PMETHODSLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        PMETHODSLayout.setVerticalGroup(
            PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PMETHODSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(CountMethods))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("CONTAINER");
        jLabel4.setFocusable(false);

        CONTAINER.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        CONTAINER.setText("Select a project");

        VARIABLES.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        VARIABLES.setMaximumRowCount(100);
        VARIABLES.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select a variable" }));
        VARIABLES.setName("Select a variable"); // NOI18N

        LOAD.setText("Generate New Trace");
        LOAD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LOAD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LOADActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(JMODEL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(PPROJECTS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(CONTAINER, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(PCLASSES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32)
                                .addComponent(PMETHODS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(VARIABLES, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(PARTICIPANT, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PROCESS, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LOAD)
                        .addGap(7, 7, 7)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PARTICIPANT)
                    .addComponent(PROCESS)
                    .addComponent(VARIABLES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LOAD, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CONTAINER)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PCLASSES, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(PPROJECTS, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PMETHODS, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JMODEL, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void LOADActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LOADActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LOADActionPerformed

    public static void main(String args[]) {
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Traceability.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Traceability.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Traceability.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Traceability.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

//        com.Trazability.Main.main(args);

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run(){
                new Traceability().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel CONTAINER;
    public javax.swing.JLabel CountClasses;
    public javax.swing.JLabel CountMethods;
    public javax.swing.JLabel CountProjects;
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
    public javax.swing.JComboBox<String> VARIABLES;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel4;
    public javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
