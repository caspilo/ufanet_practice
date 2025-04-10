package org.example;

import org.example.core.service.TaskService;
import org.example.test.DoSomething;
import org.example.test.Schedualable;
import org.example.worker.TaskWorker;
import org.example.worker.TaskWorkerPool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static TaskWorker taskWorker;
    public static TaskService taskService;
    public static Schedualable schedualable;

    public static void main(String[] args) throws Exception {

        System.out.println(DoSomething.class.getCanonicalName());
        System.out.println(DoSomething.class.getMethod("execute", Map.class));
        Map<String, String> params = new HashMap<>();
        params.put("userID", "123L");
        params.put("message", "Happy birthday!");


        Map<String, Integer> params2 = new HashMap<>();
        params2.put("Do", 2);
        params2.put("NotDo", 3);

        TaskWorkerPool taskWorkerPool = new TaskWorkerPool();

        taskWorkerPool.initWorkers(Collections.singletonMap("DoSomething", 1));
        taskWorkerPool.initWorkers(params2);

//        taskWorkerPool.startWorkers();
        // 1. Наследуемся от класса ScheduleTask и прописываем бизнес-логику(TestClass)
        //  Создаем задачу , заносим в бд
        // 2. Инициализируем воркеры
        // 3. Воркеры должны побежать по бд
        // 4. Воркер проверяет задачу, если не занята, имеет нужную категорию и в нужном состоянии, то начинается выполнение
        // 5. Воркер запускает МЕТОД execute(), меняет статус в бд, бежит дальше
        // 6. Вызываем какой-то МЕТОД , в который передаем TestClass


        TaskWorker worker = new TaskWorker("DoSomethingNew", 1);
        worker.executeTask(DoSomething.class.getCanonicalName(), params);

    }
}