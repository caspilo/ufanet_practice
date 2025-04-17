package org.example.core.service.delay;

import org.example.core.entity.DelayParams;
import org.example.core.repository.DelayRepository;

public class DelayPolicy implements DelayService {

    private final DelayRepository delayRepository;

    public DelayPolicy(DelayRepository delayRepository) {
        this.delayRepository = delayRepository;
    }

    @Override
    public DelayParams getDelayParams(Long taskId) {
        DelayParams delayParams = delayRepository.getDelayParams(taskId);
        if (delayParams != null) {
            return delayParams;
        } else {
            throw new RuntimeException("ERROR. Can not get delay parameters for task with id " + taskId + ": task not found");
        }
    }
}
