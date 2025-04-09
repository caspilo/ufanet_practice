package org.example;

import org.example.core.entity.ScheduledTask;
import org.example.core.service.TaskService;

import java.sql.Timestamp;
import java.time.Instant;

public class Main {
    public static TaskService taskService;
    public static void main(String[] args) throws Exception {
        ScheduledTask scheduledTask = new ScheduledTask(1L, "PUSH_NOTIFICATION", Timestamp.from(Instant.now()));
        taskService.createTask(scheduledTask);
    }
}