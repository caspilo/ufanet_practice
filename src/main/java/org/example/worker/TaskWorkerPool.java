package org.example.worker;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.repository.DelayRepository;
import org.example.core.repository.JdbcDelayRepository;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.TaskRepository;
import org.example.core.service.CurrentServicesAndRepositories;
import org.example.core.service.DatabaseTaskActions;
import org.example.core.service.TaskScheduler;
import org.example.core.service.TaskService;
import org.example.core.service.delay.DelayPolicy;
import org.example.core.service.delay.DelayService;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskWorkerPool {

    private final DataSource dataSource;

    private final TaskService taskService;

    private final DelayService delayService;


    public TaskWorkerPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        this.dataSource = new HikariDataSource(config);
        this.taskService = new DatabaseTaskActions(new JdbcTaskRepository(dataSource));
        this.delayService = new DelayPolicy(new JdbcDelayRepository(dataSource));
    }

    public TaskWorkerPool(DataSource dataSource) {
        this.dataSource = dataSource;
        this.taskService = new DatabaseTaskActions(new JdbcTaskRepository(dataSource));
        this.delayService = new DelayPolicy(new JdbcDelayRepository(dataSource));
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

        threadPool.submit(new TaskWorker(taskService, delayService, category));
        System.out.println("Init worker with category " + category + ", with " + threadsCount + " thread(s) " + threadPool);
    }
}