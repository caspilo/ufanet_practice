package org.example.integrationtest;

import java.util.Random;

public abstract class TestThreads {
    protected static final Random RANDOM = new Random();

    protected void sleep(int boundMillisToSleep) {
        try {
            Thread.sleep(RANDOM.nextInt(boundMillisToSleep));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract Thread[] initThreads(int threadCount, int boundMillisToSleep);
}
