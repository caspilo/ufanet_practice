package org.example.core.service.task;

import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
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
            throw new RuntimeException(String.format("Can`t reschedule task with id: %s and category: '%s'. Value of delay = %s it can`t be < 0",
                    id, category, fixDelayValue));
        }
    }

    private void exponentialRetryPolicy(Long id, int retryCount, double delayBase, long limit, String category) {
        long delayValue = getNextDelay(retryCount, delayBase, limit);
        if (delayValue >= 0) {
            taskService.rescheduleTask(id, delayValue, category);
        } else {
            taskService.changeTaskStatus(id, TaskStatus.FAILED, category);
            throw new RuntimeException(String.format("Can`t reschedule task with id: %s and category: '%s'. Value of delay = %s it can`t be > limit = %s",
                    id, category, delayValue, limit));
        }
    }

    private void applyRetryPolicy(Long id, int retryCount, DelayParams delayParams, String category) {
        if (delayParams.isWithRetry()) {
            if (delayParams.isValueIsFixed()) {
                LogService.logger.info(String.format("Retry execute task with id: %s and category: '%s' using fixed retry policy",
                        id, category));
                fixedRetryPolicy(id, category);
            } else {
                LogService.logger.info(String.format("Retry execute task with id: %s and category: '%s' using exponential retry policy",
                        id, category));
                exponentialRetryPolicy(id, retryCount, delayParams.getDelayBase(), delayParams.getDelayLimit(), category);
            }
        } else {
            LogService.logger.warning(String.format("Can`t get RetryPolicy. Retry for task with id: %s and category: '%s' is turned off", id, category));
        }
    }

    public void executeRetryPolicyForTask(Long id, String category) {
        LogService.logger.info(String.format("Trying to retry execute task with id: %s and category: '%s'",
                id, category));
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
                        LogService.logger.info(String.format("Retrying execute task with id: %s and category: '%s' started. Current attempt = %s",
                                id, category, retryCount + 2));
                    } else {
                        taskService.increaseRetryCountForTask(id, category);
                        LogService.logger.info(String.format("The attempts for retry execute task with id: %s and category: '%s' are over. ",
                                id, category));
                        taskService.changeTaskStatus(id, TaskStatus.FAILED, category);
                    }
                    return;
                }
            }
            isTaskRescheduled.put(id, true);
            applyRetryPolicy(id, retryCount, delayParams, category);
            LogService.logger.info(String.format("Retrying execute task with id: %s and category: '%s' started. Current attempt = %s",
                    id, category, retryCount + 1));
            return;
        }
        LogService.logger.info(String.format("Failed to retry execute task with id: %s and category: '%s'",
                id, category));
        taskService.changeTaskStatus(id, TaskStatus.FAILED, category);
    }
}