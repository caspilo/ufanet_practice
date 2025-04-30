package org.example.integrationtest.worker;

import org.example.integrationtest.TestThreads;

public class WorkerThreads extends TestThreads {
    private final WorkerManager workerManager;

    public WorkerThreads(WorkerManager workerManager) {
        this.workerManager = workerManager;
    }

    @Override
    public Thread[] initThreads(int threadCount, int boundMillisToSleep) {
        Thread[] workerThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            workerThreads[i] = new Thread(() -> {
                while(true) {
                    workerManager.initRandomWorker();
                    sleep(boundMillisToSleep);
                }
            });
            workerThreads[i].setName("Worker initializer-" + i);
            workerThreads[i].start();
        }
        return workerThreads;
    }

    @Override
    public Thread[] stoppingThreads(int threadCount, int boundMillisToSleep) {
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                while (true) {
                    workerManager.stopRandomWorker();
                    sleep(boundMillisToSleep);
                }
            });
            threads[i].setName("Worker stopper-" + i);
            threads[i].start();
        }
        return threads;
    }
}
