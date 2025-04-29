package org.example.integrationtest;

import com.zaxxer.hikari.*;
import org.example.config.DataSourceConfig;
import org.example.core.monitoring.*;
import org.example.core.monitoring.metrics.*;
import org.example.core.schedulable.*;
import org.example.holder.RepositoryHolder;

import javax.sql.DataSource;
import java.util.*;

public class WorkerAndTaskIntegrationTest {
    private static final int MAX_WORKER_THREADS = 3;
    private static final int MIN_WORKER_THREADS = 1;
    private static final int WORKER_THREAD_COUNT = 3;
    private static final int TASK_THREAD_COUNT = 5;
    private static final int BOUND_MILLIS_TO_SLEEP = 10000;

    public static void main(String[] args) {
        initDataSource();
        Map<MetricType, MetricHandler> metricHandlers = createAndSetupMetricHandlers();
        MetricRegisterer metricRegisterer = new MetricRegisterer(metricHandlers);
        TestThreads workerThreads = new WorkerThreads(MAX_WORKER_THREADS, MIN_WORKER_THREADS,
                setupClasses(), metricRegisterer);
        TestThreads taskThreads = new TaskThreads(setupClasses(), setupParams(), metricRegisterer);
        workerThreads.initThreads(WORKER_THREAD_COUNT, BOUND_MILLIS_TO_SLEEP);
        taskThreads.initThreads(TASK_THREAD_COUNT, BOUND_MILLIS_TO_SLEEP);
    }

    private static Map<MetricType, MetricHandler> createAndSetupMetricHandlers() {
        Map<MetricType, MetricHandler> metricHandlers = new HashMap<>();
        metricHandlers.put(MetricType.FAILED_TASK_COUNT, TaskMetrics::getFailedTaskCountByCategory);
        metricHandlers.put(MetricType.TASK_AVERAGE_TIME_EXECUTION, TaskMetrics::getTaskAverageExecutionTimeByCategory);
        metricHandlers.put(MetricType.SCHEDULED_TASK_COUNT, TaskMetrics::getScheduledTaskCountByCategory);
        metricHandlers.put(MetricType.WORKER_COUNT, WorkerMetrics::getWorkerCountByCategory);
        metricHandlers.put(MetricType.WORKER_AVERAGE_TIME_EXECUTION, WorkerMetrics::getWorkerAverageWaitTimeByCategory);
        return metricHandlers;
    }

    private static void initDataSource() {
        DataSource dataSource = createDataSource();
        RepositoryHolder.init(dataSource);
    }

    private static DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        return new HikariDataSource(config);
    }

    private static Map<Integer, Class<? extends Schedulable>> setupClasses() {
        Map<Integer, Class<? extends Schedulable>> classes = new HashMap<>();
        classes.put(0, PushNotification.class);
        classes.put(1, DoSomething.class);
        return classes;
    }

    private static Map<String, String> setupParams() {
        Map<String, String> params = new HashMap<>();
        params.put("ID", "1");
        params.put("message", "Hello World");
        return params;
    }
}
