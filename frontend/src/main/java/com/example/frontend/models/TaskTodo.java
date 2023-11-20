package com.example.frontend.models;

import java.util.ArrayList;

public class TaskTodo {
    private String title;
    private ArrayList<String> items;

    public TaskTodo(String title, ArrayList<String> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void addItem(String item) {
        this.items.add(item);
    }
}
