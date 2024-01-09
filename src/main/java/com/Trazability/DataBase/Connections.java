package com.Trazability.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Connections{
    private Connection conexion;
    private PreparedStatement ps;
    private ResultSet rs;

    public Connections() {
        String url = "jdbc:mysql://localhost:3306/test_traceability";
        String usuario = "root";
        String contraseña = "mysql";

        try {
            conexion = DriverManager.getConnection(url, usuario, contraseña);
            if(conexion!=null){
                System.out.println("Conexión exitosa a la base de datos MySQL");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }

        
    }

    public int createHistory(String name) {
        try {
            String sql = "INSERT INTO history (name,date) VALUES (?,?)";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            int filasAfectadas = ps.executeUpdate();
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
            int i = searchHistory(name);
            return i;
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
            return -1;
        }
    }

    public int searchHistory(String name){
        try {
            String sql = "SELECT id_history FROM history WHERE name = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id_history");
            } else {
                return -1; // El elemento no se encontró
            }
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
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
                return -1; // El elemento no se encontró
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
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
                return -1; // El elemento no se encontró
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
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
                return -1; // El elemento no se encontró
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
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
                return -1; // El elemento no se encontró
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
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
                return -1; // El elemento no se encontró
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
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
                return -1; // El elemento no se encontró
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
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
                return -1; // El elemento no se encontró
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
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
                return -1; // El elemento no se encontró
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
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
                return -1; // El elemento no se encontró
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
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
                return -1; // El elemento no se encontró
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
            // System.out.println("Se insertaron " + filasAfectadas + " filas.");
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
                return -1; // El elemento no se encontró
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la busqueda: " + e);
            return -1;
        }
    }
}

