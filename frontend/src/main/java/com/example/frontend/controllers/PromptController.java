package com.example.frontend.controllers;

import com.example.frontend.MainApp;
import com.example.frontend.models.CalendarEvent;
import com.example.frontend.models.Event;
import com.example.frontend.models.EventsResponse;
import com.example.frontend.models.TaskTodo;
import com.example.frontend.services.CalendarService;
import com.example.frontend.services.MeetService;
import com.example.frontend.services.TasksService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PromptController implements Initializable {
    @FXML
    private AnchorPane dynamicContent;

    @FXML
    private SplitPane prompt_layout;

    @FXML
    private Button changeContentButton;

    @FXML
    private TextArea promptTextArea;

    private ArrayList<Event> events;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            disableSplitPaneDividerDrag(prompt_layout);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/views/partials/initial-view.fxml"));
            VBox initial_view = loader.load();

            initial_view.prefWidthProperty().bind(dynamicContent.widthProperty());
            initial_view.prefHeightProperty().bind(dynamicContent.heightProperty());
            dynamicContent.getChildren().add(initial_view);
            changeContentButton.setOnAction(event -> {
                try {
                    onSubmitButtonCLick();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void disableSplitPaneDividerDrag(SplitPane splitPane) {
        for (Node divider : splitPane.lookupAll(".split-pane-divider")) {
            divider.setMouseTransparent(true);
        }
    }

    private void onSubmitButtonCLick() throws IOException {
        loadLoadingView();

        // Disable the submit button
        changeContentButton.setDisable(true);

        // Generate events in a separate thread
        Thread thread = new Thread(() -> {
            try {
                generateEvents();

                Platform.runLater(() -> {
                    if (events == null) {
                        loadView("partials/error-view");
                    } else {
                        loadView("partials/events-view");
                        loadEventsCards();
                    }
                });
            } catch (Exception e) {
                loadView("partials/error-view");
            } finally {
                // Enable the submit button
                changeContentButton.setDisable(false);
            }
        });

        thread.start();
    }

    private void loadView(String view) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/views/" + view + ".fxml"));
            VBox loadingView = loader.load();
            loadingView.prefWidthProperty().bind(dynamicContent.widthProperty());
            loadingView.prefHeightProperty().bind(dynamicContent.heightProperty());
            dynamicContent.getChildren().clear();
            dynamicContent.getChildren().add(loadingView);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateEvents() throws IOException {
        String[] todo = promptTextArea.getText().split("\n");
        String accessToken = MainApp.accessTokenService.loadAccessToken();

        // Send the request to the backend
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(MainApp.API_URL + "/events");
        request.setHeader("Authorization", "Bearer " + accessToken);
        request.setHeader("Content-Type", "application/json");

        StringEntity entity = new StringEntity("{\"todo\": [\"" + String.join("\", \"", todo) + "\"]}");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            String responseBody = EntityUtils.toString(responseEntity);
            EventsResponse eventsResponse = objectMapper.readValue(responseBody, EventsResponse.class);

            events = eventsResponse.getEvents();
        }
    }

    private void createEvents() throws IOException {
        loadLoadingView();

        new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    for (Event event : events) {
                        switch (event.getType()) {
                            case "meet":
                                CalendarEvent calendarEvent = new CalendarEvent(
                                        event.getTitle(),
                                        event.getDescription(),
                                        event.getStart(),
                                        event.getEnd()
                                );

                                MeetService meetService = new MeetService();
                                meetService.createEvent(calendarEvent);
                                break;
                            case "calendarEvent":
                                CalendarEvent calendarEvent1 = new CalendarEvent(
                                        event.getTitle(),
                                        event.getDescription(),
                                        event.getStart(),
                                        event.getEnd()
                                );

                                CalendarService calendarService = new CalendarService();
                                calendarService.createEvent(calendarEvent1);
                                break;
                            case "note":
                                TaskTodo taskTodo = new TaskTodo(
                                        event.getTitle(),
                                        event.getTodos()
                                );

                                TasksService tasksService = new TasksService();
                                tasksService.createTodoList(taskTodo);
                                break;
                        }
                    }

                    loadView("partials/success-view");
                } catch (IOException e) {
                    loadView("partials/error-view");
                }
            });
        }).start();
    }

    private void loadEventsCards() {
        ScrollPane scrollPane = (ScrollPane) dynamicContent.lookup("#events-scroll-pane");
        VBox eventsContainer = (VBox) scrollPane.getContent().lookup("#event-cards-container");

        if (eventsContainer == null) {
            throw new RuntimeException("Events container not found");
        }

        // Clear the events cards container
        eventsContainer.getChildren().clear();

        // Set approve button click handler
        Button approveButton = (Button) dynamicContent.lookup("#approve-btn");
        approveButton.setOnAction(event -> {
            try {
                createEvents();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        for (Event event : events) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/views/components/event-card.fxml"));
                HBox eventCard = loader.load();

                // Set margin for event card
                VBox.setMargin(eventCard, new javafx.geometry.Insets(0, 0, 20, 0));
                eventsContainer.prefWidthProperty().bind(scrollPane.widthProperty());

                // Get event card components
                ImageView eventIcon = (ImageView) eventCard.lookup(".event-card-icon");
                Label eventTitleLabel = (Label) eventCard.lookup(".event-card-title");
                Label eventDescriptionLabel = (Label) eventCard.lookup(".event-card-description");

                // Get event card time components
                VBox eventTimeContainer = (VBox) eventCard.lookup(".event-card-time-container");
                Label eventFromTime = (Label) eventTimeContainer.lookup(".from-time");
                Label eventToTime = (Label) eventTimeContainer.lookup(".to-time");

                // Set event card components
                eventIcon.setImage(event.getIcon());
                eventTitleLabel.setText(event.getCardTitle());
                eventDescriptionLabel.setText(event.getDescription());

                // Set event card time components
                if (event.getType().equals("note")) {
                    eventTimeContainer.setVisible(false);
                    eventTimeContainer.setManaged(false);
                } else {
                    eventFromTime.setText(event.getFormattedStart());
                    eventToTime.setText(event.getFormattedEnd());
                }

                // Set event card delete button click handler
                Button deleteButton = (Button) eventCard.lookup(".event-card-close-btn");
                deleteButton.setOnAction(event1 -> deleteEvent(event));

                eventsContainer.getChildren().add(eventCard);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadLoadingView() {
        loadView("partials/loading-view");

        // Rotate the loader icon
        Node loaderIcon = dynamicContent.lookup("#loader-icon");
        RotateTransition rotate = new RotateTransition();
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setDuration(Duration.millis(500));
        rotate.setNode(loaderIcon);
        rotate.play();
    }

    public void deleteEvent(Event event) {
        Alert alert = new Alert(
            Alert.AlertType.CONFIRMATION,
            "Are you sure you want to delete this event?",
            ButtonType.YES, ButtonType.NO
        );
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            events.remove(event);
            loadEventsCards();
        }
    }
}
