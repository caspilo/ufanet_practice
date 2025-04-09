package org.example;

import org.example.core.service.TaskService;
import org.example.test.DoSomething;
import org.example.worker.TaskWorker;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static TaskWorker taskWorker;
    public static TaskService taskService;

    public static void main(String[] args) throws Exception {

        System.out.println(DoSomething.class.getCanonicalName());
        System.out.println(DoSomething.class.getMethod("execute", Map.class));

        Map<String, String> params = new HashMap<>();
        params.put("userID", "123L");
        params.put("message", "Happy birthday!");

        TaskWorker worker = new TaskWorker("DoSomething", 1);
        worker.run();

        worker.executeTask(DoSomething.class.getCanonicalName(), params);

    }
}