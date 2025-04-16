package org.example.core.entity;

public class DelayParams {

    private Long taskId;
    private boolean withRetry = false;
    private int retryCount = 0;
    private boolean valueIsFixed = true;
    private Long delayValue = null;
    private Long delayBase = null;
    private Long delayLimit = null;

    public DelayParams(Long taskId) {
        this.taskId = taskId;
    }

    public DelayParams(Long taskId, boolean withRetry, int retryCount, boolean valueIsFixed, Long delayValue, Long delayBase, Long delayLimit) {
        this.taskId = taskId;
        this.withRetry = withRetry;
        this.retryCount = retryCount;
        this.valueIsFixed = valueIsFixed;
        this.delayValue = delayValue;
        this.delayBase = delayBase;
        this.delayLimit = delayLimit;
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

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isValueIsFixed() {
        return valueIsFixed;
    }

    public void setValueIsFixed(boolean valueIsFixed) {
        this.valueIsFixed = valueIsFixed;
    }

    public Long getDelayValue() {
        return delayValue;
    }

    public void setDelayValue(Long delayValue) {
        this.delayValue = delayValue;
    }

    public Long getDelayBase() {
        return delayBase;
    }

    public void setDelayBase(Long delayBase) {
        this.delayBase = delayBase;
    }

    public Long getDelayLimit() {
        return delayLimit;
    }

    public void setDelayLimit(Long delayLimit) {
        this.delayLimit = delayLimit;
    }
}
