package org.example.integrationtest;

import org.example.worker.TaskWorkerPool;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

public class WorkerThreads {
    private static final Random RANDOM = new Random();

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

    public void initWorkerThreads(int workerThreadCount, int boundMillisToSleep) {
        Thread[] workerThreads = new Thread[workerThreadCount];
        for (int i = 0; i < workerThreadCount; i++) {
            workerThreads[i] = new Thread(() -> {
                while(true) {
                    initWorkerWithRandomValues();
                    sleep(boundMillisToSleep);
                }
            });
            workerThreads[i].setName("Worker-" + i);
            workerThreads[i].start();
        }
    }

    private void initWorkerWithRandomValues() {
        String randomCategory = categories.get(RANDOM.nextInt(categories.size()));
        int randomThreadCount = RANDOM.nextInt(maxWorkerThreads) + minWorkerThreads;
        workerPool.initWorker(randomCategory, randomThreadCount);
        printWorkerInfo(randomCategory, randomThreadCount);
    }

    private void sleep(int boundMillisToSleep) {
        try {
            Thread.sleep(RANDOM.nextInt(boundMillisToSleep));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printWorkerInfo(String randomCategory, int randomThreadCount) {
        System.out.println("Создан новый Worker:" +
                "\nКатегория - " + randomCategory +
                "\nКол-во потоков - " + randomThreadCount +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата создания - " + LocalDateTime.now());
    }
}
