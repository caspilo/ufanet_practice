package org.example.core.monitoring.mbean;

import org.example.core.monitoring.metrics.MetricsCollector;

import java.util.Map;

public class MonitoringJmx implements MonitoringJmxMBean {
    @Override
    public Map<String, Integer> getScheduledTaskCount() {
        return MetricsCollector.getScheduledTaskCount();
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
    public Map<String, Double> getTaskAverageExecutionTime() {
        return MetricsCollector.getTaskAverageExecutionTime();
    }

    @Override
    public Map<String, Double> getWorkerAverageWaitTime() {
        return MetricsCollector.getWorkerAverageWaitTime();
    }
}
