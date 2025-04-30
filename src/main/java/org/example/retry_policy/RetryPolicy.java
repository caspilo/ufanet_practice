package org.example.retry_policy;

import java.util.Map;

public interface RetryPolicy {
    long execute(Map<String,String> retryParams);
}
