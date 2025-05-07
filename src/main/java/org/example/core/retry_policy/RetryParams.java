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

    public Integer getCurrentAttempt() {
        return Integer.valueOf(params.get("currentAttempt"));
    }

    public void setCurrentAttempt(int attempt) {
        params.put("currentAttempt", String.valueOf(attempt));
    }
}