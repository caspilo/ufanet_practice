package org.example.core.service.task.scheduler;


import java.util.Map;

public interface TaskSchedulerService {
    void cancelTask(Long id, String category);
    Long scheduleTask(String scheduleClassName, Map<String, String> params,
                      String executionTime, Delay delay);
}