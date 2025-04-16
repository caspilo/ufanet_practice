package org.example.core.service;

import org.example.test.Schedulable;

import java.util.Map;

public interface TaskSchedulerService {

    void cancelTask(Long id);

    Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime, double delayBase);

    Long scheduleTask(Class schedulableClass, Map<String, String> params, String executionTime, double delayBase);

    Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, double delayBase);

    Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, double delayBase,
                      boolean withRetry, boolean fixedRetryPolicy, double fixDelayValue, int retryCount, int upLimit);

    void rescheduleTask(Long id, long delay);
}