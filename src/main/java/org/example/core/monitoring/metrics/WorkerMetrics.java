package org.example.core.monitoring.metrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkerMetrics {
    private static final Map<String, Integer> workerCountByCategory = new ConcurrentHashMap<>();
    private static final AverageTimeCalculator averageTimeCalculator = new AverageTimeCalculator();

    public static void workerCreated(String category) {
        workerCountByCategory.merge(category, 1, Integer::sum);
    }

    public static void workerDeleted(String category) {
        workerCountByCategory.merge(category, -1, Integer::sum);
    }

    public static void workerWaited(String category, long duration) {
        averageTimeCalculator.eventHappened(category, duration);
    }

    public static int getWorkerCountByCategory(String category) {
        return workerCountByCategory.get(category);
    }

    public static double getWorkerAverageWaitTimeByCategory(String category) {
        return averageTimeCalculator.calculateAverageTimeByCategory(category);
    }
}
