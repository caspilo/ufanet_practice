package org.example.core.monitoring;

import org.example.core.monitoring.metrics.MetricType;

public interface MetricGetter {
    double getMetric(String category, MetricType metricType);
}
