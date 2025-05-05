package org.example.core.monitoring.metrics;

public enum MetricType {
    TAKEN_TASK_COUNT("TakenTaskCount"),
    COMPLETED_TASK_COUNT("CompletedTaskCount"),
    TASK_AVERAGE_TIME_EXECUTION("TaskAverageTimeExecution"),
    WORKER_AVERAGE_TIME_WAITING("WorkerAverageTimeWaiting"),;

    private final String name;

    MetricType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
