package org.example.core.retry_policy;

public interface RetryPolicy {
    long calculate(RetryParams retryParams);
}
