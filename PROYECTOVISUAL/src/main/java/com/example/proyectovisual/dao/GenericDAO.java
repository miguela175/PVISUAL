package com.example.proyectovisual.dao;

import java.sql.*;
import java.util.*;

public class GenericDAO {
    private Connection conn;
    private String tableName;

    public GenericDAO(Connection conn, String tableName) {
        this.conn = conn;
        this.tableName = tableName;
    }

    public List<Map<String, Object>> findAll() throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                rows.add(row);
            }
        }
        return rows;
    }

    public List<String> getColumnNames() throws SQLException {
        List<String> columns = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                columns.add(meta.getColumnName(i));
            }
        }
        return columns;
    }

    public Map<String, Integer> getColumnTypes() throws SQLException {
        Map<String, Integer> types = new LinkedHashMap<>();
        String query = "SELECT * FROM " + tableName + " LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                types.put(meta.getColumnName(i), meta.getColumnType(i));
            }
        }
        return types;
    }

    public void insert(Map<String, Object> values) throws SQLException {
        StringBuilder cols = new StringBuilder();
        StringBuilder params = new StringBuilder();
        for (String col : values.keySet()) {
            if (cols.length() > 0) {
                cols.append(", ");
                params.append(", ");
            }
            cols.append(col);
            params.append("?");
        }
        String sql = "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + params + ")";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            for (Object val : values.values()) {
                ps.setObject(index++, val);
            }
            ps.executeUpdate();
        }
    }

    public void update(int id, Map<String, Object> values) throws SQLException {
        StringBuilder setClause = new StringBuilder();
        for (String col : values.keySet()) {
            if (setClause.length() > 0) setClause.append(", ");
            setClause.append(col).append("=?");
        }
        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            for (Object val : values.values()) {
                ps.setObject(index++, val);
            }
            ps.setInt(index, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
