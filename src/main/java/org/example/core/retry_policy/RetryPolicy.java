package org.example.core.retry_policy;

import java.util.Map;

public interface RetryPolicy {
    long calculate(Map<String,String> retryParams);
}
