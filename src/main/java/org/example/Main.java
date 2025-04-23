package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.schedulable.DoSomething;
import org.example.core.service.task.scheduler.Delay;
import org.example.core.service.task.scheduler.TaskScheduler;
import org.example.core.service.task.scheduler.TaskSchedulerService;
import org.example.core.schedulable.PushNotification;
import org.example.holder.RepositoryHolder;
import org.example.worker.TaskWorkerPool;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.util.Map;

public class Main {


    public static void main(String[] args) {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        DataSource dataSource = new HikariDataSource(config);

        RepositoryHolder.init(dataSource); // инициализация DataSource, репозиториев, сервисов
        TaskSchedulerService taskScheduler = new TaskScheduler();
        TaskWorkerPool pool = new TaskWorkerPool();

        Map<String, String> params = Map.of(
                "ID", "4",
                "message", "test params");
        String executionTime = Timestamp.valueOf(LocalDateTime.now()).toString();
        Delay defaultDelayParams = new Delay.DelayBuilder().build();
        taskScheduler.scheduleTask(DoSomething.class.getName(), params, executionTime, defaultDelayParams);
        taskScheduler.scheduleTask(PushNotification.class.getName(), params, executionTime, defaultDelayParams);

        pool.initWorker("PushNotification", 1);
        pool.initWorker("PushNotification", 1);
    }
}