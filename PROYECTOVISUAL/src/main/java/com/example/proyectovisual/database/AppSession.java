package com.example.proyectovisual.database;
// Guardar la conexion a la base de datos

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
