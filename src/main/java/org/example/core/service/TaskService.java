package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;

import java.util.List;

public interface TaskService {

    ScheduledTask getTask(Long id);

    void changeTaskStatus(Long id, TASK_STATUS taskStatus);

    List<ScheduledTask> getReadyTasksByCategory(String category);

    List<ScheduledTask> getAndLockReadyTasksByType(String type);

    void startTransaction();

    void commitTransaction();

}
