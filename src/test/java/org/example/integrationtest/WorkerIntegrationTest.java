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
    private static final int BOUND_MILLIS_TO_SLEEP = 10000;
    private static final Random RANDOM = new Random();

    private static TaskSchedulerService taskScheduler;
    private static TaskWorkerPool workerPool;

    private static Map<Integer, String> categories;
    private static Map<Integer, String> classes;
    private static Map<String, String> params;

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
                while(true) {
                    initWorkerWithRandomValues();
                    sleep();
                }
            });
            workerThreads[i].setName("Worker-" + i);
            workerThreads[i].start();
        }
    }

    private static void initTaskThreads() {
        Thread[] taskThreads = new Thread[TASK_THREAD_COUNT];
        for (int i = 0; i < TASK_THREAD_COUNT; i++) {
            taskThreads[i] = new Thread(() -> {
                while (true) {
                    initRandomTask();
                    sleep();
                }
            });
            taskThreads[i].setName("Task-" + i);
            taskThreads[i].start();
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(RANDOM.nextInt(BOUND_MILLIS_TO_SLEEP));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initWorkerWithRandomValues() {
        String randomCategory = categories.get(RANDOM.nextInt(categories.size()));
        int randomThreadCount = WorkerIntegrationTest.RANDOM.nextInt(MAX_WORKER_THREADS) + MIN_WORKER_THREADS;
        workerPool.initWorker(randomCategory, randomThreadCount);
        System.out.println("Создан новый Worker:" +
                "\nКатегория - " + randomCategory +
                "\nКол-во потоков - " + randomThreadCount +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата создания - " + LocalDateTime.now());
    }

    private static void initRandomTask() {
        String randomClass = classes.get(RANDOM.nextInt(classes.size()));
        String executionTime = Timestamp.valueOf(LocalDateTime.now()).toString();
        taskScheduler.scheduleTask(randomClass, params, executionTime);
        System.out.println("Создан новый Task:" +
                "\nКласс - " + randomClass +
                "\nВремя выполнения - " + executionTime +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата создания - " + LocalDateTime.now());
    }
}
