package com.example.proyectovisual.controller;

import com.example.proyectovisual.database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;

public class MainController {

    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private TextField dbField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleConnect() {

        try {
            Connection conn = DBConnection.connect(
                    hostField.getText(),
                    portField.getText(),
                    dbField.getText(),
                    userField.getText(),
                    passwordField.getText()
            );

            messageLabel.setText("✅ Conectado correctamente");

        } catch (Exception e) {
            messageLabel.setText("❌ Error: " + e.getMessage());
        }
    }
}
