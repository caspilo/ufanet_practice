package org.example.core.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricsCollector {
    private static int createdWorkerCount = 0;
    private static int deletedWorkerCount = 0;

    private static int createdTaskCount = 0;
    private static int deletedTaskCount = 0;
    private static int readyTaskCount = 0;

    private static final Map<String, List<Integer>> executionTimeByCategory = new HashMap<>();

    public static void workerCreated() {
        createdWorkerCount++;
    }

    public static void workerDeleted() {
        deletedWorkerCount++;
    }

    public static void taskCreated() {
        createdTaskCount++;
    }

    public static void taskDeleted() {
        deletedTaskCount++;
    }

    public static void taskReady() {
        readyTaskCount++;
    }

    public static void taskExecutionTime(String category, int millisExecutionTime) {
        if (executionTimeByCategory.containsKey(category)) {
            executionTimeByCategory.get(category).add(millisExecutionTime);
        } else {
            List<Integer> millisExecutionTimeList = new ArrayList<>();
            millisExecutionTimeList.add(millisExecutionTime);
            executionTimeByCategory.put(category, millisExecutionTimeList);
        }
    }

    public static int getCreatedWorkerCount() {
        return createdWorkerCount;
    }

    public static int getDeletedWorkerCount() {
        return deletedWorkerCount;
    }

    public static int getCreatedTaskCount() {
        return createdTaskCount;
    }

    public static int getDeletedTaskCount() {
        return deletedTaskCount;
    }

    public static int getReadyTaskCount() {
        return readyTaskCount;
    }

    public static Map<String, List<Integer>> getExecutionTimeByCategory() {
        return new HashMap<>(executionTimeByCategory);
    }
}
