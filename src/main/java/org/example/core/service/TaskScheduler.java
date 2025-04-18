package org.example.core.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.entity.DelayParams;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.repository.DelayRepository;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.JdbcDelayRepository;
import org.example.core.repository.TaskRepository;
import org.example.core.task.Schedulable;
import org.example.worker.TaskWorkerPool;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Map;

public class TaskScheduler implements TaskSchedulerService {

    private final DataSource dataSource;

    private static TaskRepository taskRepository;

    private static DelayRepository delayRepository;

    public TaskScheduler(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        this.dataSource = new HikariDataSource(config);
    }

    public TaskScheduler(DataSource dataSource){
        this.dataSource = dataSource;
    }

    private void setRepositories(String category) {
        if (!TaskWorkerPool.taskRepositories.containsKey(category)) {
            TaskWorkerPool.taskRepositories.put(category, new JdbcTaskRepository(dataSource, category));
            TaskWorkerPool.delayRepositories.put(category, new JdbcDelayRepository(dataSource, category));
        }

        taskRepository = TaskWorkerPool.taskRepositories.get(category);
        delayRepository = TaskWorkerPool.delayRepositories.get(category);
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
            setRepositories(category);

            task.setCategory(category);
            task.setCanonicalName(schedulableClassName);
            task.setParams(params);
            task.setExecutionTime(Timestamp.valueOf(executionTime));
            Long id = taskRepository.save(task);
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
                delayParams.setRetryCount(maxRetryCount);
                delayParams.setDelayLimit(delayLimit);
                if (fixedRetryPolicy) {
                    delayParams.setFixDelayValue(fixDelayValue);
                } else {
                    delayParams.setDelayBase(delayBase);
                }
                delayRepository.save(delayParams);
            }

            return id;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    public void cancelTask(Long id, String category) {

        setRepositories(category);
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
    public void rescheduleTask(Long id, String category, long delay) {
        setRepositories(category);
        if (delay >= 0) {
            taskRepository.rescheduleTask(id, delay);
            taskRepository.changeTaskStatus(id, TASK_STATUS.PENDING);
        } else {
            throw new RuntimeException("ERROR. Can`t reschedule task with id: " + id + ". Value of delay < 0");
        }
    }
}