package org.example.core.retry_policy;

import java.util.Map;

public class ExponentialRetryPolicy implements RetryPolicy {
    @Override
    public long calculate(Map<String, String> retryParams) {
        long delayBase = Long.parseLong(retryParams.get("delayBase"));
        int attemptCount = Integer.parseInt(retryParams.get("attemptCount"));
        long delayLimit = Long.parseLong(retryParams.get("delayLimit"));
        long delay = (long) Math.pow(delayBase, attemptCount);
        return delay <= delayLimit ? delay : -1;
    }
}
