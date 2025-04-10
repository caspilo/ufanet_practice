package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.DriverDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.TaskRepository;
import org.example.core.service.TaskService;
import org.example.test.DoSomething;
import org.example.worker.TaskWorker;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static TaskRepository taskRepository;

    public static void main(String[] args) throws Exception {

        System.out.println(DoSomething.class.getCanonicalName());
        System.out.println(DoSomething.class.getMethod("execute", Map.class));

        Map<String, String> params = new HashMap<>();
        params.put("userID", "123L");
        params.put("message", "Happy birthday!");

        TaskWorker worker = new TaskWorker("DoSomething", 1);
        worker.run();

        worker.executeTask(DoSomething.class.getCanonicalName(), params);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        DataSource dataSource = new HikariDataSource(config);

        taskRepository = new JdbcTaskRepository(dataSource);

        //taskRepository.save(new ScheduledTask());
        //taskRepository.rescheduleTask(-1988446412L, 60000);
        taskRepository.cancelTask(-1988446412L);
    }
}