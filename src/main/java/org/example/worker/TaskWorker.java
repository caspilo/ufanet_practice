package org.example.worker;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
import org.example.core.monitoring.metrics.*;
import org.example.core.schedulable.Schedulable;
import org.example.core.service.task.*;
import org.example.holder.*;

import java.util.*;

public class TaskWorker implements Runnable {
    private final String category;

    private final UUID workerId;
    private final TaskService taskService;
    private final TaskExecutor taskExecutor;

    private boolean doStop = false;

    public TaskWorker(String category, UUID workerId) {
        this.workerId = workerId;
        this.taskService = ServiceHolder.getTaskService();
        this.taskExecutor = ExecutorHolder.getTaskExecutor();
        this.category = category;
    }

    public synchronized void doStop() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return !this.doStop;
    }

    private synchronized boolean executeTask(Schedulable task, Map<String, String> params) {
        return task.execute(params);
    }

    @Override
    public void run() {
        try {
            while (keepRunning()) {
                long workerWaitStartTime = System.currentTimeMillis();
                Thread.sleep(3000);
                LogService.logger.info(String.format("Worker with id: %s and category '%s' searching ready tasks...",
                        workerId, category));
                ScheduledTask nextTask = null;
                try {
                    nextTask = taskService.getAndLockNextTaskByCategory(category);
                } catch (Exception e) {
                    LogService.logger.severe("Table for category: '" + category + "' not found. " + e.getMessage());
                }
                if (nextTask != null) {
                    long workerWaitEndTime = System.currentTimeMillis();
                    WorkerMetrics.workerWaited(category, workerWaitEndTime - workerWaitStartTime);
                    LogService.logger.info(String.format("Worker %s start execute task with id: %s and category '%s'",
                            workerId, nextTask.getId(), category));
                    long executionStart = System.currentTimeMillis();
                    Thread.sleep(2000); // имитация процесса выполнения
                    Schedulable taskClass = (Schedulable) Class.forName(nextTask.getCanonicalName()).getDeclaredConstructor().newInstance();
                    if (executeTask(taskClass, nextTask.getParams())) {
                        taskService.changeTaskStatus(nextTask.getId(), TaskStatus.COMPLETED, category);
                        LogService.logger.info(String.format("Task with id: %s and category: '%s' has been executed.",
                                nextTask.getId(), category));
                        long executionEnd = System.currentTimeMillis();
                        TaskMetrics.taskExecuted(category, executionEnd - executionStart);
                    } else {
                        LogService.logger.info(String.format("Task with id: %s and category: '%s' has been failed.",
                                nextTask.getId(), category));
                        taskService.changeTaskStatus(nextTask.getId(), TaskStatus.RETRYING, category);
                        taskExecutor.executeRetryPolicyForTask(nextTask.getId(), category);
                    }
                }
            }
            LogService.logger.info(String.format("Worker with id: %s and category: '%s' stopped", workerId, category));
        } catch (Exception e) {
            LogService.logger.severe(e.getMessage());
        }
    }
}