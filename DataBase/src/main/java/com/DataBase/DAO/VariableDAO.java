package com.DataBase.DAO;

import java.sql.*;
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

    public List<String> getAllVariableNames(Timestamp history) {
        List<String> variableNames = new ArrayList<>();
        int historyId = getHistoryIdByDate(history); // Obtener el id de history usando la fecha
        if (historyId != -1) { // Verificar si se encontró un id válido
            try {
                String sql = "SELECT variable_name FROM variable WHERE id_history = ?";
                ps = connection.prepareStatement(sql);
                ps.setInt(1, historyId);
                rs = ps.executeQuery();

                System.out.println("historyId: "+historyId);

                while (rs.next()) {
                    
                    String variableName = rs.getString("variable_name");
                    System.out.println("Variable name: "+variableName);
                    variableNames.add(variableName);
                }
            } catch (SQLException e) {
                System.err.println("Error al realizar la búsqueda: " + e);
            } finally {
                // Cerrar recursos (ps, rs) aquí si es necesario
            }
        }
        System.out.println("Variable names:"+variableNames);
        return variableNames;
    }

    private int getHistoryIdByDate(Timestamp historyDate) {
        int historyId = -1;
        try {
            String sql = "SELECT id_history FROM history WHERE date = ?";
            ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, historyDate);
            rs = ps.executeQuery();

            System.out.println("historyDate: "+historyDate);

            if (rs.next()) {
                System.out.println("historyId: "+rs.getInt("id_history"));
                historyId = rs.getInt("id_history");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el id de history: " + e);
        } finally {
            // Cerrar recursos (ps, rs) aquí si es necesario
        }
        return historyId;
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

    public List<Integer> getVariablesHistory(int id_history) {
        List<Integer> variables = new ArrayList<>();
        try {
            String sql = "SELECT id_variable FROM variable WHERE id_history = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id_history);
            rs = ps.executeQuery();

            while (rs.next()) {
                Integer variable = rs.getInt("id_variable");
                if (variable != null) {
                    variables.add(variable);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
        } finally {
            // Cerrar recursos (ps, rs) aquí si es necesario
        }
        return variables;
    }

    public boolean deleteVariable(int history) {
        try {
            String sql = "DELETE FROM variable WHERE id_history = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, history);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al realizar la eliminación: " + e);
            return false;
        }
    }

}
