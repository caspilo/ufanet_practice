package org.example.core.service.task;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.repository.TaskRepository;

import java.util.List;

public class DatabaseTaskActions implements TaskService {

    private final TaskRepository taskRepository;

    public DatabaseTaskActions(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public ScheduledTask getTask(Long id, String category) {
        ScheduledTask task = taskRepository.findById(id, category);
        if (task != null) {
            return task;
        } else {
            throw new RuntimeException("ERROR. Can not get task with id " + id + ": task not found");
        }
    }

    @Override
    public void changeTaskStatus(Long id, TaskStatus taskStatus, String category) {
        if (!(getTask(id, category).getStatus().equals(taskStatus))) {
            taskRepository.changeTaskStatus(id, taskStatus, category);
        }
    }

    @Override
    public void increaseRetryCountForTask(Long id, String category) {
        taskRepository.increaseRetryCountForTask(id, category);
    }

    @Override
    public void rescheduleTask(Long id, long delay, String category) {
        if (delay >= 0) {
            taskRepository.rescheduleTask(id, delay, category);
            taskRepository.changeTaskStatus(id, TaskStatus.PENDING, category);
        } else {
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay < 0");
        }
    }

    @Override
    public List<ScheduledTask> getAndLockReadyTasksByCategory(String category) {
        return taskRepository.getAndLockReadyTasksByCategory(category);
    }

    @Override
    public ScheduledTask getAndLockNextTaskByCategory(String category) {
        return taskRepository.getAndLockNextTaskByCategory(category);
    }

    @Override
    public Long save(ScheduledTask task, String category) {
        return taskRepository.save(task, category);
    }

    @Override
    public void cancelTask(Long id, String category) {
        taskRepository.cancelTask(id, category);
    }

    @Override
    public ScheduledTask findById(Long id, String category) {
        return taskRepository.findById(id, category);
    }
}