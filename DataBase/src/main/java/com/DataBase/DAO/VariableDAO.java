package com.DataBase.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VariableDAO {
    private final Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    public VariableDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertVariable(String name, int history) {
        try {
            String sql = "INSERT INTO variable (variable_name,id_history) VALUES (?,?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, history);
            int filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchVariable(String name, int history) {
        try {
            String sql = "SELECT id_variable FROM variable WHERE variable_name = ? AND id_history = ?";
            ps = connection.prepareStatement(sql);
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

    public List<String> getAllVariableNames(int history) {
        List<String> variableNames = new ArrayList<>();
        try {
            String sql = "SELECT variable_name FROM variable where id_history = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, history);
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

    public int searchVariableByName(String name) {
        try {
            String sql = "SELECT id_variable FROM variable WHERE variable_name = ?";
            ps = connection.prepareStatement(sql);
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

}
