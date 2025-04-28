package org.example.core.monitoring;

import org.example.core.monitoring.metrics.*;

public class MetricsCollector {
    /*
     * Создание бина в BeanRegisterer
     * Метод регистрации нового бина BeanRegisterer вызывается в MetricsCollector
     * В метод передаётся: категория бина.
     * Значения бин будет подтягивать из MetricsCollector
     */

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

    public static int getScheduledTaskCountByCategory(String category) {
        return TASK_METRICS.getScheduledTaskCountByCategory(category);
    }

    public static int getFailedTaskCountByCategory(String category) {
        return TASK_METRICS.getFailedTaskCountByCategory(category);
    }

    public static int getWorkerCountByCategory(String category) {
        return WORKER_METRICS.getWorkerCountByCategory(category);
    }

    public static double getTaskAverageExecutionTimeByCategory(String category) {
        return TASK_METRICS.getTaskAverageExecutionTimeByCategory(category);
    }

    public static double getWorkerAverageWaitTimeByCategory(String category) {
        return WORKER_METRICS.getWorkerAverageWaitTimeByCategory(category);
    }
}
