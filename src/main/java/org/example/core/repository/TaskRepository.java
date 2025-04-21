package org.example.core.repository;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;

import java.util.List;

public interface TaskRepository {
    Long save(ScheduledTask task, String category);

    void cancelTask(Long id, String category);

    void changeTaskStatus(Long id, TaskStatus status, String category);

    void increaseRetryCountForTask(Long id, String category);

    List<ScheduledTask> getAndLockReadyTasksByCategory(String category);

    void rescheduleTask(Long id, long delay, String category);

    ScheduledTask findById(Long id, String category);

    boolean existsById(Long id, String category);

    void startTransaction();

    void commitTransaction();
}