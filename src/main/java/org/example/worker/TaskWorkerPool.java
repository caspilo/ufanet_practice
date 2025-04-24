package org.example.worker;

import org.example.core.logging.LogService;
import org.example.core.schedulable.Schedulable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class TaskWorkerPool {
    private final Map<String, UUID> categoriesAndIdWorkers = new HashMap<>();
    private final Map<Map<String, UUID>, TaskWorker> taskWorkerMap = new HashMap<>();

    public TaskWorkerPool() {
    }

    public <T extends Schedulable> void initWorkers(Map<Class<T>, Integer> categoriesAndThreads) {
        try {
            LogService.logger.info("Process initializing workers started");
            for (Map.Entry<Class<T>, Integer> entry : categoriesAndThreads.entrySet()) {

                Class<T> taskClass = entry.getKey();
                int threadsCount = entry.getValue();

                initWorker(taskClass, threadsCount);
            }
            LogService.logger.info("Process initializing workers completed");
        } catch (Exception e) {
            LogService.logger.log(Level.SEVERE, "Process initializing workers failed" + e.getMessage(), e);
        }
    }

    public <T extends Schedulable> void initWorker(Class<T> taskClass, int threadsCount) {

        try {
            ExecutorService threadPool = Executors.newFixedThreadPool(threadsCount);
            String category = taskClass.getSimpleName();

            for (int i = 0; i < threadsCount; i++) {
                UUID workerId = UUID.randomUUID();
                TaskWorker taskWorker = new TaskWorker(category, workerId);
                categoriesAndIdWorkers.put(category, workerId);
                taskWorkerMap.put(Collections.singletonMap(category, workerId), taskWorker);
                threadPool.submit(taskWorker);
                LogService.logger.info(String.format("Worker initializing with id: %s category '%s'",
                        workerId, category));
            }
            LogService.logger.info(String.format("Worker pool initializing with for category: '%s', with %s thread(s) %s",
                    category, threadsCount, threadPool));
        } catch (Exception e) {
            LogService.logger.log(Level.SEVERE, String.format("Worker with category: '%s' initializing failed.", taskClass.getSimpleName()) + e.getMessage(), e);
        }
    }


    public void stopWorker(String category, UUID workerId) {
        TaskWorker worker = taskWorkerMap.get(Collections.singletonMap(category, workerId));
        if (worker != null) {
            worker.doStop();
        } else {
            LogService.logger.log(Level.WARNING, String.format("Worker with id: %s and category: '%s' not found", workerId, category));
        }
    }

    public Map<String, UUID> getCategoriesAndIdWorkers() {
        return categoriesAndIdWorkers;
    }
}
