package org.example.core.service.task;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.JdbcDelayRepository;
import org.example.core.service.delay.DelayPolicy;
import org.example.core.service.delay.DelayService;
import org.example.core.schedulable.Schedulable;
import org.example.holder.ServiceHolder;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Map;

public class TaskScheduler implements TaskSchedulerService {

    private final TaskService taskService;

    private final DelayService delayService;

    public TaskScheduler(){
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
                             boolean fixedRetryPolicy, Long delayBase, Long fixDelayValue, int maxRetryCount, Long delayLimit){
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
            if (!(Class.forName(schedulableClassName).getDeclaredConstructor().newInstance() instanceof Schedulable)) {
                throw new RuntimeException("ERROR. Class with name :" + schedulableClassName + " does not implements interface with name: " + Schedulable.class.getName());
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
                delayParams.setWithRetry(withRetry);
                delayParams.setValueIsFixed(fixedRetryPolicy);
                delayParams.setRetryCount(maxRetryCount);
                delayParams.setDelayLimit(delayLimit);
                delayParams.setFixDelayValue(fixDelayValue);
                delayParams.setDelayBase(delayBase);
                delayService.save(delayParams, category);
            }
            return id;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void cancelTask(Long id, String category) {

        ScheduledTask task = taskService.findById(id, category);

        if (task != null) {
            if (task.getStatus() == TaskStatus.PENDING) {
                taskService.cancelTask(id, category);
            } else {
                throw new RuntimeException("Cannot cancel task with id " + id + ": task status is " + task.getStatus().name());
            }
        } else {
            throw new RuntimeException("Cannot cancel task with id " + id + ": task not found");
        }
    }
}