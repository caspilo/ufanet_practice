package org.example.core.service.delay;

public interface DelayService {

    boolean getRetryStateForTask(Long id);

    long getFixedDelayValue(Long id);

    boolean isRetryForTaskFixed(Long id);

    int getMaxRetryCount(Long id);

    double getDelayBase(Long id);

    long getUpLimit(Long id);
}