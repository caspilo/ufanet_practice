package org.example.core.monitoring;

import org.example.core.monitoring.mbean.MonitoringJmx;
import org.example.core.monitoring.metrics.MetricType;
import org.example.worker.TaskWorker;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.*;

public class MetricRegisterer {
    private static final String BEAN_TYPE = "MonitoringJmxMBean";

    private final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

    public void registerMetric(String category, MetricType metricType, TaskWorker worker) {
        try {
            tryRegisterMBean(category, metricType, worker);
        } catch (InstanceAlreadyExistsException ignored) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void tryRegisterMBean(String category, MetricType metricType, TaskWorker worker)
                throws MalformedObjectNameException, InstanceAlreadyExistsException,
                MBeanRegistrationException, NotCompliantMBeanException {
        String objectName = buildObjectName(category, metricType, worker.getWorkerId());
        ObjectName name = new ObjectName(objectName);
        MonitoringJmx jmx = new MonitoringJmx(metricType, worker);
        mbs.registerMBean(jmx, name);
    }

    private String buildObjectName(String category, MetricType metricType, UUID workerId) {
        String metricName = metricType.toString() + "By" + category + "-" + workerId;
        return "org.example:type=" + BEAN_TYPE + ",name=" + metricName;
    }
}
