package com.Trazability.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ProcessDAO {
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    public ProcessDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertProcess(String process, String model, String path) {
        try {
            String sql = "INSERT INTO process (process_name,model_name,path) VALUES (?,?,?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, process);
            ps.setString(2, model);
            ps.setString(3, path);
            int filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchProcess(String name, String model, String path) {
        try {
            String sql = "SELECT id_process FROM process WHERE process_name = ? AND model_name = ? AND path = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, model);
            ps.setString(3, path);

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

    public Map<String, String> searchProcessByVariableId(int id_variable) {

        Map<String, String> result = new HashMap<>();
        try {
            // Consultar la tabla 'used_by_element' para obtener el 'id_element' a partir del 'id_variable'
            String elementQuery = "SELECT id_element FROM used_by_element WHERE id_variable = ?";
            PreparedStatement elementPs = connection.prepareStatement(elementQuery);
            elementPs.setInt(1, id_variable);
            ResultSet elementRs = elementPs.executeQuery();

            if (elementRs.next()) {
                int elementId = elementRs.getInt("id_element");

                // Consultar la tabla 'element' para obtener el 'id_process' a partir del 'id_element'
                String processQuery = "SELECT id_process FROM element WHERE id_element = ?";
                PreparedStatement processPs = connection.prepareStatement(processQuery);
                processPs.setInt(1, elementId);
                ResultSet processRs = processPs.executeQuery();

                if (processRs.next()) {
                    int processId = processRs.getInt("id_process");

                    // Consultar la tabla 'process' para obtener el 'process_name' a partir del 'id_process'
                    String processInfoQuery = "SELECT process_name, model_name FROM process WHERE id_process = ?";
                    PreparedStatement processInfoPs = connection.prepareStatement(processInfoQuery);
                    processInfoPs.setInt(1, processId);
                    ResultSet processInfoRs = processInfoPs.executeQuery();

                    if (processInfoRs.next()) {
                        result.put("process_name", processInfoRs.getString("process_name"));
                        result.put("model_name", processInfoRs.getString("model_name"));
                    } else {
                        // Puedes manejar la falta de resultados según tus necesidades
                        result.put("error", "Datos no encontrados para el ID de variable proporcionado");
                    }
                } else {
                    result.put("error", "ID de proceso no encontrado en la tabla 'element'");
                }
            } else {
                result.put("error", "ID de elemento no encontrado en la tabla 'used_by_element'");
            }
        } catch (SQLException e) {
            result.put("error", "Error al realizar la búsqueda");
        }
        return result;
    }
}
