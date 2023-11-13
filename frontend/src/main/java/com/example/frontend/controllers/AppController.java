package com.example.frontend.controllers;

import com.example.frontend.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.security.GeneralSecurityException;

public class AppController {
    @FXML
    HBox authorizeAccessTokenBox;

    @FXML
    Button signinWithGoogleBtn;

    @FXML
    TextField accessTokenInput;

    @FXML
    protected void onSigninButtonClick() throws IOException {
        String authUrl = MainApp.accessTokenService.generateAuthUrl();
        openBrowser(authUrl);

        // Show the authorize access token box
        authorizeAccessTokenBox.setVisible(true);
        signinWithGoogleBtn.setVisible(false);
        signinWithGoogleBtn.setManaged(false);
    }

    @FXML
    protected void onAuthorizeButtonClick(ActionEvent event) throws IOException, GeneralSecurityException {
        // Deactivate the authorize access token button
        Button authorizeAccessTokenBtn = (Button) event.getSource();
        authorizeAccessTokenBtn.setDisable(true);
        authorizeAccessTokenBtn.setText("Authorizing...");

        // Get the access token from the input field
        String accessToken = accessTokenInput.getText();

        // Verify access token
        if(MainApp.accessTokenService.verifyAccessToken(accessToken)) {
            // Save the access token to a file
            MainApp.accessTokenService.saveAccessToken(accessToken);

            // Navigate to the prompt view
            navigateToView("prompt-view");
        } else {
            // Navigate to the welcome view
            navigateToView("welcome-view");
        }
    }

    public void navigateToView(String view) throws IOException {
        // Load the view
        Parent scene = FXMLLoader.load(MainApp.class.getResource("views/" + view + ".fxml"));

        // Get screen dimensions
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getBounds().getWidth();
        double screenHeight = screen.getBounds().getHeight();

        // Show the view in full screen
        Stage stage = MainApp.getStage();
        stage.setScene(new Scene(scene, screenWidth, screenHeight));
        stage.show();
    }

    public void openBrowser(String url) {
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url); // Open the URL in the default browser
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}