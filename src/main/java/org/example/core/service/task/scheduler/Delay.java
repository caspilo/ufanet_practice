package org.example.core.service.task.scheduler;

public class Delay {
    private final boolean withRetry;
    private final boolean fixedRetryPolicy;
    private final Long delayBase;
    private final Long fixDelayValue;
    private final int maxRetryCount;
    private final Long delayLimit;

    private Delay(boolean withRetry,
                  boolean fixedRetryPolicy,
                  Long delayBase,
                  Long fixDelayValue,
                  int maxRetryCount,
                  Long delayLimit) {
        this.withRetry = withRetry;
        this.fixedRetryPolicy = fixedRetryPolicy;
        this.delayBase = delayBase;
        this.fixDelayValue = fixDelayValue;
        this.maxRetryCount = maxRetryCount;
        this.delayLimit = delayLimit;
    }

    public boolean isWithRetry() {
        return withRetry;
    }

    public boolean isFixedRetryPolicy() {
        return fixedRetryPolicy;
    }

    public Long getDelayBase() {
        return delayBase;
    }

    public Long getFixDelayValue() {
        return fixDelayValue;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public Long getDelayLimit() {
        return delayLimit;
    }

    @Override
    public String toString() {
        return "Delay{" +
                "withRetry=" + withRetry +
                ", fixedRetryPolicy=" + fixedRetryPolicy +
                ", delayBase=" + delayBase +
                ", fixDelayValue=" + fixDelayValue +
                ", maxRetryCount=" + maxRetryCount +
                ", delayLimit=" + delayLimit +
                '}';
    }

    public static class DelayBuilder {
        private boolean withRetry = false;
        private boolean fixedRetryPolicy = false;
        private Long delayBase = 0L;
        private Long fixDelayValue = 0L;
        private int maxRetryCount = 0;
        private Long delayLimit = 0L;

        public DelayBuilder setWithRetry(boolean withRetry) {
            this.withRetry = withRetry;
            return this;
        }

        public DelayBuilder setFixedRetryPolicy(boolean fixedRetryPolicy) {
            this.fixedRetryPolicy = fixedRetryPolicy;
            return this;
        }

        public DelayBuilder setDelayBase(Long delayBase) {
            this.delayBase = delayBase;
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

        public DelayBuilder setDelayLimit(Long delayLimit) {
            this.delayLimit = delayLimit;
            return this;
        }

        public Delay build() {
            return new Delay(
                    withRetry, fixedRetryPolicy,
                    delayBase, fixDelayValue,
                    maxRetryCount, delayLimit);
        }
    }
}
