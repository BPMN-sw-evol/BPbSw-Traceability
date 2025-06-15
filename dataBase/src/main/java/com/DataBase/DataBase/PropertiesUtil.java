package com.DataBase.DataBase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
     private static final Properties properties = new Properties();

    static {
        try (InputStream input = PropertiesUtil.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                System.out.println("No se encontró application.properties");
            } else {
                properties.load(input);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
