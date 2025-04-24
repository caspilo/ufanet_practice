package org.example.core.monitoring.metrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TaskMetrics {
    private final Map<String, Integer> scheduledTaskCount = new ConcurrentHashMap<>();
    private final Map<String, Integer> failedTaskCount = new ConcurrentHashMap<>();
    private final AverageTimeCalculator averageTimeCalculator = new AverageTimeCalculator();

    public void taskScheduled(String category) {
        scheduledTaskCount.merge(category, 1, Integer::sum);
    }

    public void taskFailed(String category) {
        failedTaskCount.merge(category, 1, Integer::sum);
    }

    public void taskExecuted(String category, long duration) {
        averageTimeCalculator.eventHappened(category, duration);
    }

    public Map<String, Integer> getScheduledTaskCount() {
        return scheduledTaskCount;
    }

    public Map<String, Integer> getFailedTaskCount() {
        return failedTaskCount;
    }

    public Map<String, Double> getTaskAverageExecutionTime() {
        return averageTimeCalculator.calculateAverageTime();
    }
}
