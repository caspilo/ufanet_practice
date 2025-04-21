package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;

import java.util.List;

public interface TaskService {

    ScheduledTask getTask(Long id, String category);

    void changeTaskStatus(Long id, TaskStatus taskStatus, String category);

    void increaseRetryCountForTask(Long id, String category);

    void rescheduleTask(Long id, long delay, String category);

    List<ScheduledTask> getAndLockReadyTasksByCategory(String category);

    void startTransaction();

    void commitTransaction();

    Long save(ScheduledTask task, String category);

    void cancelTask(Long id, String category);

    ScheduledTask findById(Long id, String category);
}
