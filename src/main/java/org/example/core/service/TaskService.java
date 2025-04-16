package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;

import java.sql.Timestamp;
import java.util.List;

public interface TaskService {

    ScheduledTask getTask(long id) throws Exception;

    void cancelTask(long id) throws Exception;

    void changeTaskStatus(Long id, TASK_STATUS taskStatus) throws Exception;

    ScheduledTask createTask(Long id, String type, Timestamp executionTime) throws Exception;

    List<ScheduledTask> getReadyTasksByType(String type);

    List<ScheduledTask> getAndLockReadyTasksByType(String type);

    void startTransaction();

    void commitTransaction();

}
