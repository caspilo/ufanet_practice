package org.example.core.repository;

import org.example.core.entity.DelayParams;

public interface DelayRepository {

    DelayParams getDelayParams(Long taskId, String category);
    void save(DelayParams delayParams, String category);
}
