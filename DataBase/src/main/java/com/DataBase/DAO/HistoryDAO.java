package com.DataBase.DAO;

import java.sql.*;
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

    public List<Timestamp> getAllHistorys() {
        List<Timestamp> historyDates = new ArrayList<>();
        try {
            String sql = "SELECT date FROM history";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("date");
                if (timestamp != null) {
                    historyDates.add(new Timestamp(timestamp.getTime()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar la búsqueda: " + e);
        } finally {
            // Cerrar recursos (ps, rs) aquí si es necesario
        }
        return historyDates;
    }

}
