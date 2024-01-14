package Interfaces;

import com.Trazability.Color.BpmnColorModifier;
import com.Trazability.DataBase.Connections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Traceability extends javax.swing.JFrame {

    private Connections con = new Connections();
    private int projectId, selectedVariableId;

    public Traceability() {
        initComponents();
        this.setResizable(false);
        this.setTitle("TRACEABILITY");
        loadVariableNames(); // Cargar nombres de variables al iniciar
        addVariableSelectionListener(); // Agrega proyectos segun la variable
        getProjectSelectionListener(); // Obtener el id segun el proyecto
        getMethodSelectionListener(); // Obtener el id segun la clase
    }

    private void loadVariableNames() {
        List<String> variableNames = con.getAllVariableNames(); // Obtener nombres de variables desde la base de datos

        if (variableNames != null && !variableNames.isEmpty()) {
            VARIABLES.removeAllItems(); // Limpiar elementos previos en el JComboBox

            // Agregar los nombres de las variables al JComboBox
            for (String name : variableNames) {
                VARIABLES.addItem(name);
            }
        } else {
            // Manejo de error si no se encuentran nombres de variables
            System.err.println("No se encontraron nombres de variables.");
        }
    }

    private void addVariableSelectionListener() {
        VARIABLES.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String selectedVariable = (String) VARIABLES.getSelectedItem(); // Obtener la variable seleccionada del combo box
                    
                    // Aquí deberías obtener el ID de la variable seleccionada, supongamos que lo guardaste en selectedVariableId
                    selectedVariableId = con.searchVariableByName(selectedVariable);
                    
                    // Llamar al método searchProjectByVariableId utilizando el ID de la variable seleccionada
                    List<String> projectNames = con.searchProjectByVariableId(selectedVariableId);
                    
                    String processName = con.searchProcessByVariableId(selectedVariableId);
                    
                    // Verificar si se encontraron nombres de proyecto
                    if (projectNames != null && !projectNames.isEmpty()) {
                        // Crear un modelo para la lista LPROJECTS y añadir los nombres de proyecto encontrados
                        DefaultListModel<String> model = new DefaultListModel<>();
                        for (String projectName : projectNames) {
                            model.addElement(projectName);
                        }
                        LPROJECTS.setModel(model); // Establecer el modelo en la lista LPROJECTS
                        
                        // Obtener la cantidad de proyectos y establecerla en CountProjects
                        int numProjects = projectNames.size();
                        CountProjects.setText(Integer.toString(numProjects)); // Mostrar solo el número de proyectos
                    } else {
                        // Manejar casos en los que no se encuentren nombres de proyecto o haya errores
                        System.err.println("Nombres de proyecto no encontrados o error en la búsqueda.");
                    }
                    
                    if (processName != null && !processName.isEmpty()) {
                        PROCESS.setText(processName);
                    } else {
                        // Manejar casos en los que no se encuentren nombres de proyecto o haya errores
                        System.err.println("Nombre de proceso no encontrado o error en la búsqueda.");
                    }
                    
                    // Llamar al método getUsedElement utilizando el ID de la variable seleccionada
                    getUsedElement(selectedVariableId);
                } catch (IOException ex) {
                    Logger.getLogger(Traceability.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void getProjectSelectionListener() {
        LPROJECTS.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedProject = LPROJECTS.getSelectedValue(); // Obtener el proyecto seleccionado de la lista

                    // Obtener el ID del proyecto seleccionado
                    projectId = con.searchProject(selectedProject);

                    // Obtener el nombre del contenedor usando el ID del proyecto
                    String containerName = con.searchContainerName(projectId);

                    if (containerName != null && !containerName.isEmpty()) {
                        // Mostrar el resultado en la variable CONTAINER
                        CONTAINER.setText(containerName);
                    } else {
                        CONTAINER.setText("variable not selected");
                    }

                    // Llamar al método searchClassById utilizando el ID del proyecto seleccionado
                    List<String> classNames = con.searchClassById(projectId);
                    if (classNames != null && !classNames.isEmpty()) {

                        DefaultListModel<String> model = new DefaultListModel<>();

                        for (String className : classNames) {
                            model.addElement(className);
                        }

                        LCLASSES.setModel(model); // Establecer el modelo en la lista LCLASSES

                        // Obtener la cantidad de proyectos y establecerla en CountMethods
                        int numClasses = classNames.size();
                        CountClasses.setText(Integer.toString(numClasses)); // Mostrar solo el número de proyectos

                    } else {
                        DefaultListModel<String> defaultModel = new DefaultListModel<>();
                        defaultModel.addElement("Project not selected");
                        LCLASSES.setModel(defaultModel);
                    }

                }
            }
        });
    }

    private void getMethodSelectionListener() {
        LCLASSES.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    String selectedClass = LCLASSES.getSelectedValue(); // Obtener la clase seleccionada
                    int classId = con.searchClass(selectedClass); // Obtener el ID de la clase

                    // Llamar al método searchMethodById utilizando el ID de la clase seleccionada
                    List<String> methodNames = con.searchMethodById(classId);

                    if (methodNames != null && !methodNames.isEmpty()) {

                        // Llamar al método para agregar los métodos a la lista LMETHODS
                        DefaultListModel<String> model = new DefaultListModel<>();

                        for (String methodName : methodNames) {
                            model.addElement(methodName);
                        }

                        LMETHODS.setModel(model); // Establecer el modelo en la lista LMETHODS

                        // Obtener la cantidad de proyectos y establecerla en CountMethods
                        int numMethods = methodNames.size();
                        CountMethods.setText(Integer.toString(numMethods)); // Mostrar solo el número de proyectos

                    } else {
                        DefaultListModel<String> defaultModel = new DefaultListModel<>();
                        defaultModel.addElement("Class not selected");
                        LMETHODS.setModel(defaultModel);
                    }
                }
            }
        });
    }

    private void getUsedElement(int selectedVariableId) throws IOException {
        
        // Llamar a la función para pintar las actividades
            BpmnColorModifier modifier = new BpmnColorModifier();
        // Verificar que selectedVariableId tiene un valor válido
        if (selectedVariableId <= 0) {
            System.err.println("Error: ID de variable no válido.");
            return;
        }

        // Llamar al método searchElementsUsed utilizando el ID de la variable seleccionada
        List<String> usedElementNames = con.searchElementsUsed(selectedVariableId);

        // Imprimir los resultados de la consulta
        if (!usedElementNames.isEmpty() && !usedElementNames.get(0).equals("Elemento no encontrado")) {
            System.out.println("Resultados de la consulta para la variable con ID " + selectedVariableId + ":");
            for (String name : usedElementNames) {
                System.out.println("Nombre usado por elemento: " + name);
                modifier.buscarIdsActividad(name);

            }
            
            modifier.modifyActivityColors(usedElementNames);
        } else {
            System.out.println("No se encontraron elementos usados para la variable con ID " + selectedVariableId);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
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
        PMODEL = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        VARIABLES = new javax.swing.JComboBox<>();

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

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Select a variable");

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
                .addGap(51, 51, 51)
                .addComponent(CountProjects, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(PPROJECTSLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        PPROJECTSLayout.setVerticalGroup(
            PPROJECTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PPROJECTSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PPROJECTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CountProjects)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(21, Short.MAX_VALUE))
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
                .addGap(43, 43, 43)
                .addComponent(CountMethods, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(PMETHODSLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        PMETHODSLayout.setVerticalGroup(
            PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PMETHODSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PMETHODSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(CountMethods))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("CONTAINER");
        jLabel4.setFocusable(false);

        CONTAINER.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        CONTAINER.setText("Select a project");

        PMODEL.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout PMODELLayout = new javax.swing.GroupLayout(PMODEL);
        PMODEL.setLayout(PMODELLayout);
        PMODELLayout.setHorizontalGroup(
            PMODELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 837, Short.MAX_VALUE)
        );
        PMODELLayout.setVerticalGroup(
            PMODELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 249, Short.MAX_VALUE)
        );

        VARIABLES.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        VARIABLES.setMaximumRowCount(100);
        VARIABLES.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select a variable" }));
        VARIABLES.setName("Select a variable"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(PMODEL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 2, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(VARIABLES, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(159, 159, 159)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(PROCESS, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(PPROJECTS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CONTAINER, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(PCLASSES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)))
                        .addComponent(PMETHODS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(PROCESS)
                    .addComponent(VARIABLES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PMODEL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            public void run() {
                new Traceability().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel CONTAINER;
    public javax.swing.JLabel CountClasses;
    public javax.swing.JLabel CountMethods;
    public javax.swing.JLabel CountProjects;
    public javax.swing.JList<String> LCLASSES;
    public javax.swing.JList<String> LMETHODS;
    public javax.swing.JList<String> LPROJECTS;
    public javax.swing.JPanel PCLASSES;
    public javax.swing.JPanel PMETHODS;
    public javax.swing.JPanel PMODEL;
    private javax.swing.JPanel PPROJECTS;
    private javax.swing.JLabel PROCESS;
    public javax.swing.JComboBox<String> VARIABLES;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel2;
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
