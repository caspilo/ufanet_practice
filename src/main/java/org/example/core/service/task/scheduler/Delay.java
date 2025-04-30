package org.example.core.service.task.scheduler;

import org.example.core.retry_policy.FixedRetryPolicy;
import org.example.core.retry_policy.RetryPolicy;

import java.util.Map;

public class Delay {
    private final boolean withRetry;
    private final Long fixDelayValue;
    private final int maxRetryCount;
    private final Class<? extends RetryPolicy> retryPolicyClass;
    private final Map<String, String> retryParams;

    private Delay(boolean withRetry,
                  Long fixDelayValue,
                  int maxRetryCount,
                  Class<? extends RetryPolicy> retryPolicyClass,
                  Map<String, String> retryParams) {
        this.withRetry = withRetry;
        this.fixDelayValue = fixDelayValue;
        this.maxRetryCount = maxRetryCount;
        this.retryPolicyClass = retryPolicyClass;
        this.retryParams = retryParams;
    }

    public boolean isWithRetry() {
        return withRetry;
    }

    public Long getFixDelayValue() {
        return fixDelayValue;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public Class<? extends RetryPolicy> getRetryPolicyClass() {
        return retryPolicyClass;
    }

    public Map<String, String> getRetryParams() {
        return retryParams;
    }

    public static class DelayBuilder {
        private boolean withRetry = false;
        private Long fixDelayValue = 10000L;
        private int maxRetryCount = 0;
        private Class<? extends RetryPolicy> retryPolicyClass = FixedRetryPolicy.class;
        private Map<String, String> retryParams = Map.of("fixDelayValue", fixDelayValue.toString());

        public DelayBuilder setWithRetry(boolean withRetry) {
            this.withRetry = withRetry;
            return this;
        }


        public DelayBuilder setFixDelayValue(Long fixDelayValue) {
            this.fixDelayValue = fixDelayValue;
            return this;
        }

        public DelayBuilder setMaxRetryCount(int maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
            return this;
        }

        public DelayBuilder setRetryPolicyClass(Class<? extends RetryPolicy> retryPolicyClass) {
            this.retryPolicyClass = retryPolicyClass;
            return this;
        }

        public DelayBuilder setRetryParams(Map<String, String> retryParams) {
            this.retryParams = retryParams;
            return this;
        }

        public Delay build() {
            return new Delay(
                    withRetry,
                    fixDelayValue, maxRetryCount, retryPolicyClass, retryParams);
        }
    }
}
