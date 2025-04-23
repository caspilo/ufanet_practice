package org.example.core.metrics;

import java.util.List;
import java.util.Map;

public interface MetricsMBean {
    Map<String, Integer> getCreatedTaskCount();
    Map<String, Integer> getFailedTaskCount();
    Map<String, Integer> getWorkerCountByCategory();
    Map<String, List<Long>> getExecutionTimeByCategory();
}
