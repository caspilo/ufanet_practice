package org.example.core.monitoring.metrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AverageTimeCalculator {
    private final Map<String, Long> summaryTime = new ConcurrentHashMap<>();
    private final Map<String, Integer> summaryCount = new ConcurrentHashMap<>();

    public void eventHappened(String category, long duration) {
        summaryCount.merge(category, 1, Integer::sum);
        summaryTime.merge(category, duration, Long::sum);
    }

    public Map<String, Double> calculateAverageTime() {
        Map<String, Double> averageTimeByCategory = new HashMap<>();
        for (var category : summaryCount.keySet()) {
            double averageTime = (double) summaryTime.get(category) / summaryCount.get(category);
            averageTimeByCategory.put(category, averageTime);
        }
        return averageTimeByCategory;
    }
}
