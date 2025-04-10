package org.example.worker;

import org.example.core.entity.ScheduledTask;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskWorkerPool {

//    private static final Map<String, ExecutorService> taskWorkerPool = new HashMap<>();
    private static final List<TaskWorker> taskWorkers = new ArrayList<>();

    private static final List<ExecutorService> executorServices = new ArrayList<>();

    public void initWorkers(Map<String,Integer> categoriesAndThreads){
        for (Map.Entry<String, Integer> entry: categoriesAndThreads.entrySet()){
            String category = entry.getKey();
            int threadsCount = entry.getValue();
            ExecutorService threadPool = Executors.newFixedThreadPool(threadsCount);
//            taskWorkerPool.put(category, threadPool);
//            executorServices.add(threadPool);
//            taskWorkers.add(new TaskWorker(category,threadsCount));
            threadPool.submit(new TaskWorker(category,threadsCount));
            System.out.println("Init worker with category"+ category + ", with " + threadsCount + "  threads " + threadPool);
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
