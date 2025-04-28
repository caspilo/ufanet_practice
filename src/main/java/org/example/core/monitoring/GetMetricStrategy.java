package org.example.core.monitoring;

import org.example.core.monitoring.metrics.MetricType;

public interface GetMetricStrategy {
    double getMetric(String category, MetricType metricType);
}
