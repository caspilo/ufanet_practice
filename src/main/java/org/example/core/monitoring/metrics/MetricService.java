package org.example.core.monitoring.metrics;

import org.example.core.monitoring.MetricRegisterer;
import org.example.worker.TaskWorker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricService {
    private final MetricRegisterer metricRegisterer;
    private final String category;
    private final TaskWorker worker;
    private final AverageTimeCalculator workerAverageTimeWaiting = new AverageTimeCalculator();
    private final AverageTimeCalculator taskAverageExecutionTime = new AverageTimeCalculator();
    private final Map<MetricType, Integer> metrics = new ConcurrentHashMap<>();


    public MetricService(MetricRegisterer metricRegisterer, String category, TaskWorker worker) {
        this.metricRegisterer = metricRegisterer;
        this.category = category;
        this.worker = worker;
    }

    public void registerMetrics() {
        metricRegisterer.registerMetric(category, MetricType.WORKER_AVERAGE_TIME_WAITING, worker);
        metricRegisterer.registerMetric(category, MetricType.TASK_AVERAGE_TIME_EXECUTION, worker);
        metricRegisterer.registerMetric(category, MetricType.TAKEN_TASK_COUNT, worker);
        metricRegisterer.registerMetric(category, MetricType.COMPLETED_TASK_COUNT, worker);

        metrics.put(MetricType.TAKEN_TASK_COUNT, 0);
        metrics.put(MetricType.COMPLETED_TASK_COUNT, 0);
    }

    public void workerWaited(long duration) {
        workerAverageTimeWaiting.eventHappened(duration);
    }

    public void taskExecuted(long duration) {
        taskAverageExecutionTime.eventHappened(duration);
    }

    public void taskCompleted() {
        metrics.merge(MetricType.COMPLETED_TASK_COUNT, 1, Integer::sum);
    }

    public void taskTaken() {
        metrics.merge(MetricType.TAKEN_TASK_COUNT, 1, Integer::sum);
    }

    public double getMetric(MetricType metricType) {
        return switch (metricType) {
            case WORKER_AVERAGE_TIME_WAITING -> workerAverageTimeWaiting.calculateAverageTimeByCategory();
            case TASK_AVERAGE_TIME_EXECUTION -> taskAverageExecutionTime.calculateAverageTimeByCategory();
            case COMPLETED_TASK_COUNT, TAKEN_TASK_COUNT -> metrics.get(metricType);
        };
    }
}
