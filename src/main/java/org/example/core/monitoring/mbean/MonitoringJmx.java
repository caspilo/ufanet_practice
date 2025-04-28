package org.example.core.monitoring.mbean;

import org.example.core.monitoring.*;
import org.example.core.monitoring.metrics.*;

public class MonitoringJmx implements MonitoringJmxMBean {
    private final String category;
    private final MetricType metricType;
    private final GetMetricStrategy getMetricStrategy;

    public MonitoringJmx(String category, MetricType metricType, GetMetricStrategy getMetricStrategy) {
        this.category = category;
        this.metricType = metricType;
        this.getMetricStrategy = getMetricStrategy;
    }

    @Override
    public double getValue() {
        return getMetricStrategy.getMetric(category, metricType);
    }
}
