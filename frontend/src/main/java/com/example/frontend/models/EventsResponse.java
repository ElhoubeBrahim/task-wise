package com.example.frontend.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class EventsResponse {
    private ArrayList<Event> events;
    public String error;

    @JsonProperty("events")
    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    @JsonProperty("error")
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
