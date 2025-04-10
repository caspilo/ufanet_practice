package org.example.core.delay;

public class ExponentialDelay implements Delay{

    static private double base;
    static private final double limit = 10000.0;


    static public int getNextDelay(int attemptCount) {
        base = Math.E;
        return (int)Math.pow(base, attemptCount);
    }

    static public int getNextDelay(double baseValue, int attemptCount) {
        base = baseValue;
        return (int)Math.pow(base, attemptCount);
    }
}
