package org.example.integrationtest;

import org.example.worker.TaskWorkerPool;

import java.time.LocalDateTime;
import java.util.Map;

public class WorkerThreads extends TestThreads{
    private final int maxWorkerThreads;
    private final int minWorkerThreads;
    private final Map<Integer, String> categories;
    private final TaskWorkerPool workerPool = new TaskWorkerPool();

    public WorkerThreads(int maxWorkerThreads,
                         int minWorkerThreads,
                         Map<Integer, String> categories) {
        this.maxWorkerThreads = maxWorkerThreads;
        this.minWorkerThreads = minWorkerThreads;
        this.categories = categories;
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
        String randomCategory = categories.get(RANDOM.nextInt(categories.size()));
        int randomThreadCount = RANDOM.nextInt(maxWorkerThreads) + minWorkerThreads;
        workerPool.initWorker(randomCategory, randomThreadCount);
        printWorkerInfo(randomCategory, randomThreadCount);
    }

    private static void printWorkerInfo(String randomCategory, int randomThreadCount) {
        System.out.println("Создан новый Worker:" +
                "\nКатегория - " + randomCategory +
                "\nКол-во потоков - " + randomThreadCount +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата создания - " + LocalDateTime.now());
    }
}
