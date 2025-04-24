package org.example.worker;

import org.example.core.logging.LogService;
import org.example.core.schedulable.Schedulable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            LogService.logger.severe("Process initializing workers failed. " + e.getMessage());
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
            LogService.logger.severe(String.format("Worker with category: '%s' initializing failed. ", taskClass.getSimpleName()) + e.getMessage());
        }
    }


    public void stopWorker(String category, UUID workerId) {
        try {
            TaskWorker worker = taskWorkerMap.get(Collections.singletonMap(category, workerId));
            if (worker != null) {
                worker.doStop();
                taskWorkerMap.remove(Collections.singletonMap(category, workerId), worker);
                categoriesAndIdWorkers.remove(category, workerId);
            } else {
                LogService.logger.warning(String.format("Worker with id: %s and category: '%s' not found", workerId, category));
            }
        } catch (Exception e) {
            LogService.logger.severe(String.format("Failed to stop worker with id: %s and category: '%s'. ", workerId, category) + e.getMessage());
        }
    }

    public Map<String, UUID> getCategoriesAndIdCurrentWorkers() {
        return categoriesAndIdWorkers;
    }
}
