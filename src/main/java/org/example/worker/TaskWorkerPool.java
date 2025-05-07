package org.example.worker;

import org.example.core.logging.LogService;
import org.example.core.monitoring.*;
import org.example.core.monitoring.metrics.*;
import org.example.core.schedulable.Schedulable;

import java.util.*;
import java.util.concurrent.*;

public class TaskWorkerPool {
    private final MetricRegisterer metricRegisterer;
    private final Map<String, List<UUID>> categoriesAndIdWorkers = new ConcurrentHashMap<>();
    private final Map<Map<String, UUID>, TaskWorker> taskWorkerMap = new ConcurrentHashMap<>();

    public TaskWorkerPool(MetricRegisterer metricRegisterer) {
        this.metricRegisterer = metricRegisterer;
    }

    public <T extends Schedulable> void initWorkers(Map<Class<T>, Integer> categoriesAndThreads) {
        try {
            tryInitWorkers(categoriesAndThreads);
        } catch (Exception e) {
            LogService.logger.severe("Process initializing workers failed. " + e.getMessage());
        }
    }

    private <T extends Schedulable> void tryInitWorkers(Map<Class<T>, Integer> categoriesAndThreads) {
        LogService.logger.info("Process initializing workers started");
        for (var categoryAndThreads : categoriesAndThreads.entrySet()) {
            Class<T> taskClass = categoryAndThreads.getKey();
            int threadsCount = categoryAndThreads.getValue();
            initWorker(taskClass, threadsCount);
        }
        LogService.logger.info("Process initializing workers completed");
    }

    public <T extends Schedulable> void initWorker(Class<T> taskClass, int threadsCount) {
        try {
            ExecutorService threadPool = Executors.newFixedThreadPool(threadsCount);
            String category = taskClass.getSimpleName();

            for (int i = 0; i < threadsCount; i++) {
                UUID workerId = UUID.randomUUID();
                TaskWorker taskWorker = new TaskWorker(category, workerId);
                putInCategoriesAndIdWorkers(category, workerId);
                taskWorkerMap.put(Collections.singletonMap(category, workerId), taskWorker);
                threadPool.submit(taskWorker);
                LogService.logger.info(String.format("Worker initializing with id: %s category '%s'",
                        workerId, category));
            }
            LogService.logger.info(String.format("Worker pool initializing for category: '%s', with %s thread(s) %s",
                    category, threadsCount, threadPool));
            metricRegisterer.registerMetric(category, MetricType.WORKER_COUNT);
            metricRegisterer.registerMetric(category, MetricType.WORKER_AVERAGE_TIME_EXECUTION);
            WorkerMetrics.workerCreated(category);
        } catch (Exception e) {
            LogService.logger.severe(String.format("Worker with category: '%s' initializing failed. ", taskClass.getSimpleName()) + e.getMessage());
        }
    }

    private void putInCategoriesAndIdWorkers(String category, UUID workerId) {
        if (categoriesAndIdWorkers.containsKey(category)) {
            categoriesAndIdWorkers.get(category).add(workerId);
        } else {
            List<UUID> workerIds = new CopyOnWriteArrayList<>();
            workerIds.add(workerId);
            categoriesAndIdWorkers.put(category, workerIds);
        }
    }

    public void shutdownWorker(String category, UUID workerId) {
        try {
            TaskWorker worker = taskWorkerMap.get(Collections.singletonMap(category, workerId));
            if (worker != null) {
                worker.doStop();
                taskWorkerMap.remove(Collections.singletonMap(category, workerId), worker);
                categoriesAndIdWorkers.get(category).remove(workerId);
            } else {
                LogService.logger.warning(String.format("Worker with id: %s and category: '%s' not found", workerId, category));
            }
            WorkerMetrics.workerDeleted(category);
        } catch (Exception e) {
            LogService.logger.severe(String.format("Failed to stop worker with id: %s and category: '%s'. ", workerId, category) + e.getMessage());
        }
    }

    public void shutdownAllWorkers() {
        for (Map.Entry<String, List<UUID>> entry : categoriesAndIdWorkers.entrySet()) {
            for (UUID workerId : entry.getValue()) {
                shutdownWorker(entry.getKey(), workerId);
            }
        }
    }

    public Optional<List<UUID>> getWorkersIdByCategory(String category) {
        return Optional.ofNullable(categoriesAndIdWorkers.get(category));
    }
}
