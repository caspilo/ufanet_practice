package org.example.core.retry_policy;

public class ExponentialRetryPolicy implements RetryPolicy {
    @Override
    public long calculate(RetryParams retryParams) {
        long delay = (long) Math.pow(retryParams.getDelayBase(), retryParams.getAttemptCount());
        return delay <= retryParams.getDelayLimit() ? delay : -1;
    }
}