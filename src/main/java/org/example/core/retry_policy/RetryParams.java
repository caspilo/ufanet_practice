package org.example.core.retry_policy;

import java.util.HashMap;
import java.util.Map;

public class RetryParams {
    private final Map<String, String> params = new HashMap<>();

    public RetryParams(Map<String, String> params) {
        this.params.putAll(params);
    }

    public Long getFixDelayValue() {
        return Long.valueOf(params.get("fixDelayValue"));
    }

    public Long getDelayBase() {
        return Long.valueOf(params.get("delayBase"));
    }

    public Long getDelayLimit() {
        return Long.valueOf(params.get("delayLimit"));
    }

    public Integer getAttemptCount() {
        return Integer.valueOf(params.get("attemptCount"));
    }
}