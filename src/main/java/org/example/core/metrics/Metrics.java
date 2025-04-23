package org.example.core.metrics;

import java.util.List;
import java.util.Map;

public class Metrics implements MetricsMBean {
    @Override
    public Map<String, Integer> getCreatedTaskCount() {
        return MetricsCollector.getCreatedTaskCount();
    }

    @Override
    public Map<String, Integer> getFailedTaskCount() {
        return MetricsCollector.getFailedTaskCount();
    }

    @Override
    public Map<String, Integer> getWorkerCountByCategory() {
        return MetricsCollector.getWorkerCountByCategory();
    }

    @Override
    public Map<String, List<Long>> getExecutionTimeByCategory() {
        return MetricsCollector.getTaskExecutionTimeByCategory();
    }
}
