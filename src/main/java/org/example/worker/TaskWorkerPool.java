package org.example.worker;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.repository.DelayRepository;
import org.example.core.repository.JdbcDelayRepository;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.TaskRepository;
import org.example.core.service.DatabaseTaskActions;
import org.example.core.service.TaskScheduler;
import org.example.core.service.TaskSchedulerService;
import org.example.core.service.TaskService;
import org.example.core.service.delay.DelayPolicy;
import org.example.core.service.delay.DelayService;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskWorkerPool {

    private static final List<TaskWorker> taskWorkers = new ArrayList<>();

    private final DataSource dataSource;

    public static final Map<String, TaskRepository> taskRepositories = Collections.synchronizedMap(new HashMap<>());

    public static final Map<String, DelayRepository> delayRepositories = Collections.synchronizedMap(new HashMap<>());

    private static final List<ExecutorService> executorServices = new ArrayList<>();

    private final TaskSchedulerService taskScheduler;

    public TaskWorkerPool(TaskSchedulerService taskScheduler) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        this.dataSource = new HikariDataSource(config);
        this.taskScheduler = taskScheduler;
    }

    public TaskWorkerPool(DataSource dataSource, TaskSchedulerService taskScheduler) {
        this.dataSource = dataSource;
        this.taskScheduler = taskScheduler;
    }

    public void initWorkers(Map<String, Integer> categoriesAndThreads) {
        for (Map.Entry<String, Integer> entry : categoriesAndThreads.entrySet()) {

            String category = entry.getKey();
            int threadsCount = entry.getValue();

            initWorker(category, threadsCount);
        }
    }

    public void initWorker(String category, int threadsCount) {

        ExecutorService threadPool = Executors.newFixedThreadPool(threadsCount);

        if (!taskRepositories.containsKey(category)) {

            taskRepositories.put(category, new JdbcTaskRepository(dataSource, category));
            delayRepositories.put(category, new JdbcDelayRepository(dataSource, category));
        }

        TaskService taskService = new DatabaseTaskActions(taskRepositories.get(category));
        DelayService delayService = new DelayPolicy(delayRepositories.get(category));

        threadPool.submit(new TaskWorker(taskService, taskScheduler, delayService));
        System.out.println("Init worker with category " + category + ", with " + threadsCount + " thread(s) " + threadPool);
    }
}
