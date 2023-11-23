module com.example.frontend {
    requires javafx.controls;
    requires javafx.web;
    requires javafx.fxml;
    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.services.calendar;
    requires com.google.api.services.tasks;
    requires com.google.api.client.auth;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    opens com.example.frontend to javafx.fxml;
    exports com.example.frontend;
    exports com.example.frontend.controllers;
    exports com.example.frontend.models;
    opens com.example.frontend.controllers to javafx.fxml;
}