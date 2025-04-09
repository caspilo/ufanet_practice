package org.example.core.delay;

public interface Delay {

    static int getNextDelay(int attemptCount){
        return 10*attemptCount;
    };

    static int getNextDelay(double base, int attemptCount) {
        return 10*(int)(base*attemptCount);
    };
}
