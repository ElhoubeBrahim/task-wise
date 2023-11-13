package com.example.frontend.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PromptController implements Initializable {
    @FXML
    private AnchorPane dynamicContent;

    @FXML
    private SplitPane prompt_layout;

    @FXML
    private Button changeContentButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            disableSplitPaneDividerDrag(prompt_layout);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/views/initial-view.fxml"));
            VBox initial_view = null;

            initial_view = loader.load();
            initial_view.prefWidthProperty().bind(dynamicContent.widthProperty());
            initial_view.prefHeightProperty().bind(dynamicContent.heightProperty());
            dynamicContent.getChildren().add(initial_view);
            changeContentButton.setOnAction(event -> onSubmitButtonCLick());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void disableSplitPaneDividerDrag(SplitPane splitPane) {
        for (Node divider : splitPane.lookupAll(".split-pane-divider")) {
            divider.setMouseTransparent(true);
        }
    }
    private void onSubmitButtonCLick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/views/loading-view.fxml"));
            VBox loadingView = loader.load();
            loadingView.prefWidthProperty().bind(dynamicContent.widthProperty());
            loadingView.prefHeightProperty().bind(dynamicContent.heightProperty());
            dynamicContent.getChildren().clear();
            dynamicContent.getChildren().add(loadingView);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
