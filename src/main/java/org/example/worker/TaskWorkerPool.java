package org.example.worker;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.TaskRepository;
import org.example.core.service.DatabaseTaskActions;
import org.example.core.service.TaskScheduler;
import org.example.core.service.TaskSchedulerService;
import org.example.core.service.TaskService;
import org.example.core.service.delay.DelayProcedures;
import org.example.core.service.delay.DelayService;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskWorkerPool {

    private static final List<TaskWorker> taskWorkers = new ArrayList<>();

    private final DataSource dataSource;

    private final Map<String, TaskRepository> repositories = Collections.synchronizedMap(new HashMap<>());

    private static final List<ExecutorService> executorServices = new ArrayList<>();

    private final DelayService delayService = new DelayProcedures();

    public TaskWorkerPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        this.dataSource = new HikariDataSource(config);
    }

    public TaskWorkerPool(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initWorkers(Map<String,Integer> categoriesAndThreads){
        for (Map.Entry<String, Integer> entry: categoriesAndThreads.entrySet()){

            String category = entry.getKey();
            int threadsCount = entry.getValue();
            ExecutorService threadPool = Executors.newFixedThreadPool(threadsCount);

            TaskRepository taskRepository = new JdbcTaskRepository(dataSource, category);
            repositories.put(category, taskRepository);
            TaskService taskService = new DatabaseTaskActions(taskRepository);
            TaskSchedulerService taskScheduler = new TaskScheduler(taskRepository);

            threadPool.submit(new TaskWorker(category, threadsCount, taskService, taskScheduler, delayService));
            System.out.println("Init worker with category "+ category + ", with " + threadsCount + " thread(s) " + threadPool);
        }
    }

    public void initWorker(String category, int threadsCount) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadsCount);



    }
}
