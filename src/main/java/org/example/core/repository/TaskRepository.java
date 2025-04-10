package org.example.core.repository;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;

import java.util.List;

public interface TaskRepository {
    void save(ScheduledTask task);
    void cancelTask(Long id);
    void lockTask(Long id);
    void changeTaskStatus(Long id, TASK_STATUS status);
    List<ScheduledTask> getReadyTasks();
    List<ScheduledTask> getReadyTasksByCategory(String category);
    void rescheduleTask(Long id, int delay);
    ScheduledTask findById(Long id);
}