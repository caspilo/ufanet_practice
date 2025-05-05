package org.example.core.monitoring.mbean;

import org.example.core.monitoring.*;
import org.example.core.monitoring.metrics.*;
import org.example.worker.TaskWorker;

import java.util.Map;

public class MonitoringJmx implements MonitoringJmxMBean {
    private final MetricType metricType;
    private final TaskWorker taskWorker;

    public MonitoringJmx(MetricType metricType, TaskWorker taskWorker) {
        this.metricType = metricType;
        this.taskWorker = taskWorker;
    }

    @Override
    public double getValue() {
        return taskWorker.getMetric(metricType);
    }
}
