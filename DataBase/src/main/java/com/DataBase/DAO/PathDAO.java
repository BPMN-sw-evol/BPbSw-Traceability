package com.DataBase.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PathDAO {
    private Connection connection;

    public PathDAO(Connection connection) {
        this.connection = connection;
    }

    public String getModelBPMNPath(int id_variable) {
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
                    String pathQuery = "SELECT path FROM process WHERE id_process = ?";
                    PreparedStatement pathPs = connection.prepareStatement(pathQuery);
                    pathPs.setInt(1, processId);
                    ResultSet pathRs = pathPs.executeQuery();

                    if (pathRs.next()) {
                        return pathRs.getString("path");
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
}
