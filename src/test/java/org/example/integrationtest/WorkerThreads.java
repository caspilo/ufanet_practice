package org.example.integrationtest;

import org.example.core.monitoring.MetricRegisterer;
import org.example.core.schedulable.Schedulable;
import org.example.worker.TaskWorkerPool;

import java.time.LocalDateTime;
import java.util.Map;

public class WorkerThreads extends TestThreads{
    private final int maxWorkerThreads;
    private final int minWorkerThreads;
    private final Map<Integer, Class<? extends Schedulable>> categories;
    private final TaskWorkerPool workerPool;

    public WorkerThreads(int maxWorkerThreads,
                         int minWorkerThreads,
                         Map<Integer, Class<? extends Schedulable>> categories,
                         MetricRegisterer metricRegisterer) {
        this.maxWorkerThreads = maxWorkerThreads;
        this.minWorkerThreads = minWorkerThreads;
        this.categories = categories;
        this.workerPool = new TaskWorkerPool(metricRegisterer);
    }

    @Override
    public Thread[] initThreads(int threadCount, int boundMillisToSleep) {
        Thread[] workerThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            workerThreads[i] = new Thread(() -> {
                while(true) {
                    initWorkerWithRandomValues();
                    sleep(boundMillisToSleep);
                }
            });
            workerThreads[i].setName("Worker-" + i);
            workerThreads[i].start();
        }
        return workerThreads;
    }

    private void initWorkerWithRandomValues() {
        Class<? extends Schedulable> randomCategory =
                categories.get(RANDOM.nextInt(categories.size()));
        int randomThreadCount = RANDOM.nextInt(maxWorkerThreads) + minWorkerThreads;
        workerPool.initWorker(randomCategory, randomThreadCount);
        printWorkerInfo(randomCategory, randomThreadCount);
    }

    private static void printWorkerInfo(Class<? extends Schedulable> randomCategory,
                                        int randomThreadCount) {
        System.out.println("Создан новый Worker:" +
                "\nКатегория - " + randomCategory +
                "\nКол-во потоков - " + randomThreadCount +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата создания - " + LocalDateTime.now());
    }
}
