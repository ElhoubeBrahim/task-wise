module com.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.frontend to javafx.fxml;
    exports com.example.frontend;
    exports com.example.frontend.controllers;
    opens com.example.frontend.controllers to javafx.fxml;
}