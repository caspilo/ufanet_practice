package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.entity.ScheduledTask;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.TaskRepository;
import org.example.core.service.TaskService;
import org.example.core.service.TaskServiceDataBase;
import org.example.test.DoSomething;
import org.example.worker.TaskWorker;
import org.example.worker.TaskWorkerPool;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static TaskRepository taskRepository;

    public static TaskService taskService;

    public static void main(String[] args) throws Exception {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        DataSource dataSource = new HikariDataSource(config);

        taskRepository = new JdbcTaskRepository(dataSource);
        taskService = new TaskServiceDataBase(taskRepository);

        //taskRepository.save(new ScheduledTask());

        //TaskWorkerPool taskWorkerPool = new TaskWorkerPool(taskService);
        //taskWorkerPool.initWorkers(Map.of("DoSomething", 1));
        //taskWorkerPool.initWorkers(Map.of("DoSomething", 1));

        // scheduler.scheduleTask(..., "2025-04-15 13:00:00", ...)
        // scheduler.scheduleTask(..., TimeClass(2025, 4, 15, 13, 0, 0), ...)
    }
}