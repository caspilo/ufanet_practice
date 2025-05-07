package org.example.core.monitoring.metrics;

import java.util.concurrent.atomic.*;

public class AverageTimeCalculator {
    private final AtomicLong summaryTime = new AtomicLong(0);
    private final AtomicInteger summaryCount = new AtomicInteger(0);

    public void eventHappened(long duration) {
        summaryTime.addAndGet(duration);
        summaryCount.incrementAndGet();
    }

    public double calculateAverageTimeByCategory() {
        return (double) summaryTime.get() / summaryCount.get();
    }
}
