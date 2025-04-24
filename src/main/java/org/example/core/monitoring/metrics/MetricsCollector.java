package org.example.core.monitoring.metrics;

import java.util.*;

public class MetricsCollector {
    private static final WorkerMetrics WORKER_METRICS = new WorkerMetrics();
    private static final TaskMetrics TASK_METRICS = new TaskMetrics();

    public static void workerCreated(String category) {
        WORKER_METRICS.workerCreated(category);
    }

    public static void workerDeleted(String category) {
        WORKER_METRICS.workerDeleted(category);
    }

    public static void taskScheduled(String category) {
        TASK_METRICS.taskScheduled(category);
    }

    public static void taskFailed(String category) {
        TASK_METRICS.taskFailed(category);
    }

    public static void taskExecuted(String category, long duration) {
        TASK_METRICS.taskExecuted(category, duration);
    }

    public static void workerWaited(String category, long duration) {
        WORKER_METRICS.workerWaited(category, duration);
    }

    public static Map<String, Integer> getScheduledTaskCount() {
        return TASK_METRICS.getScheduledTaskCount();
    }

    public static Map<String, Integer> getFailedTaskCount() {
        return TASK_METRICS.getFailedTaskCount();
    }

    public static Map<String, Integer> getWorkerCountByCategory() {
        return WORKER_METRICS.getWorkerCountByCategory();
    }

    public static Map<String, Double> getTaskAverageExecutionTime() {
        return TASK_METRICS.getTaskAverageExecutionTime();
    }

    public static Map<String, Double> getWorkerAverageWaitTime() {
        return WORKER_METRICS.getWorkerAverageWaitTime();
    }
}
