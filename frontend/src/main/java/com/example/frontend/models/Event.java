package com.example.frontend.models;

import com.example.frontend.MainApp;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.image.Image;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Event {
    private String type;
    private String title;
    private String start;
    private String end;
    private ArrayList<String> todos;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("start")
    public String getStart() {
        ZonedDateTime startTime = ZonedDateTime.parse(start);
        return startTime.format(DateTimeFormatter.ofPattern("HH:mm a"));
    }

    public void setStart(String start) {
        this.start = start;
    }

    @JsonProperty("end")
    public String getEnd() {
        ZonedDateTime endTime = ZonedDateTime.parse(end);
        return endTime.format(DateTimeFormatter.ofPattern("HH:mm a"));
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @JsonProperty("todos")
    public ArrayList<String> getTodos() {
        return todos;
    }

    public void setTodos(ArrayList<String> todos) {
        this.todos = todos;
    }

    public String getCardTitle() {
        return switch (type) {
            case "meet" -> "Meeting";
            case "calendarEvent" -> "Calendar Event";
            case "note" -> "Todo";
            default -> "";
        };
    }

    public Image getIcon() {
        String path = "images/";

        switch (type) {
            case "meet":
                path += "meet-logo.png";
                break;
            case "calendarEvent":
                path += "calendar-logo.png";
                break;
            case "note":
                path += "keep-logo.png";
                break;
        }

        return new Image(MainApp.class.getResourceAsStream(path));
    }

    public String getDescription() {
        if (Objects.equals(type, "note")) {
            return String.join("\n", todos);
        }

        return title;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", todos=" + todos +
                '}';
    }
}

