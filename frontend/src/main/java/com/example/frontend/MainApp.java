package com.example.frontend;

import com.example.frontend.services.AccessTokenService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    public final static AccessTokenService accessTokenService = new AccessTokenService();

    private static Stage AppStage = null;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("views/welcome-view.fxml")); // Load FXML file
        Image icon = new Image(MainApp.class.getResourceAsStream("images/ico.png")); // Load app icon
        Scene scene = new Scene(fxmlLoader.load()); // Create scene from FXML file

        // Set stage properties
        stage.setTitle("Task Wise");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        // Save stage for later use
        AppStage = stage;
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getStage() {
        return AppStage;
    }
}