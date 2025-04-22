package org.example.core.service.task;

import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
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

    private void fixedRetryPolicy(Long id, String category) {
        long fixDelayValue = delayService.getDelayParams(id, category).getFixDelayValue();
        if (fixDelayValue >= 0) {
            taskService.rescheduleTask(id, fixDelayValue, category);
        } else {
            taskService.changeTaskStatus(id, TaskStatus.FAILED, category);
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay = " + fixDelayValue + " can`t be < 0");
        }
    }

    private void exponentialRetryPolicy(Long id, int retryCount, double delayBase, long limit, String category) {
        long delayValue = getNextDelay(retryCount, delayBase, limit);
        if (delayValue >= 0) {
            taskService.rescheduleTask(id, delayValue, category);
        } else {
            taskService.changeTaskStatus(id, TaskStatus.FAILED, category);
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay = " + delayValue + " can`t be > limit = " + limit);
        }
    }

    private void applyRetryPolicy(Long id, int retryCount, DelayParams delayParams, String category) {
        if (!isRetryPolicyForTaskFixed(id, category)) {
            exponentialRetryPolicy(id, retryCount, delayParams.getDelayBase(), delayParams.getDelayLimit(), category);
        } else {
            fixedRetryPolicy(id, category);
        }
    }

    public boolean isRetryForTask(Long id, String category) {
        return delayService.getDelayParams(id, category).isWithRetry();
    }

    public boolean isRetryPolicyForTaskFixed(Long id, String category) {
        if (isRetryForTask(id, category)) {
            return delayService.getDelayParams(id, category).isValueIsFixed();
        } else {
            throw new RuntimeException("ERROR. Can`t get RetryPolicy. Retry for task with id: " + id + " is turned off. ");
        }
    }

    public void executeRetryPolicyForTask(Long id, String category) {
        taskService.changeTaskStatus(id, TaskStatus.FAILED, category);
        DelayParams delayParams = delayService.getDelayParams(id, category);
        if (delayParams.isWithRetry()) {
            ScheduledTask task = taskService.getTask(id, category);
            int retryCount = task.getRetryCount();
            int maxRetryCount = delayParams.getRetryCount();
            if (isTaskRescheduled.containsKey(id)) {
                if (isTaskRescheduled.get(id).equals(true)) {

                    if (retryCount < maxRetryCount - 1) {
                        applyRetryPolicy(id, retryCount, delayParams, category);
                        taskService.increaseRetryCountForTask(id, category);
                    } else {
                        taskService.increaseRetryCountForTask(id, category);
                        taskService.changeTaskStatus(id, TaskStatus.FAILED, category);
                    }
                    return;
                }
            }
            isTaskRescheduled.put(id, true);
            applyRetryPolicy(id, retryCount, delayParams, category);
        }
    }
}