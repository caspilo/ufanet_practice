package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.TaskRepository;
import org.example.core.service.DatabaseTaskActions;
import org.example.core.service.TaskScheduler;
import org.example.core.service.TaskSchedulerService;
import org.example.core.service.TaskService;
import org.example.core.service.delay.DelayService;
import org.example.worker.TaskWorkerPool;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static TaskRepository taskRepository;

    public static TaskService taskService;

    public static TaskSchedulerService taskSchedulerService;

    public static DelayService delayService;

    public static void main(String[] args) throws Exception {

        Map<String, String> params = new HashMap<>();
        params.put("userID", "123L");
        params.put("message", "Happy birthday!");


        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        DataSource dataSource = new HikariDataSource(config);

        taskRepository = new JdbcTaskRepository(dataSource);
        taskService = new DatabaseTaskActions(taskRepository);
        taskSchedulerService = new TaskScheduler(taskRepository);


        Timestamp as = new Timestamp(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stroka = df.format(as);
        System.out.println(Timestamp.valueOf("2000-10-10 10:22:12.99").getTime());

        System.out.println(df.format(as));



        Map<String, Integer> params2 = new HashMap<>();
        params2.put("DoSomething", 1);
        params2.put("Do", 2);
        params2.put("NotDo", 3);

        TaskWorkerPool taskWorkerPool = new TaskWorkerPool(taskService, taskSchedulerService, delayService);

        taskWorkerPool.initWorkers(params2);
    }
}