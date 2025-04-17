package org.example.core.service;


import org.example.core.task.Schedulable;

import java.util.Map;

public interface TaskSchedulerService {

    void cancelTask(Long id);

    Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime);

    Long scheduleTask(Class schedulableClass, Map<String, String> params, String executionTime);

    Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime);

    Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, boolean withRetry,
                      boolean fixedRetryPolicy, Long fixDelayValue, Long delayBase, int retryCount, Long delayLimit);

    void rescheduleTask(Long id, long delay);
}