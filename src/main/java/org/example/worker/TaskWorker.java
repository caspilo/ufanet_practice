package org.example.worker;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
import org.example.core.monitoring.MetricRegisterer;
import org.example.core.monitoring.metrics.*;
import org.example.core.schedulable.Schedulable;
import org.example.core.service.task.*;
import org.example.holder.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskWorker implements Runnable {
    private final UUID workerId;
    private final String category;
    private final TaskService taskService;
    private final TaskExecutor taskExecutor;
    private final Random random = new Random();

    private AtomicBoolean doStop = new AtomicBoolean(false);
    private MetricService metricService;

    public TaskWorker(String category, UUID workerId) {
        this.workerId = workerId;
        this.taskService = ServiceHolder.getTaskService();
        this.taskExecutor = ExecutorHolder.getTaskExecutor();
        this.category = category;
    }

    public void initMetrics(MetricRegisterer metricRegisterer) {
        this.metricService = new MetricService(metricRegisterer, category, this);
        this.metricService.registerMetrics();
    }

    public UUID getWorkerId() {
        return workerId;
    }

    public double getMetric(MetricType metricType) {
        return metricService.getMetric(metricType);
    }

    public void doStop() {
        this.doStop.set(true);
    }

    private boolean keepRunning() {
        return !this.doStop.get();
    }

    private synchronized boolean executeTask(Schedulable task, Map<String, String> params) {
        return task.execute(params);
    }

    @Override
    public void run() {
        try {
            while (keepRunning()) {
                long workerWaitStartTime = System.currentTimeMillis();
                Thread.sleep(random.nextInt(2000));
                LogService.logger.info(String.format("Worker with id: %s and category '%s' searching ready tasks...",
                        workerId, category));
                ScheduledTask nextTask = null;
                try {
                    nextTask = taskService.getNextReadyTaskByCategory(category);
                } catch (Exception e) {
                    LogService.logger.severe("Table for category: '" + category + "' not found. " + e.getMessage());
                }
                if (nextTask != null) {
                    long workerWaitEndTime = System.currentTimeMillis();
                    metricService.workerWaited(workerWaitEndTime - workerWaitStartTime);
                    metricService.taskTaken();
                    LogService.logger.info(String.format("Worker %s start execute task with id: %s and category '%s'",
                            workerId, nextTask.getId(), category));
                    long executionStart = System.currentTimeMillis();
                    Thread.sleep(random.nextInt(5000)); // имитация процесса выполнения
                    Schedulable taskClass = nextTask.getSchedulableClass().getDeclaredConstructor().newInstance();
                    if (executeTask(taskClass, nextTask.getParams())) {
                        taskService.changeTaskStatus(nextTask.getId(), TaskStatus.COMPLETED, category);
                        LogService.logger.info(String.format("Task with id: %s and category: '%s' has been executed.",
                                nextTask.getId(), category));
                        long executionEnd = System.currentTimeMillis();
                        metricService.taskExecuted(executionEnd - executionStart);
                        metricService.taskCompleted();
                    } else {
                        LogService.logger.info(String.format("Task with id: %s and category: '%s' has been failed.",
                                nextTask.getId(), category));
                        taskService.changeTaskStatus(nextTask.getId(), TaskStatus.RETRYING, category);
                        taskExecutor.executeRetryPolicyForTask(nextTask.getId(), nextTask.getCategory(), nextTask.getRetryCount() + 1);
                    }
                }
            }
            LogService.logger.info(String.format("Worker with id: %s and category: '%s' stopped", workerId, category));
        } catch (Exception e) {
            LogService.logger.severe(e.getMessage());
        }
    }
}