package com.example.frontend;

import com.example.frontend.services.AccessTokenService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MainApp extends Application {
    public final static AccessTokenService accessTokenService = new AccessTokenService();

    private static Stage AppStage = null;

    @Override
    public void start(Stage stage) throws IOException {
        // Load access token from file
        String view = "welcome-view";
        try {
            String accessToken = accessTokenService.loadAccessToken();
            if (accessToken != null && accessTokenService.verifyAccessToken(accessToken)) {
                view = "prompt-view";
            }
        } catch (Exception e) {
            // Pass
        }

        // Load view
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("views/" + view + ".fxml")); // Load FXML file
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