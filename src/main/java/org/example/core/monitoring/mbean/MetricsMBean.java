package org.example.core.monitoring.mbean;

import java.util.Map;

public interface MetricsMBean {
    Map<String, Integer> getScheduledTaskCount();
    Map<String, Integer> getFailedTaskCount();
    Map<String, Integer> getWorkerCountByCategory();
    Map<String, Double> getTaskAverageExecutionTime();
    Map<String, Double> getWorkerAverageWaitTime();
}
