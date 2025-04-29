package org.example.core.monitoring.mbean;

import org.example.core.monitoring.*;
import org.example.core.monitoring.metrics.*;

import java.util.Map;

public class MonitoringJmx implements MonitoringJmxMBean {
    private final String category;
    private final MetricType metricType;
    private final Map<MetricType, MetricHandler> metricHandler;

    public MonitoringJmx(String category, MetricType metricType, Map<MetricType, MetricHandler> metricHandler) {
        this.category = category;
        this.metricType = metricType;
        this.metricHandler = metricHandler;
    }

    @Override
    public double getValue() {
        return metricHandler.get(metricType).getMetric(category);
    }
}
