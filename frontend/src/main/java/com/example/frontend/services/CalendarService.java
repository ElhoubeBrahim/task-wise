package com.example.frontend.services;

import com.example.frontend.models.CalendarEvent;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CalendarService {
    Calendar calendar;

    public CalendarService() throws IOException {
        this.calendar = new Calendar.Builder(
            new NetHttpTransport(),
            new GsonFactory(),
            new GoogleCredential().setAccessToken(new AccessTokenService().loadAccessToken())
        ).setApplicationName("Task Wise").build();
    }

    protected Event constructEvent(CalendarEvent event) {
        String summary = event.getSummary();
        String description = event.getDescription();
        EventDateTime startTime = new EventDateTime().setDateTime(DateTime.parseRfc3339(event.getStartTime()));
        EventDateTime endTime = new EventDateTime().setDateTime(DateTime.parseRfc3339(event.getEndTime()));

        Event calendarEvent = new Event();
        calendarEvent.setSummary(summary);
        calendarEvent.setDescription(description);
        calendarEvent.setStart(startTime);
        calendarEvent.setEnd(endTime);

        return calendarEvent;
    }

    public Event createEvent(CalendarEvent event) throws IOException {
        Event calendarEvent = constructEvent(event);
        return this.calendar.events().insert("primary", calendarEvent).execute();
    }
}
