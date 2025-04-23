package org.example.core.service.task.scheduler;


import org.example.core.schedulable.Schedulable;

import java.util.Map;

public interface TaskSchedulerService {
    void cancelTask(Long id, String category);
    Long scheduleTask(Class<? extends Schedulable> scheduleClass, Map<String, String> params,
                      String executionTime, Delay delay);
}