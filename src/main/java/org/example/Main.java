package org.example;

import com.zaxxer.hikari.*;
import org.example.config.DataSourceConfig;
import org.example.core.monitoring.*;
import org.example.core.monitoring.metrics.*;
import org.example.core.schedulable.*;
import org.example.core.service.task.scheduler.*;
import org.example.holder.RepositoryHolder;
import org.example.worker.TaskWorkerPool;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class Main {


    public static void main(String[] args) {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        DataSource dataSource = new HikariDataSource(config);

        RepositoryHolder.init(dataSource); // инициализация DataSource, репозиториев, сервисов
        Map<MetricType, MetricHandler> metricHandlers = createAndSetupMetricHandlers();
        MetricRegisterer metricRegisterer = new MetricRegisterer(metricHandlers);
        TaskSchedulerService taskScheduler = new TaskScheduler(metricRegisterer);
        TaskWorkerPool pool = new TaskWorkerPool(metricRegisterer);

        Map<String, String> params = Map.of(
                "ID", "4",
                "message", "test params");
        String executionTime = Timestamp.valueOf(LocalDateTime.now()).toString();
        Delay defaultDelayParams = new Delay.DelayBuilder().build();
        taskScheduler.scheduleTask(DoSomething.class, params, executionTime, defaultDelayParams);
        taskScheduler.scheduleTask(PushNotification.class, params, executionTime, defaultDelayParams);

        pool.initWorkers(Map.of(PushNotification.class, 2));
        pool.initWorkers(Map.of(DoSomething.class, 1));
    }

    private static Map<MetricType, MetricHandler> createAndSetupMetricHandlers() {
        Map<MetricType, MetricHandler> metricHandlers = new HashMap<>();
        metricHandlers.put(MetricType.FAILED_TASK_COUNT, TaskMetrics::getFailedTaskCountByCategory);
        metricHandlers.put(MetricType.TASK_AVERAGE_TIME_EXECUTION, TaskMetrics::getTaskAverageExecutionTimeByCategory);
        metricHandlers.put(MetricType.SCHEDULED_TASK_COUNT, TaskMetrics::getScheduledTaskCountByCategory);
        metricHandlers.put(MetricType.WORKER_COUNT, WorkerMetrics::getWorkerCountByCategory);
        metricHandlers.put(MetricType.WORKER_AVERAGE_TIME_EXECUTION, WorkerMetrics::getWorkerAverageWaitTimeByCategory);
        return metricHandlers;
    }
}