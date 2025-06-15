package com.DataBase.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    public ClassDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertClass(int id_project, String name) {
        try {
            String sql = "INSERT INTO class (id_project,name_class) VALUES (?,?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id_project);
            ps.setString(2, name);
            int filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchClass(String name, int id_project) {
        try {
            String sql = "SELECT id_class FROM class WHERE name_class = ? AND id_project = ?";
            ps = connection.prepareStatement(sql);
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

    public int searchClass(String name) {
        try {
            String sql = "SELECT id_class FROM class WHERE name_class = ?";
            ps = connection.prepareStatement(sql);
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

    public List<String> searchClassById(int id_project, int id_variable) {
        List<String> classNames = new ArrayList<>();

        try {
            String sql = "SELECT id_method FROM used_by_method WHERE id_variable = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id_variable);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id_method = rs.getInt("id_method");
                sql = "SELECT id_class FROM method WHERE id_method = ?";
                PreparedStatement ps1 = connection.prepareStatement(sql);
                ps1.setInt(1, id_method);
                ResultSet rs1 = ps1.executeQuery();

                if (rs1.next()) {
                    int id_class = rs1.getInt("id_class");
                    String sql1 = "SELECT name_class FROM class WHERE id_class = ? AND id_project = ?";
                    PreparedStatement ps2 = connection.prepareStatement(sql1);
                    ps2.setInt(1, id_class);
                    ps2.setInt(2, id_project);
                    ResultSet rs2 = ps2.executeQuery();

                    if (rs2.next()) {
                        String className = rs2.getString("name_class");
                        if (!classNames.contains(className)) {
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
}
