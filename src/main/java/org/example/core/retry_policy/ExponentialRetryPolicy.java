package org.example.core.retry_policy;

public class ExponentialRetryPolicy implements RetryPolicy {
    @Override
    public long calculate(RetryParams retryParams) {
        long delay = (long) Math.pow((double) retryParams.getDelayBase() / 1000, retryParams.getCurrentAttempt()) * 1000;
        return delay <= retryParams.getDelayLimit() ? delay : -1;
    }
}