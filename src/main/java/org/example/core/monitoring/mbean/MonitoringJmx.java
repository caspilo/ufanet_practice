package org.example.core.monitoring.mbean;

import org.example.core.monitoring.*;
import org.example.core.monitoring.metrics.*;

public class MonitoringJmx implements MonitoringJmxMBean {
    private final String category;
    private final MetricType metricType;
    private final MetricGetter metricGetter;

    public MonitoringJmx(String category, MetricType metricType, MetricGetter metricGetter) {
        this.category = category;
        this.metricType = metricType;
        this.metricGetter = metricGetter;
    }

    @Override
    public double getValue() {
        return metricGetter.getMetric(category, metricType);
    }
}
