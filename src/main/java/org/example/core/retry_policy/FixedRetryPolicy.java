package org.example.core.retry_policy;

public class FixedRetryPolicy implements RetryPolicy {
    @Override
    public long calculate(RetryParams retryParams) {
        return retryParams.getFixDelayValue();
    }
}