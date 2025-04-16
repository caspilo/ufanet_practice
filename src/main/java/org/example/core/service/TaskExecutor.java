package org.example.core.service;

import org.example.core.delay.DelayCalculator;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.repository.TaskRepository;
import org.example.test.Schedulable;

import java.util.Map;

public class TaskExecutor {

    TaskRepository taskRepository;

    public TaskExecutor(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void executeTask(Schedulable task, Map<String, String> params) {

        

    }

    private boolean delayTask(Long id, int retryCount) {

        int delay = DelayCalculator.getNextDelay(retryCount);

        if (delay > 0) {
            taskRepository.rescheduleTask(id, delay);
        }
        else {
            taskRepository.changeTaskStatus(id, TASK_STATUS.FAILED);
        }

        return delay > 0;
    }
}
