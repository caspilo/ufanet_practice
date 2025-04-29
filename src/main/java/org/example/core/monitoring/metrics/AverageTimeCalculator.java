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

    public double calculateAverageTimeByCategory(String category) {
        return (double) summaryTime.get(category) / summaryCount.get(category);
    }
}
