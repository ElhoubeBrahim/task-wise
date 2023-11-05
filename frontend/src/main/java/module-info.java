module com.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.frontend to javafx.fxml;
    exports com.example.frontend;
}