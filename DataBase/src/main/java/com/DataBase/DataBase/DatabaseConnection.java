package com.DataBase.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final Connection connection;

    private DatabaseConnection() {
        String url = "jdbc:mysql://localhost:3326/db_traceability";
//        String url = "jdbc:mysql://localhost:3306/test_traceability";
        String usuario = "root";
        String contraseña = "mysql";

        try {
            connection = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión exitosa a la base de datos MySQL");
        } catch (SQLException e) {
            // Lanza una excepción para que el cliente pueda manejarla adecuadamente
            throw new IllegalStateException("Error al conectar a la base de datos", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}

