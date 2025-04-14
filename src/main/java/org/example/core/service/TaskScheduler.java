package org.example.core.service;

import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.TaskRepository;

import java.sql.Timestamp;
import java.util.Map;

public class TaskScheduler {

    private final TaskRepository taskRepository;

    public TaskScheduler(JdbcTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    public Long scheduleTask(String className, Map<String, String> params, Timestamp executionTime, double delayBase) {



        return null;
    }

    public void cancelTask(Long id) {
        taskRepository.cancelTask(id);
    }


}
