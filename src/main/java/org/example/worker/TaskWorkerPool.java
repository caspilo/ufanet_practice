package org.example.worker;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.repository.JdbcDelayRepository;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.service.task.DatabaseTaskActions;
import org.example.core.service.task.TaskService;
import org.example.core.service.delay.DelayPolicy;
import org.example.core.service.delay.DelayService;
import org.example.holder.ServiceHolder;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskWorkerPool {

    private final TaskService taskService;

    private final DelayService delayService;

    public TaskWorkerPool() {
        this.taskService = ServiceHolder.getTaskService();
        this.delayService = ServiceHolder.getDelayService();
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