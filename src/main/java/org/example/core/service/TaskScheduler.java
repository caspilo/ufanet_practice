package org.example.core.service;

import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.repository.DelayRepository;
import org.example.core.repository.TaskRepository;
import org.example.core.task.Schedulable;

import java.lang.reflect.InvocationTargetException;
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
    public void scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime) {
        scheduleTask(schedulableClass.getClass(), params, executionTime);
    }


    @Override
    public void scheduleTask(Class objectClass, Map<String, String> params, String executionTime) {
        scheduleTask(objectClass.getName(), params, executionTime);
    }


    @Override
    public void scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime) {
        scheduleTask(schedulableClassName, params, executionTime, false,
                false, 0L, 0L, 0, 0L);
    }


    @Override
    public void scheduleTask(String schedulableClassName, Map<String, String> params, String executionTime, boolean withRetry,
                             boolean fixedRetryPolicy, Long delayBase, Long fixDelayValue, int maxRetryCount, Long delayLimit) {
        ScheduledTask task = new ScheduledTask();
        try {
            if (!(Class.forName(schedulableClassName).getDeclaredConstructor().newInstance() instanceof Schedulable)) {
                throw new RuntimeException("ERROR. Class with name :" + schedulableClassName + " does not implements class with name: " + Schedulable.class.getName());
            }
            task.setCategory(Class.forName(schedulableClassName).getSimpleName());
            task.setCanonicalName(schedulableClassName);
            task.setParams(params);
            task.setExecutionTime(Timestamp.valueOf(executionTime));
            task.setId(taskRepository.save(task));
            if (withRetry) {
                if (maxRetryCount < 0) {
                    throw new RuntimeException("ERROR. Incorrect value for parameter maxRetryCount = " + maxRetryCount + ". Value can`t be < 0");
                }
                if (delayLimit < 0) {
                    throw new RuntimeException("ERROR. Incorrect value for parameter delayLimit = " + delayLimit + ". Value can`t be < 0");
                }
                if (delayBase < 0) {
                    throw new RuntimeException("ERROR. Incorrect value for parameter delayBase = " + delayBase + ". Value can`t be < 0");
                }
                if (fixDelayValue < 0) {
                    throw new RuntimeException("ERROR. Incorrect value for parameter fixDelayValue = " + fixDelayValue + ". Value can`t be < 0");
                }
                DelayParams delayParams = new DelayParams(task.getId());
                delayParams.setRetryCount(maxRetryCount);
                delayParams.setDelayLimit(delayLimit);
                delayParams.setFixDelayValue(fixDelayValue);
                delayParams.setDelayBase(delayBase);
                delayParams.setWithRetry(withRetry);
                delayRepository.save(delayParams);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void cancelTask(Long id) {

        ScheduledTask task = taskRepository.findById(id);

        if (task != null) {
            if (task.getStatus() == TASK_STATUS.PENDING) {
                taskRepository.cancelTask(id);
            } else {
                throw new RuntimeException("Cannot cancel task with id " + id + ": task status is " + task.getStatus().name());
            }
        } else {
            throw new RuntimeException("Cannot cancel task with id " + id + ": task not found");
        }
    }

    @Override
    public void rescheduleTask(Long id, long delay) {
        if (delay >= 0) {
            taskRepository.rescheduleTask(id, delay);
            taskRepository.changeTaskStatus(id, TASK_STATUS.PENDING);
        } else {
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay < 0");
        }
    }
}