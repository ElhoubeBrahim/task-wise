package com.example.frontend.models;

public class CalendarEvent {
    protected String summary;
    protected String description;
    protected String start;
    protected String end;

    public CalendarEvent(String summary, String description, String start, String end) {
        this.summary = summary;
        this.description = description;
        this.start = start;
        this.end = end;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStartTime() {
        return this.start;
    }

    public String getEndTime() {
        return this.end;
    }
}
