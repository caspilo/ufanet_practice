package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.test.Schedulable;

import java.util.List;
import java.util.Map;

public interface TaskService {

    void cancelTask(long id);

    public Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime, double delayBase);

    public Long scheduleTask(Class schedulableClass, Map<String, String> params, String executionTime, double delayBase);

    public Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, double delayBase);
}
