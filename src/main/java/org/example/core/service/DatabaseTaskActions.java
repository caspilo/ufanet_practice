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
        taskRepository.changeTaskStatus(id, taskStatus);
    }

    @Override
    public List<ScheduledTask> getReadyTasks() {
        return taskRepository.getReadyTasks();
    }

    @Override
    public List<ScheduledTask> getAndLockReadyTasks() {
       return taskRepository.getAndLockReadyTasks();
    }

    public List<ScheduledTask> getReadyTasksByCategory(String category) {
        return taskRepository.getReadyTasksByCategory(category);
    }

    public List<ScheduledTask> getAndLockReadyTasksByCategory(String category) {
        return taskRepository.getAndLockReadyTasksByCategory(category);
    }

    public void startTransaction() {
        taskRepository.startTransaction();
    }

    public void commitTransaction() {
        taskRepository.commitTransaction();
    }
}