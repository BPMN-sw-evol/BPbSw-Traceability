package com.Trazability.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    public ProjectDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertProject(String name, String path) {
        try {
            String sql = "INSERT INTO project (name_project,path) VALUES (?,?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, path);
            int filasAfectadas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al realizar la inserción: " + e);
        }
    }

    public int searchProject(String name, String path) {
        try {
            String sql = "SELECT id_project FROM project WHERE name_project = ? AND path = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, path);
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

    public int searchProject(String name) {
        try {
            String sql = "SELECT id_project FROM project WHERE name_project = ?";
            ps = connection.prepareStatement(sql);
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

    public List<String> searchProjectByVariableId(int variableId) {
        List<String> projectNames = new ArrayList<>();

        try {
            // Consultar la tabla 'contained_in' para obtener el 'id_data_container' a partir del 'id' de la variable
            String containerQuery = "SELECT id_data_container FROM contained_in WHERE id_variable = ?";
            PreparedStatement containerPs = connection.prepareStatement(containerQuery);
            containerPs.setInt(1, variableId);
            ResultSet containerRs = containerPs.executeQuery();

            while (containerRs.next()) {
                int dataContainerId = containerRs.getInt("id_data_container");

                // Consultar la tabla 'data_container' para obtener el 'id_project' a partir del 'id_data_container'
                String dataContainerQuery = "SELECT id_project FROM data_container WHERE id_data_container = ?";
                PreparedStatement dataContainerPs = connection.prepareStatement(dataContainerQuery);
                dataContainerPs.setInt(1, dataContainerId);
                ResultSet dataContainerRs = dataContainerPs.executeQuery();

                if (dataContainerRs.next()) {
                    int projectId = dataContainerRs.getInt("id_project");

                    // Consultar la tabla 'project' para obtener el 'name_project' a partir del 'id_project'
                    String projectQuery = "SELECT name_project FROM project WHERE id_project = ?";
                    PreparedStatement projectPs = connection.prepareStatement(projectQuery);
                    projectPs.setInt(1, projectId);
                    ResultSet projectRs = projectPs.executeQuery();

                    if (projectRs.next()) {
                        String projectName = projectRs.getString("name_project");
                        if (!projectNames.contains(projectName)) {
                            projectNames.add(projectName);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
        }

        return projectNames;
    }
}
