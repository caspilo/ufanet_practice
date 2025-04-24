package org.example.worker;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
import org.example.core.monitoring.metrics.MetricsCollector;
import org.example.core.schedulable.Schedulable;
import org.example.core.service.task.TaskExecutor;
import org.example.core.service.task.TaskService;
import org.example.holder.ExecutorHolder;
import org.example.holder.ServiceHolder;

import java.util.Map;
import java.util.logging.Level;

public class TaskWorker implements Runnable {
    private final String category;
    private final TaskService taskService;
    private final TaskExecutor taskExecutor;

    public TaskWorker(String category) {
        this.taskService = ServiceHolder.getTaskService();
        this.taskExecutor = ExecutorHolder.getTaskExecutor();
        this.category = category;
    }

    private boolean executeTask(Schedulable task, Map<String, String> params) {
        return task.execute(params);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                long workerWaitStartTime = System.currentTimeMillis();
                Thread.sleep(3000);
                ScheduledTask nextTask = taskService.getAndLockNextTaskByCategory(category);
                if (nextTask != null) {
                    long workerWaitEndTime = System.currentTimeMillis();
                    MetricsCollector.workerWaited(category, workerWaitEndTime - workerWaitStartTime);
                    LogService.logger.info(String.format("Worker %s start execute task with id: %s and category '%s'",
                            Thread.currentThread(), nextTask.getId(), category));
                    long executionStart = System.currentTimeMillis();
                    Thread.sleep(2000); // имитация процесса выполнения
                    Schedulable taskClass = (Schedulable) Class.forName(nextTask.getCanonicalName()).getDeclaredConstructor().newInstance();
                    if (executeTask(taskClass, nextTask.getParams())) {
                        taskService.changeTaskStatus(nextTask.getId(), TaskStatus.COMPLETED, category);
                        LogService.logger.info(String.format("Task with id: %s and category: '%s' has been executed.",
                                nextTask.getId(), category));
                        long executionEnd = System.currentTimeMillis();
                        MetricsCollector.taskExecuted(category, executionEnd - executionStart);
                    } else {
                        LogService.logger.info(String.format("Task with id: %s and category: '%s' has been failed.",
                                nextTask.getId(), category));
                        taskService.changeTaskStatus(nextTask.getId(), TaskStatus.FAILED, category);
                        taskExecutor.executeRetryPolicyForTask(nextTask.getId(), category);
                    }
                }
            }
        } catch (Exception e) {
            LogService.logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}