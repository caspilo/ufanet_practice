package org.example.core.entity;

import org.example.core.retry_policy.FixedRetryPolicy;
import org.example.core.retry_policy.RetryPolicy;

import java.util.HashMap;
import java.util.Map;

public class DelayParams {

    private Long taskId;
    private boolean withRetry = false;
    private int maxRetryCount = 0;
    private Long fixDelayValue = null;
    private Class<? extends RetryPolicy> retryPolicyClass = FixedRetryPolicy.class;
    private Map<String, String> retryParams = new HashMap<>();

    public DelayParams(Long taskId) {
        this.taskId = taskId;
    }

    public DelayParams(Long taskId, boolean withRetry, int maxRetryCount, Long fixDelayValue, Class<? extends RetryPolicy> retryPolicyClass,
                       Map<String, String> retryParams) {
        this.taskId = taskId;
        this.withRetry = withRetry;
        this.maxRetryCount = maxRetryCount;
        this.retryPolicyClass = retryPolicyClass;
        this.fixDelayValue = fixDelayValue;
        this.retryParams = retryParams;
        this.retryParams.putIfAbsent("fixDelayValue", fixDelayValue.toString());
    }


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public boolean isWithRetry() {
        return withRetry;
    }

    public void setWithRetry(boolean withRetry) {
        this.withRetry = withRetry;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public Long getFixDelayValue() {
        return fixDelayValue;
    }

    public void setFixDelayValue(Long fixDelayValue) {
        this.fixDelayValue = fixDelayValue;
    }

    public Class<? extends RetryPolicy> getRetryPolicyClass() {
        return retryPolicyClass;
    }

    public void setRetryPolicyClass(Class<? extends RetryPolicy> retryPolicyClass) {
        this.retryPolicyClass = retryPolicyClass;
    }

    public Map<String, String> getRetryParams() {
        return retryParams;
    }

    public void setRetryParams(Map<String, String> retryParams) {
        this.retryParams = retryParams;
    }

    public void addRetryParams(String key, String value) {
        this.retryParams.putIfAbsent(key, value);
    }

    public void setRetryPolicyClass(String retryPolicyClassName) {
        try {
            setRetryPolicyClass((Class<? extends RetryPolicy>) Class.forName(retryPolicyClassName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
