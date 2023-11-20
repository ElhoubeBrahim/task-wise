package com.example.frontend.services;

import com.example.frontend.models.CalendarEvent;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.util.Random;
import java.util.stream.Collectors;

public class MeetService extends CalendarService {
    public MeetService() throws IOException {
        super();
    }

    protected ConferenceData createConferenceData() {
        // Generate random request id (length 10)
        String randomRequestId = new Random().ints(10, 0, 36)
                .mapToObj(i -> Character.toString("abcdefghijklmnopqrstuvwxyz0123456789".charAt(i)))
                .collect(Collectors.joining());

        // Setup meet create request
        CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest();
        createConferenceRequest.setRequestId(randomRequestId);
        createConferenceRequest.setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"));

        // Setup conference data
        ConferenceData conferenceData = new ConferenceData();
        conferenceData.setCreateRequest(createConferenceRequest);
        return conferenceData;
    }

    @Override
    public Event createEvent(CalendarEvent event) throws IOException {
        Event calendarEvent = constructEvent(event);

        // Generate a meet link
        ConferenceData conferenceData = createConferenceData();
        calendarEvent.setConferenceData(conferenceData);

        return this.calendar.events()
            .insert("primary", calendarEvent)
            .setConferenceDataVersion(1)
            .execute();
    }
}
