package com.example.proyectovisual.database;


import java.sql.Connection;

public class AppSession {
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    public static void setConnection(Connection conn) {
        connection = conn;
    }
}
