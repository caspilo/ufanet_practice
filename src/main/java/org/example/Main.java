package org.example;

import org.example.entity.ScheduledTask;
import org.example.service.TaskService;

public class Main {
    public static TaskService taskService;
    public static void main(String[] args) throws Exception {
        ScheduledTask scheduledTask = new ScheduledTask();
        taskService.createTask(scheduledTask);
    }
}