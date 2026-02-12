package com.example.proyectovisual.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import com.example.proyectovisual.dao.GenericDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class MainController {

    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private TextField dbNameField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private TextField tableNameField;

    @FXML private TableView<ObservableList<String>> dynamicTable;
    @FXML private VBox crudBox; // contenedor donde se generan los TextFields

    private Connection conn;
    private GenericDAO dao;
    private List<TextField> crudFields = new ArrayList<>();
    private List<String> columnNames = new ArrayList<>();

    @FXML
    private void handleConnectAndLoad() {
        String url = "jdbc:postgresql://" + hostField.getText() + ":" + portField.getText() + "/" + dbNameField.getText();
        String user = this.userField.getText();
        String password = this.passwordField.getText();
        String tableName = tableNameField.getText();

        try {
            conn = DriverManager.getConnection(url, user, password);
            dao = new GenericDAO(conn, tableName);
            columnNames = dao.getColumnNames();
            buildCrudFields();
            loadTable();
            showInfo("Conexión exitosa", "Se cargó la tabla: " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error de conexión", "No se pudo conectar:\n" + e.getMessage());
        }
    }

    private void buildCrudFields() {
        crudBox.getChildren().clear();
        crudFields.clear();
        // saltamos la primera columna si es ID
        for (int i = 1; i < columnNames.size(); i++) {
            TextField tf = new TextField();
            tf.setPromptText(columnNames.get(i));
            crudBox.getChildren().add(tf);
            crudFields.add(tf);
        }
        // botones CRUD
        Button addBtn = new Button("Agregar");
        addBtn.setOnAction(e -> handleAdd());
        Button updateBtn = new Button("Actualizar");
        updateBtn.setOnAction(e -> handleUpdate());
        Button deleteBtn = new Button("Eliminar");
        deleteBtn.setOnAction(e -> handleDelete());
        crudBox.getChildren().addAll(addBtn, updateBtn, deleteBtn);
    }

    private void loadTable() {
        try {
            List<Map<String, Object>> rows = dao.findAll();

            if (rows.isEmpty()) {
                dynamicTable.getColumns().clear();
                dynamicTable.setItems(FXCollections.observableArrayList());
                return;
            }

            dynamicTable.getColumns().clear();
            Map<String, Object> firstRow = rows.get(0);
            int colIndex = 0;
            for (String colName : firstRow.keySet()) {
                final int index = colIndex;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
                col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(index)));
                dynamicTable.getColumns().add(col);
                colIndex++;
            }

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            for (Map<String, Object> row : rows) {
                ObservableList<String> rowData = FXCollections.observableArrayList();
                for (Object val : row.values()) {
                    rowData.add(val != null ? val.toString() : "");
                }
                data.add(rowData);
            }
            dynamicTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo cargar la tabla:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            Map<String, Object> values = new LinkedHashMap<>();
            for (int i = 1; i < columnNames.size(); i++) {
                String colName = columnNames.get(i);
                String inputValue = crudFields.get(i-1).getText();
                Object value = convertValue(colName, inputValue);
                values.put(colName, value);
            }
            dao.insert(values);
            loadTable();
            limpiarCrud();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo insertar:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        ObservableList<String> selectedRow = dynamicTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            showAlert("Error", "Selecciona una fila para actualizar.");
            return;
        }
        try {
            int id = Integer.parseInt(selectedRow.get(0));
            Map<String, Object> values = new LinkedHashMap<>();
            for (int i = 1; i < columnNames.size(); i++) {
                String colName = columnNames.get(i);
                String inputValue = crudFields.get(i-1).getText();
                Object value = convertValue(colName, inputValue);
                values.put(colName, value);
            }
            dao.update(id, values);
            loadTable();
            limpiarCrud();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo actualizar:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        ObservableList<String> selectedRow = dynamicTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            showAlert("Error", "Selecciona una fila para eliminar.");
            return;
        }
        try {
            int id = Integer.parseInt(selectedRow.get(0)); // asumimos que la primera columna es ID
            dao.delete(id);
            loadTable();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo eliminar:\n" + e.getMessage());
        }
    }

    private Object convertValue(String colName, String inputValue) {
        if (colName.equalsIgnoreCase("duracion")) {
            return Integer.parseInt(inputValue);
        } else if (colName.equalsIgnoreCase("precio")) {
            return Double.parseDouble(inputValue);
        }
        return inputValue; // por defecto String
    }

    private void limpiarCrud() {
        for (TextField tf : crudFields) {
            tf.clear();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
