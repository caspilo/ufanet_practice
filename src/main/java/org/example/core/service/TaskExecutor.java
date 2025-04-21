package org.example.core.service;

import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.service.delay.DelayService;

import java.util.HashMap;
import java.util.Map;

import static org.example.core.service.delay.DelayCalculator.getNextDelay;

public class TaskExecutor {

    private final TaskService taskService;

    private final DelayService delayService;
    Map<Long, Boolean> isTaskRescheduled = new HashMap<>();


    public TaskExecutor(TaskService taskService, DelayService delayService) {
        this.taskService = taskService;
        this.delayService = delayService;
    }

    private void fixedRetryPolicy(Long id) {
        long fixDelayValue = delayService.getDelayParams(id).getFixDelayValue();
        if (fixDelayValue >= 0) {
            taskService.rescheduleTask(id, fixDelayValue);
        } else {
            taskService.changeTaskStatus(id, TASK_STATUS.FAILED);
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay = " + fixDelayValue + " can`t be < 0");
        }
    }

    private void exponentialRetryPolicy(Long id, int retryCount, double delayBase, long limit) {
        long delayValue = getNextDelay(retryCount, delayBase, limit);
        if (delayValue >= 0) {
            taskService.rescheduleTask(id, delayValue);
        } else {
            taskService.changeTaskStatus(id, TASK_STATUS.FAILED);
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay = " + delayValue + " can`t be > limit = " + limit);
        }
    }

    private void applyRetryPolicy(Long id, int retryCount, DelayParams delayParams) {
        if (!isRetryPolicyForTaskFixed(id)) {
            exponentialRetryPolicy(id, retryCount, delayParams.getDelayBase(), delayParams.getDelayLimit());
        } else {
            fixedRetryPolicy(id);
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
        taskService.changeTaskStatus(id, TASK_STATUS.FAILED);
        DelayParams delayParams = delayService.getDelayParams(id);
        if (delayParams.isWithRetry()) {
            ScheduledTask task = taskService.getTask(id);
            int retryCount = task.getRetryCount();
            int maxRetryCount = delayParams.getRetryCount();
            if (isTaskRescheduled.containsKey(id)) {
                if (isTaskRescheduled.get(id).equals(true)) {

                    if (retryCount < maxRetryCount - 1) {
                        applyRetryPolicy(id, retryCount, delayParams);
                        taskService.increaseRetryCountForTask(id);
                    } else {
                        taskService.increaseRetryCountForTask(id);
                        taskService.changeTaskStatus(id, TASK_STATUS.FAILED);
                    }
                    return;
                }
            }
            isTaskRescheduled.put(id, true);
            applyRetryPolicy(id, retryCount, delayParams);
        }
    }
}