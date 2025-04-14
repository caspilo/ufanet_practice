package org.example.core.delay;

public class ExponentialDelay implements Delay{

    static private double base;
    static private final double limit = 10000.0;


    static public int getNextDelay(int attemptCount) {
        base = Math.E;
        int delay = (int)Math.pow(base, attemptCount);
        return delay <= limit ? delay : -1;
    }

    static public int getNextDelay(double baseValue, int attemptCount) {
        base = baseValue;
        return getNextDelay(attemptCount);
    }
}
