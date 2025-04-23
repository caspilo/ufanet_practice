package org.example.core.service.task.scheduler;

import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
import org.example.core.metrics.MetricsCollector;
import org.example.core.schedulable.Schedulable;
import org.example.core.service.delay.DelayService;
import org.example.core.service.task.TaskService;
import org.example.holder.ServiceHolder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.logging.Level;

public class TaskScheduler implements TaskSchedulerService {

    private final TaskService taskService;

    private final DelayService delayService;

    public TaskScheduler() {
        this.taskService = ServiceHolder.getTaskService();
        this.delayService = ServiceHolder.getDelayService();
    }

    @Override
    public Long scheduleTask(Class<? extends Schedulable> scheduleClass, Map<String, String> params, String executionTime, Delay delay) {
        try {
            LogService.logger.info("Process scheduleTask started");
            validateParams(delay);
            ScheduledTask savedTask = createAndSaveTask(scheduleClass, params, executionTime);
            createAndSaveDelayParams(delay, savedTask);
            LogService.logger.info("Process scheduleTask has been completed. Returns id for task: " + savedTask.getId());
            return savedTask.getId();
        } catch (Exception e) {
            LogService.logger.log(Level.SEVERE, "Process schedule task failed. " + e.getMessage(), e);
            return null;
        } finally {
            MetricsCollector.taskScheduled(scheduleClass.getSimpleName());
        }
    }

    private void validateParams(Delay delay) {
        if (delay.getMaxRetryCount() < 0) {
            throw new RuntimeException("Incorrect value of parameter maxRetryCount = " + delay.getMaxRetryCount() + ". Value can`t be < 0");
        }
        if (delay.getDelayLimit() < 0) {
            throw new RuntimeException("Incorrect value for parameter delayLimit = " + delay.getDelayLimit() + ". Value can`t be < 0");
        }
        if (delay.getDelayBase() < 0) {
            throw new RuntimeException("Incorrect value for parameter delayBase = " + delay.getDelayBase() + ". Value can`t be < 0");
        }
        if (delay.getFixDelayValue() < 0) {
            throw new RuntimeException("Incorrect value for parameter fixDelayValue = " + delay.getFixDelayValue() + ". Value can`t be < 0");
        }
    }

    private ScheduledTask createAndSaveTask(Class<? extends Schedulable> scheduleClass,
                                            Map<String, String> params, String executionTime) {
        ScheduledTask task = new ScheduledTask();
        String category = scheduleClass.getSimpleName();
        task.setCategory(category);
        task.setCanonicalName(scheduleClass.getCanonicalName());
        task.setParams(params);
        task.setExecutionTime(Timestamp.valueOf(executionTime));
        Long id = taskService.save(task, category);
        task.setId(id);
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
            LogService.logger.log(Level.SEVERE, String.format("Process cancel task with id: %s and category: '%s' has been failed. ", id, category) + e.getMessage(), e);
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