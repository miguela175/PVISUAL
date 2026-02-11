package com.example.proyectovisual.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection;

    public static Connection connect(String host, String port,
                                     String dbName, String user, String password)
            throws SQLException {

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

        connection = DriverManager.getConnection(url, user, password);

        return connection;
    }

    public static Connection getConnection() {
        return connection;
    }
}
