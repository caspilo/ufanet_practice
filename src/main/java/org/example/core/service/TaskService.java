package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;

import java.util.List;

public interface TaskService {

    ScheduledTask getTask(Long id);

    void changeTaskStatus(Long id, TASK_STATUS taskStatus);

    void increaseRetryCountForTask(Long id);

    void rescheduleTask(Long id, long delay);

    List<ScheduledTask> getReadyTasks();

    List<ScheduledTask> getAndLockReadyTasks();

    List<ScheduledTask> getReadyTasksByCategory(String category);

    List<ScheduledTask> getAndLockReadyTasksByCategory(String category);

    void startTransaction();

    void commitTransaction();
}
