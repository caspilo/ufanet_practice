package org.example.core.service;

import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.service.delay.DelayService;

import static org.example.core.service.delay.DelayCalculator.getNextDelay;

public class TaskExecutor {

    private final TaskService taskService;

    private final DelayService delayService;

    public TaskExecutor(TaskService taskService, DelayService delayService) {
        this.taskService = taskService;
        this.delayService = delayService;
    }

    private void fixedRetryPolicy(Long id) {
        long fixDelayValue = delayService.getDelayParams(id).getFixDelayValue();
        if (fixDelayValue >= 0) {
            taskService.rescheduleTask(id, fixDelayValue);
        } else {
            taskService.changeTaskStatus(id, TaskStatus.FAILED);
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay = " + fixDelayValue + " can`t be < 0");
        }
    }

    private void exponentialRetryPolicy(Long id, int retryCount, double delayBase, long limit) {
        long delayValue = getNextDelay(retryCount, delayBase, limit);
        if (delayValue >= 0) {
            taskService.rescheduleTask(id, delayValue);
        } else {
            taskService.changeTaskStatus(id, TaskStatus.FAILED);
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay = " + delayValue + " can`t be > limit = " + limit);
        }
    }

    public boolean isRetryForTask(Long id) {
        return delayService.getDelayParams(id).isWithRetry();
    }

    public boolean isRetryPolicyForTaskFixed(Long id) {
        if (isRetryForTask(id)) {
            return delayService.getDelayParams(id).isValueIsFixed();
        } else {
            throw new RuntimeException("ERROR. Can`t get RetryPolicy. Retry for task with id: " + id + " is turned off. ");
        }
    }

    public void executeRetryPolicyForTask(Long id) {
        if (isRetryForTask(id)) {
            DelayParams delayParams = delayService.getDelayParams(id);
            ScheduledTask task = taskService.getTask(id);
            int retryCount = task.getRetryCount();
            int maxRetryCount = delayParams.getRetryCount();
            if (retryCount < maxRetryCount) {
                if (!isRetryPolicyForTaskFixed(id)) {
                    exponentialRetryPolicy(id, retryCount, delayParams.getDelayBase(), delayParams.getDelayLimit());
                } else {
                    fixedRetryPolicy(id);
                }
                taskService.increaseRetryCountForTask(id);
                return;
            }
        }
        taskService.changeTaskStatus(id, TaskStatus.FAILED);
    }
}