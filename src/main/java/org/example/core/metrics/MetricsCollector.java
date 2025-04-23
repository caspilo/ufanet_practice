package org.example.core.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsCollector {
    private static final Map<String, Integer> workerCountByCategory = new ConcurrentHashMap<>();
    private static final Map<String, Integer> createdTaskCountByCategory = new ConcurrentHashMap<>();
    private static final Map<String, Integer> failedTaskCountByCategory = new ConcurrentHashMap<>();
    private static final Map<String, List<Long>> taskExecutionTimeByCategory = new ConcurrentHashMap<>();

    public static void workerCreated(String category) {
        workerCountByCategory.merge(category, 1, Integer::sum);
    }

    public static void workerDeleted(String category) {
        workerCountByCategory.merge(category, -1, Integer::sum);
    }

    public static void taskScheduled(String category) {
        createdTaskCountByCategory.merge(category, 1, Integer::sum);
    }

    public static void taskFailed(String category) {
        failedTaskCountByCategory.merge(category, 1, Integer::sum);
    }

    public static void taskExecutionTime(String category, long millisExecutionTime) {
        if (taskExecutionTimeByCategory.containsKey(category)) {
            taskExecutionTimeByCategory.get(category).add(millisExecutionTime);
        } else {
            List<Long> millisExecutionTimeList = new ArrayList<>();
            millisExecutionTimeList.add(millisExecutionTime);
            taskExecutionTimeByCategory.put(category, millisExecutionTimeList);
        }
    }

    public static Map<String, Integer> getCreatedTaskCount() {
        return createdTaskCountByCategory;
    }

    public static Map<String, Integer> getFailedTaskCount() {
        return failedTaskCountByCategory;
    }

    public static Map<String, Integer> getWorkerCountByCategory() {
        return workerCountByCategory;
    }

    public static Map<String, List<Long>> getTaskExecutionTimeByCategory() {
        return taskExecutionTimeByCategory;
    }
}
