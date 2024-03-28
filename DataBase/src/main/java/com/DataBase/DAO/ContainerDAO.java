package com.DataBase.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContainerDAO {
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    public ContainerDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertContainer(String name, int id_project) {
        try {
            String sql = "INSERT INTO data_container (name_container,id_project) VALUES (?,?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, id_project);
            int filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchContainer(String name, int id_project) {
        try {
            String sql = "SELECT id_data_container FROM data_container WHERE name_container = ? AND id_project = ?";
            ps = connection.prepareStatement(sql);
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

    public int insertContainedIn(int id_variable, int id_container) {
        try {
            String sql = "INSERT INTO contained_in (id_variable,id_data_container) VALUES (?,?)";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id_variable);
            ps.setInt(2, id_container);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
        return -1;
    }

    public int searchContainedIn(int id_variable, int id_container) {
        try {
            String sql = "SELECT id_contained_in FROM contained_in WHERE id_variable = ? AND id_data_container = ?";
            ps = connection.prepareStatement(sql);
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

    public String searchContainerName(int id_project, int id_variable) {
        String container = "";
        try {
            String sql = "SELECT id_data_container FROM contained_in WHERE id_variable = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id_variable);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id_container = rs.getInt("id_data_container");
                String sql1 = "SELECT name_container FROM data_container WHERE id_data_container = ? AND id_project = ?";
                PreparedStatement ps1 = connection.prepareStatement(sql1);
                ps1.setInt(1, id_container);
                ps1.setInt(2, id_project);
                ResultSet rs1 = ps1.executeQuery();

                if (rs1.next()) {
                    container = rs1.getString("name_container");
                }
            }

            return container;
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
            return "Error al realizar la búsqueda";
        }
    }
}
