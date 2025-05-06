package org.example.integrationtest;

import com.zaxxer.hikari.*;
import org.example.config.DataSourceConfig;
import org.example.core.monitoring.MetricRegisterer;
import org.example.core.schedulable.*;
import org.example.holder.RepositoryHolder;
import org.example.integrationtest.task.*;
import org.example.integrationtest.worker.*;

import javax.sql.DataSource;
import java.util.*;

public class WorkerAndTaskIntegrationTest {
    private static final int MAX_WORKER_THREADS = 2;
    private static final int MIN_WORKER_THREADS = 1;
    private static final int WORKER_THREAD_COUNT = 1;
    private static final int TASK_THREAD_COUNT = 1;
    private static final int BOUND_MILLIS_TO_SLEEP = 10000;

    public static void main(String[] args) {
        initDataSource();
        MetricRegisterer metricRegisterer = new MetricRegisterer();
        TestThreads workerThreads = createWorkerThreads(metricRegisterer);
        TestThreads taskThreads = createTaskThreads();
        initThreads(workerThreads, WORKER_THREAD_COUNT);
        initThreads(taskThreads, TASK_THREAD_COUNT);
    }

    private static void initThreads(TestThreads testThreads, int workerThreadCount) {
        testThreads.initThreads(workerThreadCount, BOUND_MILLIS_TO_SLEEP);
        testThreads.stoppingThreads(workerThreadCount, BOUND_MILLIS_TO_SLEEP);
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

    private static TestThreads createWorkerThreads(MetricRegisterer metricRegisterer) {
        WorkerManager workerManager = new WorkerManager(MAX_WORKER_THREADS, MIN_WORKER_THREADS,
                setupClasses(), metricRegisterer);
        return new WorkerThreads(workerManager);
    }

    private static TestThreads createTaskThreads() {
        TaskManager taskManager = new TaskManager(setupClasses(), setupParams());
        return new TaskThreads(taskManager);
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
