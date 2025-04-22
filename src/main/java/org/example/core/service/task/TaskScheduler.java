package org.example.core.service.task;

import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
import org.example.core.schedulable.Schedulable;
import org.example.core.service.delay.DelayService;
import org.example.holder.ServiceHolder;

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
    public Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime) {
        return scheduleTask(schedulableClass.getClass(), params, executionTime);
    }


    @Override
    public Long scheduleTask(Class objectClass, Map<String, String> params, String executionTime) {
        return scheduleTask(objectClass.getName(), params, executionTime);
    }


    @Override
    public Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime) {
        return scheduleTask(schedulableClassName, params, executionTime, false,
                false, 0L, 0L, 0, 0L);
    }


    @Override
    public Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime, boolean withRetry,
                             boolean fixedRetryPolicy, Long delayBase, Long fixDelayValue, int maxRetryCount, Long delayLimit) {
        return scheduleTask(schedulableClass.getClass(), params, executionTime, withRetry, fixedRetryPolicy, delayBase, fixDelayValue, maxRetryCount, delayLimit);
    }


    @Override
    public Long scheduleTask(Class objectClass, Map<String, String> params, String executionTime, boolean withRetry,
                             boolean fixedRetryPolicy, Long delayBase, Long fixDelayValue, int maxRetryCount, Long delayLimit) {
        return scheduleTask(objectClass.getName(), params, executionTime, withRetry, fixedRetryPolicy, delayBase, fixDelayValue, maxRetryCount, delayLimit);
    }


    @Override
    public Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, boolean withRetry,
                             boolean fixedRetryPolicy, Long delayBase, Long fixDelayValue, int maxRetryCount, Long delayLimit) {
        try {
            LogService.logger.info("Process scheduleTask started");

            if (!(Class.forName(schedulableClassName).getDeclaredConstructor().newInstance() instanceof Schedulable)) {
                throw new RuntimeException("Class with name :" + schedulableClassName + " does not implements interface with name: " + Schedulable.class.getName());
            }
            if (maxRetryCount < 0) {
                throw new RuntimeException("Incorrect value of parameter maxRetryCount = " + maxRetryCount + ". Value can`t be < 0");
            }
            if (delayLimit < 0) {
                throw new RuntimeException("Incorrect value for parameter delayLimit = " + delayLimit + ". Value can`t be < 0");
            }
            if (delayBase < 0) {
                throw new RuntimeException("Incorrect value for parameter delayBase = " + delayBase + ". Value can`t be < 0");
            }
            if (fixDelayValue < 0) {
                throw new RuntimeException("Incorrect value for parameter fixDelayValue = " + fixDelayValue + ". Value can`t be < 0");
            }
            ScheduledTask task = new ScheduledTask();

            String category = Class.forName(schedulableClassName).getSimpleName();

            task.setCategory(category);
            task.setCanonicalName(schedulableClassName);
            task.setParams(params);
            task.setExecutionTime(Timestamp.valueOf(executionTime));
            Long id = taskService.save(task, category);
            task.setId(id);
            if (withRetry) {

                DelayParams delayParams = new DelayParams(task.getId());
                delayParams.setWithRetry(withRetry);
                delayParams.setValueIsFixed(fixedRetryPolicy);
                delayParams.setRetryCount(maxRetryCount);
                delayParams.setDelayLimit(delayLimit);
                delayParams.setFixDelayValue(fixDelayValue);
                delayParams.setDelayBase(delayBase);
                delayService.save(delayParams, category);
            }
            LogService.logger.info("Process scheduleTask has been completed. Returns id for task: " + id);
            return id;

        } catch (Exception e) {
            LogService.logger.log(Level.SEVERE, "Process schedule task failed. " + e.getMessage(), e);
            return null;
        }
    }


    @Override
    public void cancelTask(Long id, String category) {
        LogService.logger.info(String.format("Process cancel task with id: %s and category: '%s' started", id, category));
        ScheduledTask task = taskService.findById(id, category);
        try {

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
        } catch (Exception e) {
            LogService.logger.log(Level.SEVERE, String.format("Process cancel task with id: %s and category: '%s' has been failed. ", id, category) + e.getMessage(), e);
        }
    }
}