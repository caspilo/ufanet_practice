package org.example.worker;

import org.example.core.logging.LogService;
import org.example.core.monitoring.*;
import org.example.core.monitoring.metrics.*;
import org.example.core.schedulable.Schedulable;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskWorkerPool {
    private final MetricRegisterer metricRegisterer;
    private final Map<String, ArrayList<UUID>> workersIdAndCategory = new HashMap<>();
    private final Map<Map<String, UUID>, TaskWorker> taskWorkerMap = new HashMap<>();

    public TaskWorkerPool(MetricRegisterer metricRegisterer) {
        this.metricRegisterer = metricRegisterer;
    }

    public void initWorkers(Map<Class<? extends Schedulable>, Integer> categoriesAndThreads) {
        try {
            LogService.logger.info("Process initializing workers started");
            for (Map.Entry<Class<? extends Schedulable>, Integer> entry : categoriesAndThreads.entrySet()) {

                Class<? extends Schedulable> taskClass = entry.getKey();
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
                addToMap(category, workerId);
                taskWorkerMap.put(Collections.singletonMap(category, workerId), taskWorker);
                threadPool.submit(taskWorker);
                LogService.logger.info(String.format("Worker initializing with id: %s category '%s'",
                        workerId, category));
            }
            LogService.logger.info(String.format("Worker pool initializing with for category: '%s', with %s thread(s) %s",
                    category, threadsCount, threadPool));
            metricRegisterer.registerMetric(category, MetricType.WORKER_COUNT);
            metricRegisterer.registerMetric(category, MetricType.WORKER_AVERAGE_TIME_EXECUTION);
            WorkerMetrics.workerCreated(category);
        } catch (Exception e) {
            LogService.logger.severe(String.format("Worker with category: '%s' initializing failed. ", taskClass.getSimpleName()) + e.getMessage());
        }
    }


    // TODO: воркеры продолжают работу в потоках!!! Они никак не прерываются
    public void shutdownWorker(String category, UUID workerId) {
        try {
            TaskWorker worker = taskWorkerMap.get(Collections.singletonMap(category, workerId));
            if (worker != null) {
                worker.doStop();
                taskWorkerMap.remove(Collections.singletonMap(category, workerId), worker);
                workersIdAndCategory.get(category).remove(workerId);
                if (workersIdAndCategory.get(category).isEmpty()) {
                    workersIdAndCategory.remove(category);
                }



                LogService.logger.info(String.format("Worker with id %s and category '%s' has been shutdown", workerId, category));
            } else {
                LogService.logger.warning(String.format("Worker with id %s and category '%s' not found", workerId, category));
            }
            WorkerMetrics.workerDeleted(category);
        } catch (Exception e) {
            LogService.logger.severe(String.format("Failed to stop worker with id %s and category '%s'. ", workerId, category) + e.getMessage());
        }
    }

    public void shutdownAllWorkers() {
        for (Map.Entry<String, ArrayList<UUID>> entry : workersIdAndCategory.entrySet()) {
            for (UUID workerId : entry.getValue()) {
                shutdownWorker(entry.getKey(), workerId);
            }
        }
    }

    public Map<String, ArrayList<UUID>> getWorkersIdAndCategory() {
        return workersIdAndCategory;
    }

    private void addToMap(String category, UUID id) {
        workersIdAndCategory.putIfAbsent(category, new ArrayList<>());
        workersIdAndCategory.get(category).add(id);
    }
}