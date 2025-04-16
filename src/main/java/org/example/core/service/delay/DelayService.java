package org.example.core.service.delay;

public interface DelayService {

    boolean getRetryStateForTask(Long id);

    Long getFixedDelayValue(Long id);

    boolean isRetryForTaskFixed(Long id);

    int getMaxRetryCount(Long id);

    double getDelayBase(Long id);

    double getUpLimit(Long id);
}