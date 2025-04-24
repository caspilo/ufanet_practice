package org.example.core.monitoring.metrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkerMetrics {
    private final Map<String, Integer> workerCountByCategory = new ConcurrentHashMap<>();
    private final AverageTimeCalculator averageTimeCalculator = new AverageTimeCalculator();

    public void workerCreated(String category) {
        workerCountByCategory.merge(category, 1, Integer::sum);
    }

    public void workerDeleted(String category) {
        workerCountByCategory.merge(category, -1, Integer::sum);
    }

    public void workerWaited(String category, long duration) {
        averageTimeCalculator.eventHappened(category, duration);
    }

    public Map<String, Integer> getWorkerCountByCategory() {
        return workerCountByCategory;
    }

    public Map<String, Double> getWorkerAverageWaitTime() {
        return averageTimeCalculator.calculateAverageTime();
    }
}
