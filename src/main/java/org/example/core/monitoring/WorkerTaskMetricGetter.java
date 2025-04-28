package org.example.core.monitoring;

import org.example.core.monitoring.metrics.*;

public class WorkerTaskMetricGetter implements MetricGetter {
    @Override
    public double getMetric(String category, MetricType metricType) {
        switch (metricType) {
            case WORKER_COUNT -> {
                return WorkerMetrics.getWorkerCountByCategory(category);
            }
            case FAILED_TASK_COUNT -> {
                return TaskMetrics.getFailedTaskCountByCategory(category);
            }
            case SCHEDULED_TASK_COUNT -> {
                return TaskMetrics.getScheduledTaskCountByCategory(category);
            }
            case WORKER_AVERAGE_TIME_EXECUTION -> {
                return WorkerMetrics.getWorkerAverageWaitTimeByCategory(category);
            }
            case TASK_AVERAGE_TIME_EXECUTION -> {
                return TaskMetrics.getTaskAverageExecutionTimeByCategory(category);
            }
            default -> throw new UnsupportedOperationException("Unsupported metricType: " + metricType);
        }
    }
}
