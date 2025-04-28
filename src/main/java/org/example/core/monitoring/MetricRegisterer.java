package org.example.core.monitoring;

import org.example.core.monitoring.mbean.MonitoringJmx;
import org.example.core.monitoring.metrics.MetricType;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class MetricRegisterer {
    private static final String BEAN_TYPE = "MonitoringJmxMBean";

    private final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    private final MetricGetter metricGetter;
    private String objectName;

    public MetricRegisterer(MetricGetter metricGetter) {
        this.metricGetter = metricGetter;
    }

    public void registerMetric(String category, MetricType metricType) {
        try {
            tryRegisterMBean(category, metricType);
        } catch (InstanceAlreadyExistsException e) {
            System.out.println(objectName + " already exists");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void tryRegisterMBean(String category, MetricType metricType)
                throws MalformedObjectNameException, InstanceAlreadyExistsException,
                MBeanRegistrationException, NotCompliantMBeanException {
        objectName = buildObjectName(category, metricType);
        ObjectName name = new ObjectName(objectName);
        MonitoringJmx jmx = new MonitoringJmx(category, metricType, metricGetter);
        mbs.registerMBean(jmx, name);
    }

    private String buildObjectName(String category, MetricType metricType) {
        String metricName = metricType.toString() + "By" + category;
        return "org.example:type=" + BEAN_TYPE + ",name=" + metricName;
    }
}
