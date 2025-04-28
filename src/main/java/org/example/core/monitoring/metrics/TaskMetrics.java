package org.example.core.monitoring.metrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TaskMetrics {
    private final Map<String, Integer> scheduledTaskCount = new ConcurrentHashMap<>();
    private final Map<String, Integer> failedTaskCount = new ConcurrentHashMap<>();
    private final AverageTimeCalculator averageTimeCalculator = new AverageTimeCalculator();

    public void taskScheduled(String category) {
        scheduledTaskCount.merge(category, 1, Integer::sum);
        failedTaskCount.put(category, 0);
    }

    public void taskFailed(String category) {
        failedTaskCount.merge(category, 1, Integer::sum);
    }

    public void taskExecuted(String category, long duration) {
        averageTimeCalculator.eventHappened(category, duration);
    }

    public int getScheduledTaskCountByCategory(String category) {
        return scheduledTaskCount.get(category);
    }

    public int getFailedTaskCountByCategory(String category) {
        return failedTaskCount.get(category);
    }

    public double getTaskAverageExecutionTimeByCategory(String category) {
        return averageTimeCalculator.calculateAverageTimeByCategory(category);
    }
}
