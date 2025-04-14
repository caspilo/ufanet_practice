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

        System.out.println(DoSomething.class.getCanonicalName());
        Map<String, String> params = new HashMap<>();
        params.put("userID", "123L");
        params.put("message", "Happy birthday!");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
//        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        DataSource dataSource = new HikariDataSource(config);

        taskRepository = new JdbcTaskRepository(dataSource);
        taskService = new TaskServiceDataBase(taskRepository);
        ScheduledTask sa = new ScheduledTask();
        sa.setCanonicalName("org.example.core.entity.TestClass");
//        taskRepository.save(sa);
        //taskRepository.rescheduleTask(-1988446412L, 60000);
//        taskRepository.cancelTask(-1988446412L);


        TaskWorker worker = new TaskWorker("default", 1, taskService);
        worker.run();

        Map<String, Integer> params2 = new HashMap<>();
        params2.put("Do", 2);
        params2.put("NotDo", 3);

        TaskWorkerPool taskWorkerPool = new TaskWorkerPool(taskService);

        taskWorkerPool.initWorkers(Collections.singletonMap("DoSomething", 1));
        taskWorkerPool.initWorkers(params2);
    }
}