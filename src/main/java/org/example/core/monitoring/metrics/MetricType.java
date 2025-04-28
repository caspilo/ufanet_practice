package org.example.core.monitoring.metrics;

public enum MetricType {
    SCHEDULED_TASK_COUNT("ScheduledTaskCount"),
    FAILED_TASK_COUNT("FailedTaskCount"),
    WORKER_COUNT("WorkerCount"),
    TASK_AVERAGE_TIME_EXECUTION("TaskAverageTimeExecution"),
    WORKER_AVERAGE_TIME_EXECUTION("WorkerAverageTimeExecution"),;

    private final String name;

    MetricType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
