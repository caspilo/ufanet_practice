package org.example.integrationtest.worker;

import org.example.integrationtest.TestThreads;

public class WorkerThreads extends TestThreads {
    private final WorkerManager workerManager;

    public WorkerThreads(WorkerManager workerManager) {
        this.workerManager = workerManager;
    }

    @Override
    protected Thread createInitThreads(int boundMillisToSleep) {
        Thread thread = new Thread(() -> {
            while(true) {
                workerManager.initRandomWorker();
                sleep(boundMillisToSleep);
            }
        });
        setupThreadName(thread, "Worker initializer");
        return thread;
    }

    @Override
    protected Thread createStoppingThread(int boundMillisToSleep) {
        Thread thread = new Thread(() -> {
            while(true) {
                workerManager.stopRandomWorker();
                sleep(boundMillisToSleep);
            }
        });
        setupThreadName(thread, "Worker stopper");
        return thread;
    }
}
