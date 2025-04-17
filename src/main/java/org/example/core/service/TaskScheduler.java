package org.example.core.service;

import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.repository.DelayRepository;
import org.example.core.repository.TaskRepository;
import org.example.test.Schedulable;

import java.sql.Timestamp;
import java.util.Map;

public class TaskScheduler implements TaskSchedulerService {

    private final TaskRepository taskRepository;

    private final DelayRepository delayRepository;

    public TaskScheduler(TaskRepository taskRepository, DelayRepository delayRepository) {
        this.taskRepository = taskRepository;
        this.delayRepository = delayRepository;
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
                false, -1L, -1L, -1, -1L);
    }


    @Override
    public Long scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, boolean withRetry,
                             boolean fixedRetryPolicy, Long delayBase, Long fixDelayValue, int maxRetryCount, Long delayLimit) {
        ScheduledTask task = new ScheduledTask();
        task.setCanonicalName(schedulableClassName);
        task.setParams(params);
        task.setExecutionTime(Timestamp.valueOf(executionTime));
        if (withRetry) {
            DelayParams delayParams = new DelayParams(task.getId());
            delayParams.setRetryCount(maxRetryCount);
            delayParams.setDelayLimit(delayLimit);
            if (fixedRetryPolicy) {
                delayParams.setFixDelayValue(fixDelayValue);
            } else {
                delayParams.setDelayBase(delayBase);
            }
            delayRepository.save(delayParams);
        }
        return taskRepository.save(task);
    }


    @Override
    public void cancelTask(Long id) {

        ScheduledTask task = taskRepository.findById(id);

        if (task != null) {
            if (task.getStatus() == TASK_STATUS.PENDING) {
                taskRepository.cancelTask(id);
                taskRepository.save(task);
            } else {
                throw new RuntimeException("Cannot cancel task with id " + id + ": task status is " + task.getStatus().name());
            }
        } else {
            throw new RuntimeException("Cannot cancel task with id " + id + ": task not found");
        }
    }

    @Override
    public void rescheduleTask(Long id, long delay) {
        taskRepository.rescheduleTask(id, delay);
    }
}