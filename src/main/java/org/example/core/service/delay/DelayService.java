package org.example.core.service.delay;

import org.example.core.entity.DelayParams;

public interface DelayService {

    DelayParams getDelayParams(Long taskId, String category);

    void save(DelayParams delayParams, String category);
}