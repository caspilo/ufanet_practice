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
        ScheduledTask task = taskRepository.findById(id);
        if (task != null) {
            return task;
        } else {
            throw new RuntimeException("ERROR. Can not get task with id " + id + ": task not found");
        }
    }

    public void changeTaskStatus(Long id, TASK_STATUS taskStatus) {
        if (!(getTask(id).getStatus().equals(taskStatus))) {
            taskRepository.changeTaskStatus(id, taskStatus);
        }
    }

    @Override
    public void increaseRetryCountForTask(Long id) {
        taskRepository.increaseRetryCountForTask(id);
    }

    @Override
    public void rescheduleTask(Long id, long delay) {
        if (delay >= 0) {
            taskRepository.rescheduleTask(id, delay);
            taskRepository.changeTaskStatus(id, TASK_STATUS.PENDING);
        } else {
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay < 0");
        }
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