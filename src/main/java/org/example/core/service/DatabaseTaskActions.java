package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.repository.TaskRepository;

import java.util.List;


public class DatabaseTaskActions implements TaskService {

    private final TaskRepository taskRepository;

    public DatabaseTaskActions(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public ScheduledTask getTask(Long id) {
        return taskRepository.findById(id);
    }

    public void changeTaskStatus(Long id, TASK_STATUS taskStatus) {
        getTask(id).setStatus(taskStatus);
    }


    public List<ScheduledTask> getReadyTasksByCategory(String category) {
        return taskRepository.getReadyTasksByCategory(category);
    }

    public List<ScheduledTask> getAndLockReadyTasksByType(String type) {
        return null;
    }

    public void startTransaction() {

    }

    public void commitTransaction() {

    }
}