module com.example.proyectovisual {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.proyectovisual.controller to javafx.fxml;
    opens com.example.proyectovisual to javafx.graphics;
}
