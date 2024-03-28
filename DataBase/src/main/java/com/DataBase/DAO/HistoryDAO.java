package com.DataBase.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    public HistoryDAO(Connection connection) {
        this.connection = connection;
    }

    public int createHistory(String name) {
        try {
            String sql = "INSERT INTO history (name,date) VALUES (?,NOW())";
            ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear historia: " + e.getMessage());
        }
        return -1;
    }

    public List<Integer> getAllHistorys() {
        List<Integer> historyIDs = new ArrayList<>();
        try {
            String sql = "SELECT id_history FROM history";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                historyIDs.add(rs.getInt("id_history"));
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
        } finally {
            // Cerrar recursos (ps, rs) aquí si es necesario
        }
        return historyIDs;
    }
}
