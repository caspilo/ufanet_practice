package org.example.core.service.delay;

import org.example.core.entity.DelayParams;
import org.example.core.logging.LogService;
import org.example.core.repository.DelayRepository;

public class DelayPolicy implements DelayService {

    private final DelayRepository delayRepository;

    public DelayPolicy(DelayRepository delayRepository) {
        this.delayRepository = delayRepository;
    }

    @Override
    public DelayParams getDelayParams(Long taskId, String category) {
        DelayParams delayParams = delayRepository.getDelayParams(taskId, category);
        if (delayParams != null) {
            return delayParams;
        } else {
            throw new RuntimeException(String.format("Can not get delay parameters for task with id: %s and category: '%s' task not found",
                    taskId, category));
        }
    }

    @Override
    public void save(DelayParams delayParams, String category) {
        delayRepository.save(delayParams, category);
        LogService.logger.info(String.format("DelayParams for task with id: %s and category: '%s' successfully created: object %s",
                delayParams.getTaskId(), category, delayParams));
    }
}
