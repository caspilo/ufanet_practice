package org.example.integrationtest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.monitoring.mbean.MonitoringJmx;
import org.example.core.schedulable.DoSomething;
import org.example.core.schedulable.PushNotification;
import org.example.core.schedulable.Schedulable;
import org.example.holder.RepositoryHolder;

import javax.management.*;
import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

public class WorkerAndTaskIntegrationTest {
    private static final int MAX_WORKER_THREADS = 3;
    private static final int MIN_WORKER_THREADS = 1;
    private static final int WORKER_THREAD_COUNT = 3;
    private static final int TASK_THREAD_COUNT = 5;
    private static final int BOUND_MILLIS_TO_SLEEP = 10000;

    public static void main(String[] args) {
        initDataSource();
        TestThreads workerThreads = new WorkerThreads(MAX_WORKER_THREADS, MIN_WORKER_THREADS, setupCategories());
        TestThreads taskThreads = new TaskThreads(setupClasses(), setupParams());
        workerThreads.initThreads(WORKER_THREAD_COUNT, BOUND_MILLIS_TO_SLEEP);
        taskThreads.initThreads(TASK_THREAD_COUNT, BOUND_MILLIS_TO_SLEEP);
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

    private static Map<Integer, String> setupCategories() {
        Map<Integer, String> categories = new HashMap<>();
        categories.put(0, "PushNotification");
        categories.put(1, "DoSomething");
        return categories;
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
