package org.example.core.service.task;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
import org.example.core.repository.TaskRepository;

import java.sql.Timestamp;
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
            throw new RuntimeException(String.format("Can`t get task with id: %s and category: '%s'. Task not found", id, category));
        }
    }

    @Override
    public void changeTaskStatus(Long id, TaskStatus taskStatus, String category) {
        TaskStatus currentStatus = getTask(id, category).getStatus();
        if (!(currentStatus.equals(taskStatus))) {
            taskRepository.changeTaskStatus(id, taskStatus, category);
            LogService.logger.info(String.format("Status for task with id: %s and category: '%s' changed from status '%s' to status '%s'",
                    id, category, currentStatus.name(), taskStatus.name()));
        }
    }

    @Override
    public void increaseRetryCountForTask(Long id, String category) {
        taskRepository.increaseRetryCountForTask(id, category);
    }

    @Override
    public void rescheduleTask(Long id, long delay, String category) {
        if (delay >= 0) {
            Timestamp time = new Timestamp(getTask(id, category).getExecutionTime().getTime() + delay);
            taskRepository.rescheduleTask(id, delay, category);
            LogService.logger.info(String.format("Reschedule task with id: %s and category: '%s'. New execution time = %s",
                    id, category, time));
            taskRepository.changeTaskStatus(id, TaskStatus.PENDING, category);
        } else {
            throw new RuntimeException(String.format("Can`t reschedule task with id: %s and category: '%s'. Value of delay < 0", id, category));
        }
    }

    @Override
    public List<ScheduledTask> getReadyTasksByCategory(String category) {
        return taskRepository.getReadyTasksByCategory(category);
    }

    @Override
    public ScheduledTask getNextReadyTaskByCategory(String category) {
        return taskRepository.getNextReadyTaskByCategory(category);
    }

    @Override
    public Long save(ScheduledTask task, String category) {
        Long id = taskRepository.save(task, category);
        LogService.logger.info(String.format("Task with id: %s successfully created: object %s", id, task));
        return id;
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