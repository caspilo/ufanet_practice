package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.service.task.TaskScheduler;
import org.example.core.service.task.TaskSchedulerService;
import org.example.core.schedulable.PushNotification;
import org.example.holder.RepositoryHolder;
import org.example.worker.TaskWorkerPool;

import javax.sql.DataSource;
import java.util.Map;

public class Main {


    public static void main(String[] args) throws Exception {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        DataSource dataSource = new HikariDataSource(config);

        RepositoryHolder.init(dataSource); // инициализация DataSource, репозиториев, сервисов
        TaskSchedulerService taskScheduler = new TaskScheduler();
        TaskWorkerPool pool = new TaskWorkerPool();

        pool.initWorker("PushNotification", 1);
        pool.initWorker("PushNotification", 1);
    }
}