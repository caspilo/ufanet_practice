package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.test.Schedulable;

import java.util.List;
import java.util.Map;

public interface TaskService {

    ScheduledTask getTask(long id);

    void cancelTask(long id);

    void changeTaskStatus(Long id, TASK_STATUS taskStatus);

    List<ScheduledTask> getReadyTasksByCategory(String category);

    public Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime, double delayBase);

    public Long scheduleTask(Class schedulableClass, Map<String, String> params, String executionTime, double delayBase);

    public Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, double delayBase);

    List<ScheduledTask> getAndLockReadyTasksByType(String type);

    void startTransaction();

    void commitTransaction();

}
