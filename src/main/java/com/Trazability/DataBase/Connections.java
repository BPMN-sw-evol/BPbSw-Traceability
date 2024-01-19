package com.Trazability.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Connections {

    private Connection conexion;
    private PreparedStatement ps;
    private ResultSet rs;

    public Connections() {
        String url = "jdbc:mysql://localhost:3306/test_traceability";
        String usuario = "root";
        String contraseña = "mysql";

        try {
            conexion = DriverManager.getConnection(url, usuario, contraseña);
            if (conexion != null) {
                System.out.println("Conexión exitosa a la base de datos MySQL");
            }

        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }

    }

    public int createHistory(String name) {
        try {
            String sql = "INSERT INTO history (name,date) VALUES (?,?)";
            java.sql.Timestamp date = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now());
            date.setNanos(0);
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            ps.setTimestamp(2, date);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
            int i = searchHistory(name,date);
            return i;
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
            return -1;
        }
    }

    public int searchHistory(String name, java.sql.Timestamp date){
        try {
            String sql = "SELECT id_history FROM history WHERE name = ? AND date = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            ps.setTimestamp(2, date);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_history");
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertVariable(String name,int history){
        try {
            String sql = "INSERT INTO variable (variable_name,id_history) VALUES (?,?)";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, history);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchVariable(String name,int history){
        try {
            String sql = "SELECT id_variable FROM variable WHERE variable_name = ? AND id_history = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, history);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_variable");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertContainer(String name,int id_project){
        try {
            String sql = "INSERT INTO data_container (name_container,id_project) VALUES (?,?)";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, id_project);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchContainer(String name,int id_project){
        try {
            String sql = "SELECT id_data_container FROM data_container WHERE name_container = ? AND id_project = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, id_project);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_data_container");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertContainedIn(int id_variable,int id_container){
        try {
            String sql = "INSERT INTO contained_in (id_variable,id_data_container) VALUES (?,?)";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_variable);
            ps.setInt(2, id_container);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchContainedIn(int id_variable, int id_container){
        try {
            String sql = "SELECT id_contained_in FROM contained_in WHERE id_variable = ? AND id_data_container = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_variable);
            ps.setInt(2, id_container);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_contained_in");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertProject(String name){
        try {
            String sql = "INSERT INTO project (name_project) VALUES (?)";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchProject(String name){
        try {
            String sql = "SELECT id_project FROM project WHERE name_project = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_project");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertClass(int id_project,String name){
        try {
            String sql = "INSERT INTO class (id_project,name_class) VALUES (?,?)";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_project);
            ps.setString(2, name);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchClass(String name, int id_project){
        try {
            String sql = "SELECT id_class FROM class WHERE name_class = ? AND id_project = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, id_project);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_class");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }
    
    public int searchClass(String name){
        try {
            String sql = "SELECT id_class FROM class WHERE name_class = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_class");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertMethod(int id_class,String name){
        try {
            String sql = "INSERT INTO method (id_class,name_method) VALUES (?,?)";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_class);
            ps.setString(2, name);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchMethod(int id_class, String name){
        try {
            String sql = "SELECT id_method FROM method WHERE name_method = ? AND id_class = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, id_class);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_method");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertMethodUsed(int id_variable,int id_method,boolean modify){
        try {
            String sql = "INSERT INTO used_by_method (id_variable,id_method,modify) VALUES (?,?,?)";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_variable);
            ps.setInt(2, id_method);
            ps.setBoolean(3, modify);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchMethodUsed(int id_variable){
        try {
            String sql = "SELECT id_used_by_method FROM used_by_method WHERE id_variable = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_variable);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_used_by_method");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertProcess(String process){
        try {
            String sql = "INSERT INTO process (process_name) VALUES (?)";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, process);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchProcess(String name){
        try {
            String sql = "SELECT id_process FROM process WHERE process_name = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_process");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertElementType(String name){
        try {
            String sql = "INSERT INTO element_type (element_type_name) VALUES (?)";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchElementType(String name){
        try {
            String sql = "SELECT id_element_type FROM element_type WHERE element_type_name = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_element_type");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertElement(int id_element_type, String name, String lane, int id_process){
        try {
            String sql = "INSERT INTO element (id_element_type,element_name,lane,id_process) VALUES (?,?,?,?)";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_element_type);
            ps.setString(2, name);
            ps.setString(3, lane);
            ps.setInt(4, id_process);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchElement(String name){
        try {
            String sql = "SELECT id_element FROM element WHERE element_name = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_element");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public void insertElementUsed(int id_variable,int id_element,String first){
        try {
            String sql = "INSERT INTO used_by_element (id_variable,id_element,used_first) VALUES (?,?,?)";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_variable);
            ps.setInt(2, id_element);
            ps.setString(3, first);
            int filasAfectadas = ps.executeUpdate();
            System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchElementUsed(int id_variable){
        try {
            String sql = "SELECT id_used_by_element FROM used_by_element WHERE id_variable = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_variable);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_used_by_element");
            } else {
                return -1; // El curso no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    // -------------------------------------------------
    public int searchVariableByName(String name) {
        try {
            String sql = "SELECT id_variable FROM variable WHERE variable_name = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_variable");
            } else {
                return -1; // El elemento no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }

    public List<String> getAllVariableNames() {
        List<String> variableNames = new ArrayList<>();
        try {
            String sql = "SELECT variable_name FROM variable";
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String variableName = rs.getString("variable_name");
                variableNames.add(variableName);
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
        } finally {
            // Cerrar recursos (ps, rs) aquí si es necesario
        }
        return variableNames;
    }

    public String searchContainerName(int id_project,int id_variable) {
        String container = "";
        try {
            String sql = "SELECT id_data_container FROM contained_in WHERE id_variable = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_variable);
            rs = ps.executeQuery();

            while(rs.next()) {
                int id_container = rs.getInt("id_data_container");
                sql = "SELECT name_container FROM data_container WHERE id_data_container = ? AND id_project = ?";
                PreparedStatement ps1 = conexion.prepareStatement(sql);
                ps1.setInt(1, id_container);
                ps1.setInt(2, id_project);
                ResultSet rs1 = ps1.executeQuery();
                
                if(rs1.next()){
                    container = rs1.getString("name_container");
                }
            }
            
            return container;
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
            return "Error al realizar la búsqueda";
        }
    }

    public List<String> searchProjectByVariableId(int variableId) {
        List<String> projectNames = new ArrayList<>();

        try {
            // Consultar la tabla 'contained_in' para obtener el 'id_data_container' a partir del 'id' de la variable
            String containerQuery = "SELECT id_data_container FROM contained_in WHERE id_variable = ?";
            PreparedStatement containerPs = conexion.prepareStatement(containerQuery);
            containerPs.setInt(1, variableId);
            ResultSet containerRs = containerPs.executeQuery();

            while (containerRs.next()) {
                int dataContainerId = containerRs.getInt("id_data_container");

                // Consultar la tabla 'data_container' para obtener el 'id_project' a partir del 'id_data_container'
                String dataContainerQuery = "SELECT id_project FROM data_container WHERE id_data_container = ?";
                PreparedStatement dataContainerPs = conexion.prepareStatement(dataContainerQuery);
                dataContainerPs.setInt(1, dataContainerId);
                ResultSet dataContainerRs = dataContainerPs.executeQuery();

                if (dataContainerRs.next()) {
                    int projectId = dataContainerRs.getInt("id_project");

                    // Consultar la tabla 'project' para obtener el 'name_project' a partir del 'id_project'
                    String projectQuery = "SELECT name_project FROM project WHERE id_project = ?";
                    PreparedStatement projectPs = conexion.prepareStatement(projectQuery);
                    projectPs.setInt(1, projectId);
                    ResultSet projectRs = projectPs.executeQuery();

                    if (projectRs.next()) {
                        String projectName = projectRs.getString("name_project");
                        projectNames.add(projectName);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
        }

        return projectNames;
    }

    public List<String> searchClassById(int id_project, int id_variable) {
        List<String> classNames = new ArrayList<>();
        
        try {
            String sql = "SELECT id_method FROM used_by_method WHERE id_variable = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_variable);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id_method = rs.getInt("id_method");
                sql = "SELECT id_class FROM method WHERE id_method = ?";
                PreparedStatement ps1 = conexion.prepareStatement(sql);
                ps1.setInt(1, id_method);
                ResultSet rs1 = ps1.executeQuery();
                
                if(rs1.next()){
                    int id_class = rs1.getInt("id_class");
                    sql = "SELECT name_class FROM class WHERE id_class = ? AND id_project = ?";
                    PreparedStatement ps2 = conexion.prepareStatement(sql);
                    ps2.setInt(1, id_class);
                    ps2.setInt(2, id_project);
                    ResultSet rs2 = ps2.executeQuery();
                    
                    if(rs2.next()){
                        String className = rs2.getString("name_class");
                        if(!classNames.contains(className)){
                            classNames.add(className);
                        }
                        
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
        }

        return classNames;
    }

    public List<String> searchMethodById(int id_class,int id_variable) {
        List<String> methodNames = new ArrayList<>();

        try {
            String sql = "SELECT id_method FROM used_by_method WHERE id_variable = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_variable);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id_method = rs.getInt("id_method");
                sql = "SELECT name_method FROM method WHERE id_method = ? AND id_class = ?";
                PreparedStatement ps1 = conexion.prepareStatement(sql);
                ps1.setInt(1, id_method);
                ps1.setInt(2, id_class);
                ResultSet rs1 = ps1.executeQuery();
                
                if(rs1.next()){
                    String methodName = rs1.getString("name_method");
                    methodNames.add(methodName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
        }

        return methodNames;
    }

    public String searchProcessByVariableId(int id_variable) {
        try {
            // Consultar la tabla 'used_by_element' para obtener el 'id_element' a partir del 'id_variable'
            String elementQuery = "SELECT id_element FROM used_by_element WHERE id_variable = ?";
            PreparedStatement elementPs = conexion.prepareStatement(elementQuery);
            elementPs.setInt(1, id_variable);
            ResultSet elementRs = elementPs.executeQuery();

            if (elementRs.next()) {
                int elementId = elementRs.getInt("id_element");

                // Consultar la tabla 'element' para obtener el 'id_process' a partir del 'id_element'
                String processQuery = "SELECT id_process FROM element WHERE id_element = ?";
                PreparedStatement processPs = conexion.prepareStatement(processQuery);
                processPs.setInt(1, elementId);
                ResultSet processRs = processPs.executeQuery();

                if (processRs.next()) {
                    int processId = processRs.getInt("id_process");

                    // Consultar la tabla 'process' para obtener el 'process_name' a partir del 'id_process'
                    String processNameQuery = "SELECT process_name FROM process WHERE id_process = ?";
                    PreparedStatement processNamePs = conexion.prepareStatement(processNameQuery);
                    processNamePs.setInt(1, processId);
                    ResultSet processNameRs = processNamePs.executeQuery();

                    if (processNameRs.next()) {
                        return processNameRs.getString("process_name");
                    } else {
                        return "Nombre de proceso no encontrado";
                    }
                } else {
                    return "ID de proceso no encontrado en la tabla 'element'";
                }
            } else {
                return "ID de elemento no encontrado en la tabla 'used_by_element'";
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
            return "Error al realizar la búsqueda";
        }
    }

    public List<String> searchElementsUsed(int id_variable) {
        List<String> usedElementNames = new ArrayList<>();

        try {
            // Consultar la tabla 'used_by_element' para obtener los IDs de los elementos
            String sql = "SELECT id_element FROM used_by_element WHERE id_variable = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id_variable);
            rs = ps.executeQuery();

            // Iterar sobre los resultados obtenidos
            while (rs.next()) {
                int elementId = rs.getInt("id_element");

                // Consultar la tabla 'element' para obtener el nombre del elemento
                String elementName = getElementNameById(elementId);

                if (elementName != null) {
                    usedElementNames.add(elementName);
                }
            }

            return usedElementNames;
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
            return Collections.emptyList(); // Devolver una lista vacía en caso de error
        }
    }

// Método adicional para obtener el nombre del elemento por su ID
    private String getElementNameById(int elementId) {
        try {
            String sql = "SELECT element_name FROM element WHERE id_element = ?";
            PreparedStatement elementPs = conexion.prepareStatement(sql);
            elementPs.setInt(1, elementId);
            ResultSet elementRs = elementPs.executeQuery();

            if (elementRs.next()) {
                return elementRs.getString("element_name");
            } else {
                return null; // El elemento no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el nombre del elemento: " + e);
            return null;
        }
    }

}
