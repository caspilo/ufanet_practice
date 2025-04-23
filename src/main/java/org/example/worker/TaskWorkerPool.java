package org.example.worker;

import org.example.core.logging.LogService;
import org.example.core.schedulable.Schedulable;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class TaskWorkerPool {

    public TaskWorkerPool() {
    }

    public void initWorkers(Map<Schedulable, Integer> categoriesAndThreads) {
        try {
            LogService.logger.info("Process initializing workers started");
            for (Map.Entry<Schedulable, Integer> entry : categoriesAndThreads.entrySet()) {

                String category = entry.getKey().getClass().getSimpleName();
                int threadsCount = entry.getValue();

                initWorker(category, threadsCount);
            }
            LogService.logger.info("Process initializing workers completed");
        } catch (Exception e) {
            LogService.logger.log(Level.SEVERE, "Process initializing workers failed" + e.getMessage(), e);
        }
    }

    public void initWorker(String category, int threadsCount) {

        ExecutorService threadPool = Executors.newFixedThreadPool(threadsCount);

        for (int i = 0; i < threadsCount; i++) {
            threadPool.submit(new TaskWorker(category));
        }
        LogService.logger.info(String.format("Worker initializing with category %s, with %s thread(s) %s", category, threadsCount, threadPool));
    }
}
