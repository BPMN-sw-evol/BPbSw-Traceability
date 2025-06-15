package com.msgfoundation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;

@Configuration
public class DatabaseInitializer {

    @Value("${app.database.names}") 
    private List<String> databaseNames;

    @Bean
    public CommandLineRunner initializeDatabases(DataSource dataSource) {
        return args -> {
            System.out.println("Iniciando verificación de bases de datos...");
            
            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData metaData = conn.getMetaData();
                
                // Verificar cada base de datos
                for (String dbName : databaseNames) {
                    verifyAndCreateDatabase(conn, metaData, dbName, dataSource);
                }
                
                System.out.println("Proceso de verificación completado");
            } catch (Exception e) {
                System.err.println("Error durante la inicialización: " + e.getMessage());
                throw new RuntimeException("Fallo en la inicialización de bases de datos", e);
            }
        };
    }

    private void verifyAndCreateDatabase(Connection conn, DatabaseMetaData metaData, 
                                       String dbName, DataSource dataSource) throws Exception {
        try (ResultSet resultSet = metaData.getCatalogs()) {
            boolean dbExists = false;
            
            while (resultSet.next()) {
                if (dbName.equalsIgnoreCase(resultSet.getString(1))) {
                    dbExists = true;
                    break;
                }
            }
            
            if (!dbExists) {
                try {
                    System.out.println("Creando base de datos: " + dbName);
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource); // Usamos el DataSource directamente
                    jdbcTemplate.execute("CREATE DATABASE " + dbName);
                    System.out.println("Base de datos creada exitosamente: " + dbName);
                } catch (Exception e) {
                    System.out.println("Base de datos ya existe: " + dbName);
                }
            } else {
                System.out.println("Base de datos ya existe: " + dbName);
            }
            
        }
    }
}