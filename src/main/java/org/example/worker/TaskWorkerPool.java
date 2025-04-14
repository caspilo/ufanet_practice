package org.example.worker;

import org.example.core.service.TaskService;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskWorkerPool {

//    private static final Map<String, ExecutorService> taskWorkerPool = new HashMap<>();
    private static final List<TaskWorker> taskWorkers = new ArrayList<>();

    private final TaskService taskService;

    private static final List<ExecutorService> executorServices = new ArrayList<>();

    public TaskWorkerPool(TaskService taskService) {
        this.taskService = taskService;
    }

    public void initWorkers(Map<String,Integer> categoriesAndThreads){
        for (Map.Entry<String, Integer> entry: categoriesAndThreads.entrySet()){
            String category = entry.getKey();
            int threadsCount = entry.getValue();
            ExecutorService threadPool = Executors.newFixedThreadPool(threadsCount);
//            taskWorkerPool.put(category, threadPool);
//            executorServices.add(threadPool);
//            taskWorkers.add(new TaskWorker(category,threadsCount));
            threadPool.submit(new TaskWorker(category,threadsCount, taskService));
            System.out.println("Init worker with category "+ category + ", with " + threadsCount + " thread(s) " + threadPool);
        }
    }

//    public void startWorkers(){
//        for(TaskWorker taskWorker: taskWorkers){
//            for (ExecutorService service: executorServices){
//                service.submit(taskWorker);
//            }
//        }
//    }
}
