package org.example.core.service;


import org.example.core.task.Schedulable;

import java.util.Map;

public interface TaskSchedulerService {

    void cancelTask(Long id, String category);

    Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime);

    Long scheduleTask(Class schedulableClass, Map<String, String> params, String executionTime);

    Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime);

    Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime, boolean withRetry,
                      boolean fixedRetryPolicy, Long fixDelayValue, Long delayBase, int retryCount, Long delayLimit);

    Long scheduleTask(Class objectClass, Map<String, String> params, String executionTime, boolean withRetry,
                      boolean fixedRetryPolicy, Long fixDelayValue, Long delayBase, int retryCount, Long delayLimit);

    Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, boolean withRetry,
                      boolean fixedRetryPolicy, Long fixDelayValue, Long delayBase, int retryCount, Long delayLimit);
}