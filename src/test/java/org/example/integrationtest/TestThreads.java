package org.example.integrationtest;

import java.util.Random;
import java.util.concurrent.*;

public abstract class TestThreads {
    protected static final Random RANDOM = new Random();

    protected void sleep(int boundMillisToSleep) {
        try {
            Thread.sleep(RANDOM.nextInt(boundMillisToSleep));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void initThreads(int threadCount, int boundMillisToSleep) {
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            Runnable initRunnable = createInitThread(boundMillisToSleep);
            for (int i = 0; i < threadCount; i++) {
                executor.execute(initRunnable);
            }
        }
    }

    public void stoppingThreads(int threadCount, int boundMillisToSleep) {
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            Runnable stoppingRunnable = createStoppingThread(boundMillisToSleep);
            for (int i = 0; i < threadCount; i++) {
                executor.execute(stoppingRunnable);
            }
        }
    }

    protected void setupThreadName(Thread thread, String threadName) {
        long threadId = thread.threadId();
        String threadFullName = threadName + "-" + threadId;
        thread.setName(threadFullName);
    }

    protected abstract Runnable createInitThread(int boundMillisToSleep);

    protected abstract Runnable createStoppingThread(int boundMillisToSleep);
}
