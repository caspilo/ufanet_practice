package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.TaskRepository;

import java.util.Optional;

public class TaskScheduler {

    private final TaskRepository taskRepository;

    public TaskScheduler(JdbcTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }



}
