package org.example.core.repository;

import org.example.core.entity.DelayParams;

public interface DelayRepository {

    DelayParams getDelayParams(Long taskId);
    void save(DelayParams delayParams);
}
