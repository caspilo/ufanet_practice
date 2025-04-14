package org.example.core.repository;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;

import java.util.List;

public interface TaskRepository {
    Long save(ScheduledTask task);
    void cancelTask(Long id);
    void changeTaskStatus(Long id, TASK_STATUS status);
    List<ScheduledTask> getReadyTasks();
    List<ScheduledTask> getAndLockReadyTasks();
    List<ScheduledTask> getReadyTasksByCategory(String category);
    void rescheduleTask(Long id, int delay);
    ScheduledTask findById(Long id);
    boolean existsById(Long id);
}