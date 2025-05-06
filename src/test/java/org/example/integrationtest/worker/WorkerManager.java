package org.example.integrationtest.worker;

import org.example.core.monitoring.MetricRegisterer;
import org.example.core.schedulable.Schedulable;
import org.example.core.worker.TaskWorkerPool;

import java.time.LocalDateTime;
import java.util.*;

public class WorkerManager {
    private static final Random RANDOM = new Random();

    private final int maxWorkerThreads;
    private final int minWorkerThreads;
    private final Map<Integer, Class<? extends Schedulable>> categories;
    private final TaskWorkerPool workerPool;

    public WorkerManager(int maxWorkerThreads, int minWorkerThreads,
                         Map<Integer, Class<? extends Schedulable>> categories,
                         MetricRegisterer metricRegisterer) {
        this.maxWorkerThreads = maxWorkerThreads;
        this.minWorkerThreads = minWorkerThreads;
        this.categories = categories;
        this.workerPool = new TaskWorkerPool(metricRegisterer);
    }

    public void initRandomWorker() {
        Class<? extends Schedulable> randomCategory =
                categories.get(RANDOM.nextInt(categories.size()));
        int randomThreadCount = RANDOM.nextInt(maxWorkerThreads) + minWorkerThreads;
        workerPool.initWorker(randomCategory, randomThreadCount);
        printInitWorkerInfo(randomCategory.getSimpleName(), randomThreadCount);
    }

    private void printInitWorkerInfo(String randomCategory, int threadCount) {
        System.out.println("Создан новый Worker:" +
                "\nКатегория - " + randomCategory +
                "\nКол-во потоков - " + threadCount +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата создания - " + LocalDateTime.now());
    }

    public void stopRandomWorker() {
        String randomCategory = categories
                .get(RANDOM.nextInt(categories.size()))
                .getSimpleName();
        Optional<List<UUID>> workersIdOptional = workerPool.getWorkersIdByCategory(randomCategory);
        if (workersIdOptional.isPresent()) {
            List<UUID> workersId = workersIdOptional.get();
            UUID randomWorkerId = workersId.get(RANDOM.nextInt(workersId.size()));
            workerPool.shutdownWorker(randomCategory, randomWorkerId);
            printStoppedWorkerInfo(randomCategory);
        }
    }

    private void printStoppedWorkerInfo(String randomCategory) {
        System.out.println("Остановлен worker: " +
                "\nКатегория - " + randomCategory +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата остановки - " + LocalDateTime.now());
    }
}
