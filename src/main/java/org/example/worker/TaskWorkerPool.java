package org.example.worker;

import org.example.core.logging.LogService;
import org.example.core.schedulable.Schedulable;
import org.example.core.validator.ScheduleClassValidator;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class TaskWorkerPool {

    public TaskWorkerPool() {
    }

    public void initWorkers(Map<Class, Integer> categoriesAndThreads) {
        try {
            LogService.logger.info("Process initializing workers started");
            for (Map.Entry<Class, Integer> entry : categoriesAndThreads.entrySet()) {

                ScheduleClassValidator.validateTaskClass(entry.getKey());

                Class taskClass = entry.getKey();
                int threadsCount = entry.getValue();

                initWorker(taskClass, threadsCount);
            }
            LogService.logger.info("Process initializing workers completed");
        } catch (Exception e) {
            LogService.logger.log(Level.SEVERE, "Process initializing workers failed" + e.getMessage(), e);
        }
    }

    public void initWorker(Class taskClass, int threadsCount) {

        try {
            ScheduleClassValidator.validateTaskClass(taskClass);

            ExecutorService threadPool = Executors.newFixedThreadPool(threadsCount);
            String category = taskClass.getSimpleName();

            for (int i = 0; i < threadsCount; i++) {
                threadPool.submit(new TaskWorker(category));
            }
            LogService.logger.info(String.format("Worker initializing with category %s, with %s thread(s) %s", category, threadsCount, threadPool));
        } catch (Exception e) {
            LogService.logger.log(Level.SEVERE, "Worker initializing failed" + e.getMessage(), e);
        }
    }
}
