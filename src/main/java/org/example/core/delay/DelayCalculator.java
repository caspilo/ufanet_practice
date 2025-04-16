package org.example.core.delay;

public class DelayCalculator {

    static private final double defaultBase = Math.E;

    static public Long getNextDelay(int attemptCount) {
        return getNextDelay(attemptCount, defaultBase);
    }

    static public Long getNextDelay(int attemptCount, double base) {
        return (long) Math.pow(base, attemptCount);
    }

    static public Long getNextDelay(int attemptCount, int limit) {
        return getNextDelay(attemptCount, defaultBase, limit);
    }

    static public Long getNextDelay(int attemptCount, double base, double limit) {
        long delay = (long) Math.pow(base, attemptCount);
        return (delay <= limit ? delay : -1);
    }
}
