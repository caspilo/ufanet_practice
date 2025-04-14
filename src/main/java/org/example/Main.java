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
import org.example.worker.TaskWorkerPool;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static TaskRepository taskRepository;

    public static void main(String[] args) throws Exception {

        System.out.println(DoSomething.class.getCanonicalName());
        System.out.println(DoSomething.class.getMethod("execute", Map.class));
        Map<String, String> params = new HashMap<>();
        params.put("userID", "123L");
        params.put("message", "Happy birthday!");


//        Map<String, Integer> params2 = new HashMap<>();
//        params2.put("Do", 2);
//        params2.put("NotDo", 3);
//
//        TaskWorkerPool taskWorkerPool = new TaskWorkerPool();
//
//        taskWorkerPool.initWorkers(Collections.singletonMap("DoSomething", 1));
//        taskWorkerPool.initWorkers(params2);

//        taskWorkerPool.startWorkers();
        // 1. Наследуемся от класса ScheduleTask и прописываем бизнес-логику(TestClass)
        //  Создаем задачу , заносим в бд
        // 2. Инициализируем воркеры
        // 3. Воркеры должны побежать по бд
        // 4. Воркер проверяет задачу, если не занята, имеет нужную категорию и в нужном состоянии, то начинается выполнение
        // 5. Воркер запускает МЕТОД execute(), меняет статус в бд, бежит дальше
        // 6. Вызываем какой-то МЕТОД , в который передаем TestClass


        TaskWorker worker = new TaskWorker("DoSomething", 1);
        worker.executeTask(DoSomething.class.getCanonicalName(), params);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        DataSource dataSource = new HikariDataSource(config);

        taskRepository = new JdbcTaskRepository(dataSource);
    }
}