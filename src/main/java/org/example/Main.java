package org.example;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.TestClass;
import org.example.core.service.TaskService;
import org.example.worker.TaskWorker;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;

public class Main {
    public static TaskWorker taskWorker;
    public static TaskService taskService;
    public static void main(String[] args) {

        new TaskWorker("1", Collections.singletonMap("1", "org.example.core.entity.TestClass")).run();
    }
}