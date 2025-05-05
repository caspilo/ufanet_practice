package org.example.core.service.task.scheduler;


import org.example.core.schedulable.Schedulable;

import java.util.*;

public interface TaskSchedulerService {
    void cancelTask(Long id, String category);
    <T extends Schedulable> Optional<Long> scheduleTask(Class<T> scheduleClass, Map<String, String> params,
                                                        String executionTime, Delay delay);
}