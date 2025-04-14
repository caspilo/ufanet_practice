package org.example.core.service;

import org.example.core.delay.ExponentialDelay;
import org.example.core.repository.TaskRepository;
import org.example.test.Schedualable;

import java.util.Map;

public class TaskExecutor {

    TaskRepository taskRepository;

    public TaskExecutor(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void executeTask(Schedualable task, Map<String, String> params) {

    }

    private boolean delayTask(Long id, int retryCount) {

        int delay = ExponentialDelay.getNextDelay(retryCount);

        taskRepository.rescheduleTask(id, ExponentialDelay.getNextDelay(retryCount));

        return delay > 0;
    }
}
