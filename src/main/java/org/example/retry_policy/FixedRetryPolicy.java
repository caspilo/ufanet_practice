package org.example.retry_policy;

import java.util.Map;

public class FixedRetryPolicy implements RetryPolicy {
    @Override
    public long execute(Map<String, String> retryParams) {
        return Long.parseLong(retryParams.get("fixDelayValue"));
    }
}
