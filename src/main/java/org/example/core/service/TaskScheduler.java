package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.repository.TaskRepository;
import org.example.test.Schedulable;

import java.sql.Timestamp;
import java.util.Map;

public class TaskScheduler implements TaskSchedulerService {

    private final TaskRepository taskRepository;

    public TaskScheduler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime, double delayBase) {
        return scheduleTask(schedulableClass.getClass(), params, executionTime, delayBase);
    }

    public Long scheduleTask(Class objectClass, Map<String, String> params, String executionTime, double delayBase) {
        return scheduleTask(objectClass.getName(), params, executionTime, delayBase);
    }

    public Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, double delayBase) {
        return scheduleTask(schedulableClassName, params, executionTime, delayBase, false,
                false, -1,-1, -1);
    }

    public Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, double delayBase,
                             boolean withRetry, boolean fixedRetryPolicy, double fixDelayValue, int retryCount, int upLimit) {
        ScheduledTask task = new ScheduledTask();
        task.setCanonicalName(schedulableClassName);
        task.setParams(params);
        task.setExecutionTime(Timestamp.valueOf(executionTime));
        if (withRetry) {

        }

        return taskRepository.save(task);
    }

    public void cancelTask(Long id) {
        if (!taskRepository.findById(id).getStatus().equals(TASK_STATUS.COMPLETED)) {
            taskRepository.changeTaskStatus(id, TASK_STATUS.CANCELED);
        } else {
            throw new RuntimeException("ERROR. Can`t cancel this task. Status for task with id: " + id + "is COMPLETED.");
        }
    }

    public void rescheduleTask(Long id, Long delay) {
        taskRepository.rescheduleTask(id, delay);
    }
}