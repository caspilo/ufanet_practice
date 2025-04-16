package org.example.core.delay;

public class DelayCalculator {

    static private final double defaultBase = Math.E;

    static public int getNextDelay(int attemptCount) {
        return (int)Math.pow(defaultBase, attemptCount);
    }

    static public int getNextDelay(int attemptCount, double base) {
        return (int)Math.pow(base, attemptCount);
    }

    static public int getNextDelay(int attemptCount, int limit) {
        return getNextDelay(attemptCount, defaultBase, limit);
    }

    static public int getNextDelay(int attemptCount, double base, int limit) {
        int delay = (int)Math.pow(base, attemptCount);
        return delay <= limit ? delay : -1;
    }
}
