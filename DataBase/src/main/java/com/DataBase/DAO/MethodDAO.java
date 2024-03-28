package com.DataBase.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MethodDAO {
    private final Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    public MethodDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertMethod(int id_class, String name) {
        try {
            String sql = "INSERT INTO method (id_class,name_method) VALUES (?,?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id_class);
            ps.setString(2, name);
            int filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchMethod(int id_class, String name) {
        try {
            String sql = "SELECT id_method FROM method WHERE name_method = ? AND id_class = ?";
            ps = connection.prepareStatement(sql);
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

    public void insertMethodUsed(int id_variable, int id_method, boolean modify) {
        try {
            String sql = "INSERT INTO used_by_method (id_variable,id_method,modify) VALUES (?,?,?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id_variable);
            ps.setInt(2, id_method);
            ps.setBoolean(3, modify);
            int filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public List<String> searchMethodById(int id_class, int id_variable) {
        List<String> methodNames = new ArrayList<>();

        try {
            String sql = "SELECT id_method FROM used_by_method WHERE id_variable = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id_variable);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id_method = rs.getInt("id_method");
                String sql1 = "SELECT name_method FROM method WHERE id_method = ? AND id_class = ?";
                PreparedStatement ps1 = connection.prepareStatement(sql1);
                ps1.setInt(1, id_method);
                ps1.setInt(2, id_class);
                ResultSet rs1 = ps1.executeQuery();

                if (rs1.next()) {
                    String methodName = rs1.getString("name_method");
                    methodNames.add(methodName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
        }

        return methodNames;
    }
}
