package org.example.core.service;

import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.service.delay.DelayService;

import static org.example.core.delay.DelayCalculator.getNextDelay;

public class TaskExecutor {

    private final TaskSchedulerService taskSchedulerService;

    private final TaskService taskService;

    private final DelayService delayService;

    public TaskExecutor(TaskSchedulerService taskSchedulerService, TaskService taskService, DelayService delayService) {
        this.taskSchedulerService = taskSchedulerService;
        this.taskService = taskService;
        this.delayService = delayService;
    }

    private boolean delayTask(Long id, int retryCount) {

        Long delay = getNextDelay(retryCount);

        if (delay > 0) {
            taskSchedulerService.rescheduleTask(id, delay);
        } else {
            taskService.changeTaskStatus(id, TASK_STATUS.FAILED);
        }
        return false;
    }

    private void fixedRetryPolicy(Long id) {
        long delayValue = delayService.getFixedDelayValue(id);
        if (delayValue >= 0) {
            taskSchedulerService.rescheduleTask(id, delayValue);
        } else {
            taskService.changeTaskStatus(id, TASK_STATUS.FAILED);
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay = " + delayValue + " can`t be < 0");
        }
    }

    private void exponentialRetryPolicy(Long id, int retryCount, double delayBase, double limit) {
        long delay = getNextDelay(retryCount, delayBase, limit);
        if (delay >= 0) {
            taskSchedulerService.rescheduleTask(id, delay);
        } else {
            taskService.changeTaskStatus(id, TASK_STATUS.FAILED);
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay = " + delay + " can`t be > limit = " + limit);
        }
    }

    public boolean checkRetryForTask(Long id) {
        return delayService.getRetryStateForTask(id);
    }

    public boolean isRetryPolicyForTaskFixed(Long id) {
        if (checkRetryForTask(id)) {
            return delayService.isRetryForTaskFixed(id);
        } else {
            throw new RuntimeException("ERROR. Can`t get RetryPolicy. Retry for task with id: " + id + " is turned off. ");
        }
    }

    public void executeRetryPolicyForTask(Long id) {
        int retryCount = taskService.getTask(id).getRetryCount();
        int maxRetryCount = delayService.getMaxRetryCount(id);

        if (checkRetryForTask(id)) {
            if (!(retryCount == maxRetryCount)) {
                if (!isRetryPolicyForTaskFixed(id)) {
                    exponentialRetryPolicy(id, retryCount, delayService.getDelayBase(id), delayService.getUpLimit(id));
                    taskService.getTask(id).setRetryCount(retryCount + 1);
                } else {
                    fixedRetryPolicy(id);
                    taskService.getTask(id).setRetryCount(retryCount + 1);
                }
                return;
            }
        }
        taskService.changeTaskStatus(id, TASK_STATUS.FAILED);
    }
}