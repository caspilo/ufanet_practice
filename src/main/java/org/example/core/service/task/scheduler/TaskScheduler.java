package org.example.core.service.task.scheduler;

import org.example.core.entity.*;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
import org.example.core.monitoring.MetricRegisterer;
import org.example.core.monitoring.metrics.*;
import org.example.core.schedulable.Schedulable;
import org.example.core.service.delay.DelayService;
import org.example.core.service.task.TaskService;
import org.example.core.validator.DelayValidator;
import org.example.core.holder.ServiceHolder;

import java.sql.Timestamp;
import java.util.*;

public class TaskScheduler implements TaskSchedulerService {

    private final TaskService taskService;

    private final DelayService delayService;

    private final MetricRegisterer metricRegisterer;

    public TaskScheduler(MetricRegisterer metricRegisterer) {
        this.metricRegisterer = metricRegisterer;
        this.taskService = ServiceHolder.getTaskService();
        this.delayService = ServiceHolder.getDelayService();
    }

    @Override
    public <T extends Schedulable> Optional<Long> scheduleTask(Class<T> scheduleClass, Map<String, String> params, String executionTime, Delay delay) {
        try {
            LogService.logger.info("Process scheduleTask for '" + scheduleClass.getName() + "' started");
            if (!DelayValidator.validateParams(delay)) {
                throw new RuntimeException("Delay params validation failed");
            }
            ScheduledTask savedTask = createAndSaveTask(scheduleClass, params, executionTime);
            createAndSaveDelayParams(delay, savedTask);

            LogService.logger.info("Process scheduleTask has been completed. Returns id for task: " + savedTask.getId());
            return Optional.of(savedTask.getId());
        } catch (Exception e) {
            LogService.logger.severe("Process schedule task failed. " + e.getMessage());
            return Optional.empty();
        } finally {
            metricRegisterer.registerMetric(scheduleClass.getSimpleName(), MetricType.SCHEDULED_TASK_COUNT);
            metricRegisterer.registerMetric(scheduleClass.getSimpleName(), MetricType.FAILED_TASK_COUNT);
            metricRegisterer.registerMetric(scheduleClass.getSimpleName(), MetricType.TASK_AVERAGE_TIME_EXECUTION);
            TaskMetrics.taskScheduled(scheduleClass.getSimpleName());
        }
    }

    private <T extends Schedulable> ScheduledTask createAndSaveTask(Class<T> scheduleClass, Map<String, String> params, String executionTime) {
        ScheduledTask task = new ScheduledTask();
        String category = scheduleClass.getSimpleName();
        task.setCategory(category);
        task.setCanonicalName(scheduleClass.getName());
        task.setParams(params);
        task.setExecutionTime(Timestamp.valueOf(executionTime));
        task.setId(taskService.save(task, category));
        return task;
    }

    private void createAndSaveDelayParams(Delay delay, ScheduledTask task) {
        DelayParams delayParams = new DelayParams(task.getId());
        delayParams.setWithRetry(delay.isWithRetry());
        delayParams.setValueIsFixed(delay.isFixedRetryPolicy());
        delayParams.setRetryCount(delay.getMaxRetryCount());
        delayParams.setDelayLimit(delay.getDelayLimit());
        delayParams.setFixDelayValue(delay.getFixDelayValue());
        delayParams.setDelayBase(delay.getDelayBase());
        delayService.save(delayParams, task.getCategory());
    }

    @Override
    public void cancelTask(Long id, String category) {
        LogService.logger.info(String.format("Process cancel task with id: %s and category: '%s' started", id, category));
        ScheduledTask task = taskService.findById(id, category);
        try {
            tryToCancelTask(id, category, task);
        } catch (Exception e) {
            LogService.logger.severe(String.format("Process cancel task with id: %s and category: '%s' has been failed. ", id, category) + e.getMessage());
        }
    }

    private void tryToCancelTask(Long id, String category, ScheduledTask task) {
        if (task != null) {
            if (task.getStatus() == TaskStatus.PENDING) {
                taskService.cancelTask(id, category);
                LogService.logger.info(String.format("Process cancel Task with id: %s and category: '%s' completed. Task has been canceled successfully", id, category));
            } else {
                throw new RuntimeException(String.format("Cannot cancel task with id: %s and category: '%s'. Task status is '%s'", id, category, task.getStatus().name()));
            }
        } else {
            throw new RuntimeException(String.format("Cannot cancel task with id: %s and category: '%s'. Task not found", id, category));
        }
    }
}