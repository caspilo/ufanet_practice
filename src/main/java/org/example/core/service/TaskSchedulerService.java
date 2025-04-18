package org.example.core.service;


import org.example.core.task.Schedulable;

import java.util.Map;

public interface TaskSchedulerService {

    void cancelTask(Long id);

    void scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime);

    void scheduleTask(Class schedulableClass, Map<String, String> params, String executionTime);

    void scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime);

    void scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, boolean withRetry,
                      boolean fixedRetryPolicy, Long fixDelayValue, Long delayBase, int retryCount, Long delayLimit);

    void rescheduleTask(Long id, long delay);
}