package org.example.core.service.task.scheduler;

import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
import org.example.core.schedulable.Schedulable;
import org.example.core.service.delay.DelayService;
import org.example.core.service.task.TaskService;
import org.example.core.validator.DelayValidator;
import org.example.core.validator.ScheduleClassValidator;
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
    public <T extends Schedulable> Long scheduleTask(Class<T> scheduleClass, Map<String, String> params, String executionTime, Delay delay) {
        try {
            LogService.logger.info("Process scheduleTask started");
            validateParams(delay);
            String scheduleClassName = scheduleClass.getName();
            ScheduledTask savedTask = createAndSaveTask(scheduleClassName, params, executionTime);
            if (isRetryableTask(delay)) {
                createAndSaveDelayParams(delay, savedTask);
            }
            LogService.logger.info("Process scheduleTask has been completed. Returns id for task: " + savedTask.getId());
            return savedTask.getId();
        } catch (Exception e) {
            LogService.logger.log(Level.SEVERE, "Process schedule task failed. " + e.getMessage(), e);
            return null;
        }

    }

    private void validateParams(Delay delay) {

        DelayValidator.validateParams(delay);
    }

    private ScheduledTask createAndSaveTask(String scheduleClassName, Map<String, String> params, String executionTime)
            throws ClassNotFoundException {
        ScheduledTask task = new ScheduledTask();
        String category = Class.forName(scheduleClassName).getSimpleName();
        task.setCategory(category);
        task.setCanonicalName(scheduleClassName);
        task.setParams(params);
        task.setExecutionTime(Timestamp.valueOf(executionTime));
        Long id = taskService.save(task, category);
        task.setId(id);
        return task;
    }

    private void createAndSaveDelayParams(Delay delay, ScheduledTask task) {
        DelayParams delayParams = new DelayParams(task.getId());
        delayParams.setWithRetry(isRetryableTask(delay));
        delayParams.setValueIsFixed(delay.isFixedRetryPolicy());
        delayParams.setRetryCount(delay.getMaxRetryCount());
        delayParams.setDelayLimit(delay.getDelayLimit());
        delayParams.setFixDelayValue(delay.getFixDelayValue());
        delayParams.setDelayBase(delay.getDelayBase());
        delayService.save(delayParams, task.getCategory());
    }

    private static boolean isRetryableTask(Delay delay) {
        return delay.isWithRetry();
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