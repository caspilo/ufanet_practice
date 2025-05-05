package org.example.core.service.task;

import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
import org.example.core.monitoring.metrics.TaskMetrics;
import org.example.core.service.delay.DelayService;
import org.example.core.retry_policy.RetryPolicy;

import java.util.HashMap;
import java.util.Map;

public class TaskExecutor {

    private final TaskService taskService;

    private final DelayService delayService;

    Map<Long, Boolean> isTaskRescheduled = new HashMap<>();


    public TaskExecutor(TaskService taskService, DelayService delayService) {
        this.taskService = taskService;
        this.delayService = delayService;
    }

    private void applyRetryPolicy(Long id, DelayParams delayParams, String category) {
        RetryPolicy retryPolicyClass;
        try {
            retryPolicyClass = delayParams.getRetryPolicyClass().newInstance();
            LogService.logger.info(String.format("Retry execute task with id: %s and category: '%s' using retry policy: '%s'",
                    id, category, retryPolicyClass.getClass().getName()));
            long delayValue = retryPolicyClass.calculate(delayParams.getRetryParams());
            if (delayValue >= 0) {
                taskService.rescheduleTask(id, delayValue, category);
            } else {
                taskService.changeTaskStatus(id, TaskStatus.FAILED, category);
                throw new RuntimeException(String.format("Can`t reschedule task with id: %s and category: '%s'. Value of delay = %s it can`t be < 0",
                        id, category, delayValue));
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeRetryPolicyForTask(Long id, String category) {
        LogService.logger.info(String.format("Trying to retry execute task with id: %s and category: '%s'",
                id, category));
        DelayParams delayParams = delayService.getDelayParams(id, category);
        if (delayParams.isWithRetry()) {
            ScheduledTask task = taskService.getTask(id, category);
            int retryCount = task.getRetryCount();
            int maxRetryCount = delayParams.getMaxRetryCount();
            if (isTaskRescheduled.containsKey(id)) {
                if (isTaskRescheduled.get(id).equals(true)) {

                    if (retryCount < maxRetryCount - 1) {
                        applyRetryPolicy(id, delayParams, category);
                        taskService.increaseRetryCountForTask(id, category);
                        LogService.logger.info(String.format("Retrying execute task with id: %s and category: '%s' started. Current attempt = %s",
                                id, category, retryCount + 2));
                    } else {
                        taskService.increaseRetryCountForTask(id, category);
                        LogService.logger.info(String.format("The attempts for retry execute task with id: %s and category: '%s' are over. ",
                                id, category));
                        taskService.changeTaskStatus(id, TaskStatus.FAILED, category);
                        TaskMetrics.taskFailed(category);
                    }
                    return;
                }
            }
            isTaskRescheduled.put(id, true);
            applyRetryPolicy(id, delayParams, category);
            LogService.logger.info(String.format("Retrying execute task with id: %s and category: '%s' started. Current attempt = %s",
                    id, category, retryCount + 1));
            return;
        }
        LogService.logger.info(String.format("Can`t get RetryPolicy. Retry for task with id: %s and category: '%s' is turned off", id, category));
        taskService.changeTaskStatus(id, TaskStatus.FAILED, category);
    }
}