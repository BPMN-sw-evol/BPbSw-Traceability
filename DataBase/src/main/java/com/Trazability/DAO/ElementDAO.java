package com.Trazability.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementDAO {
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    public ElementDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertElementType(String name) {
        try {
            String sql = "INSERT INTO element_type (element_type_name) VALUES (?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            int filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchElementType(String name) {
        try {
            String sql = "SELECT id_element_type FROM element_type WHERE element_type_name = ?";
            ps = connection.prepareStatement(sql);
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

    public void insertElement(int id_element_type, String name, String lane, int id_process) {
        try {
            String sql = "INSERT INTO element (id_element_type,element_name,lane,id_process) VALUES (?,?,?,?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id_element_type);
            ps.setString(2, name);
            ps.setString(3, lane);
            ps.setInt(4, id_process);
            int filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchElement(String name) {
        try {
            String sql = "SELECT id_element FROM element WHERE element_name = ?";
            ps = connection.prepareStatement(sql);
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

    public void insertElementUsed(int id_variable, int id_element, String first) {
        try {
            String sql = "INSERT INTO used_by_element (id_variable,id_element,used_first) VALUES (?,?,?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id_variable);
            ps.setInt(2, id_element);
            ps.setString(3, first);
            int filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchElementUsed(int id_variable) {
        try {
            String sql = "SELECT id_used_by_element FROM used_by_element WHERE id_variable = ?";
            ps = connection.prepareStatement(sql);
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

    public List<String> searchElementsUsed(int id_variable) {
        List<String> usedElementNames = new ArrayList<>();

        try {
            // Consultar la tabla 'used_by_element' para obtener los IDs de los elementos
            String sql = "SELECT id_element FROM used_by_element WHERE id_variable = ?";
            ps = connection.prepareStatement(sql);
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
            PreparedStatement elementPs = connection.prepareStatement(sql);
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
