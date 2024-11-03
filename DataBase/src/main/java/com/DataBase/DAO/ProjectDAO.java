package com.DataBase.DAO;

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
            String methodQuery = "SELECT id_method FROM used_by_method WHERE id_variable = ?";
            PreparedStatement methodPs = connection.prepareStatement(methodQuery);
            methodPs.setInt(1, variableId);
            ResultSet methodRs = methodPs.executeQuery();

            while (methodRs.next()) {
                int methodId = methodRs.getInt("id_method");

                // Consultar la tabla 'data_container' para obtener el 'id_project' a partir del 'id_data_container'
                String  classQuery = "SELECT id_class FROM method WHERE id_method = ?";
                PreparedStatement classPs = connection.prepareStatement(classQuery);
                classPs.setInt(1, methodId);
                ResultSet classRs = classPs.executeQuery();

                if (classRs.next()) {
                    int classId = classRs.getInt("id_class");

                    // Consultar la tabla 'data_container' para obtener el 'id_project' a partir del 'id_data_container'
                    String  projectIdQuery = "SELECT id_project FROM class WHERE id_class = ?";
                    PreparedStatement projectIdPs = connection.prepareStatement(projectIdQuery);
                    projectIdPs.setInt(1, classId);
                    ResultSet projectIdRs = projectIdPs.executeQuery();

                    if (projectIdRs.next()) {
                        int projectId = projectIdRs.getInt("id_project");

                        String  projectQuery = "SELECT name_project FROM project WHERE id_project = ?";
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
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
        }

        return projectNames;
    }
}
