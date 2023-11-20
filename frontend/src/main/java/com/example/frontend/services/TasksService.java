package com.example.frontend.services;

import com.example.frontend.models.TaskTodo;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

import java.io.IOException;
import java.util.ArrayList;

public class TasksService {

    Tasks task;

    public TasksService() throws IOException {
        this.task = new Tasks.Builder(
            new NetHttpTransport(),
            new GsonFactory(),
            new GoogleCredential().setAccessToken(new AccessTokenService().loadAccessToken())
        ).setApplicationName("Task Wise").build();
    }

    public TaskList createTodoList(TaskTodo todo) throws IOException {
        TaskList taskList = new TaskList();

        taskList.setTitle(todo.getTitle());

        TaskList result = task.tasklists().insert(taskList).execute();

        ArrayList<Task> tasks = new ArrayList<>();
        for (String item : todo.getItems()) {
            Task taskItem = new Task();
            taskItem.setTitle(item);
            tasks.add(taskItem);
        }

        for (Task taskItem : tasks) {
            task.tasks().insert(result.getId(), taskItem).execute();
        }

        return result;
    }
}
