package org.example.core.retry_policy;

import java.util.Map;

public class FixedRetryPolicy implements RetryPolicy {
    @Override
    public long calculate(Map<String, String> retryParams) {
        return Long.parseLong(retryParams.get("fixDelayValue"));
    }
}
