package org.example.core.monitoring.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskMetrics {
    private static final Map<String, Integer> scheduledTaskCount = new ConcurrentHashMap<>();
    private static final Map<String, Integer> failedTaskCount = new ConcurrentHashMap<>();
    private static final AverageTimeCalculator averageTimeCalculator = new AverageTimeCalculator();

    public static void taskScheduled(String category) {
        scheduledTaskCount.merge(category, 1, Integer::sum);
        failedTaskCount.put(category, 0);
    }

    public static void taskFailed(String category) {
        failedTaskCount.merge(category, 1, Integer::sum);
    }

    public static void taskExecuted(String category, long duration) {
        averageTimeCalculator.eventHappened(category, duration);
    }

    public static int getScheduledTaskCountByCategory(String category) {
        return scheduledTaskCount.get(category);
    }

    public static int getFailedTaskCountByCategory(String category) {
        return failedTaskCount.get(category);
    }

    public static double getTaskAverageExecutionTimeByCategory(String category) {
        return averageTimeCalculator.calculateAverageTimeByCategory(category);
    }
}
