package org.example.integrationtest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.schedulable.DoSomething;
import org.example.core.schedulable.PushNotification;
import org.example.core.service.task.TaskScheduler;
import org.example.core.service.task.TaskSchedulerService;
import org.example.holder.RepositoryHolder;
import org.example.worker.TaskWorkerPool;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WorkerIntegrationTest {
    private static final int WORKER_THREAD_COUNT = 3;
    private static final int MAX_WORKER_THREADS = 3;
    private static final int MIN_WORKER_THREADS = 1;
    private static final int TASK_THREAD_COUNT = 5;

    private static TaskSchedulerService taskScheduler;
    private static TaskWorkerPool workerPool;

    private static Map<Integer, String> categories;
    private static Map<Integer, String> classes;
    private static Map<String, String> params;
    private static Random random = new Random();

    static {
        setupCategories();
        setupClasses();
        setupParams();
    }

    private static void setupCategories() {
        categories = new HashMap<>();
        categories.put(0, "PushNotification");
        categories.put(1, "DoSomething");
    }

    private static void setupClasses() {
        classes = new HashMap<>();
        classes.put(0, PushNotification.class.getName());
        classes.put(1, DoSomething.class.getName());
    }

    private static void setupParams() {
        params = new HashMap<>();
        params.put("ID", "1");
        params.put("message", "Hello World");
    }

    public static void main(String[] args) {
        initDataSource();
        workerPool = new TaskWorkerPool();
        taskScheduler = new TaskScheduler();

        initTaskThreads();
        initWorkerThreads();
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

    private static void initWorkerThreads() {
        Thread[] workerThreads = new Thread[WORKER_THREAD_COUNT];
        for (int i = 0; i < WORKER_THREAD_COUNT; i++) {
            workerThreads[i] = new Thread(() -> {
                initWorkerWithRandomValues();
            });
            workerThreads[i].start();
        }
    }

    private static void initTaskThreads() {
        Thread[] taskThreads = new Thread[TASK_THREAD_COUNT];
        for (int i = 0; i < TASK_THREAD_COUNT; i++) {
            taskThreads[i] = new Thread(() -> {
                initRandomTask();
            });
            taskThreads[i].start();
        }
    }

    private static void initWorkerWithRandomValues() {
        String randomCategory = categories.get(random.nextInt(categories.size()));
        int randomThreadCount = WorkerIntegrationTest.random.nextInt(MAX_WORKER_THREADS) + MIN_WORKER_THREADS;
        workerPool.initWorker(randomCategory, randomThreadCount);
    }

    private static void initRandomTask() {
        String randomCategory = classes.get(random.nextInt(classes.size()));
        String executionTime = Timestamp.valueOf(LocalDateTime.now()).toString();
        taskScheduler.scheduleTask(randomCategory, params, executionTime);
    }
}
