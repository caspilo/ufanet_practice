package org.example.integrationtest.worker;

import org.example.integrationtest.TestThreads;

public class WorkerThreads extends TestThreads {
    private final WorkerManager workerManager;

    public WorkerThreads(WorkerManager workerManager) {
        this.workerManager = workerManager;
    }

    @Override
    protected Runnable createInitThread(int boundMillisToSleep) {
        return () -> {
            setupThreadName(Thread.currentThread(), "Worker initializer");
            while(true) {
                workerManager.initRandomWorker();
                sleep(boundMillisToSleep);
            }
        };
    }

    @Override
    protected Runnable createStoppingThread(int boundMillisToSleep) {
        return () -> {
            setupThreadName(Thread.currentThread(), "Worker stopper");
            while(true) {
                workerManager.stopRandomWorker();
                sleep(boundMillisToSleep);
            }
        };
    }
}
