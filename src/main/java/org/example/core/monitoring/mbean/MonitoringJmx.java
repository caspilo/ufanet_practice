package org.example.core.monitoring.mbean;

import org.example.core.monitoring.MetricsCollector;
import org.example.core.monitoring.metrics.*;

public class MonitoringJmx implements MonitoringJmxMBean {
    private final String category;
    private final MetricType metricType;

    public MonitoringJmx(String category, MetricType metricType) {
        this.category = category;
        this.metricType = metricType;
    }

    @Override
    public double getValue() {
        switch (metricType) {
            case WORKER_COUNT -> {
                return MetricsCollector.getWorkerCountByCategory(category);
            }
            case FAILED_TASK_COUNT -> {
                return MetricsCollector.getFailedTaskCountByCategory(category);
            }
            case SCHEDULED_TASK_COUNT -> {
                return MetricsCollector.getScheduledTaskCountByCategory(category);
            }
            case WORKER_AVERAGE_TIME_EXECUTION -> {
                return MetricsCollector.getWorkerAverageWaitTimeByCategory(category);
            }
            case TASK_AVERAGE_TIME_EXECUTION -> {
                return MetricsCollector.getTaskAverageExecutionTimeByCategory(category);
            }
            default -> throw new UnsupportedOperationException("Unsupported metricType: " + metricType);
        }
    }
}
