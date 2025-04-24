package org.example.worker;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.logging.LogService;
import org.example.core.schedulable.Schedulable;
import org.example.core.service.task.TaskExecutor;
import org.example.core.service.task.TaskService;
import org.example.holder.ExecutorHolder;
import org.example.holder.ServiceHolder;

import java.util.Map;
import java.util.UUID;

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
                Thread.sleep(3000);
                LogService.logger.info(String.format("Worker with id: %s and category '%s' searching ready tasks...",
                        workerId, category));
                ScheduledTask nextTask = taskService.getAndLockNextTaskByCategory(category);
                if (nextTask != null) {
                    LogService.logger.info(String.format("Worker %s start execute task with id: %s and category '%s'",
                            workerId, nextTask.getId(), category));
                    Thread.sleep(2000); // имитация процесса выполнения
                    Schedulable taskClass = (Schedulable) Class.forName(nextTask.getCanonicalName()).getDeclaredConstructor().newInstance();
                    if (executeTask(taskClass, nextTask.getParams())) {
                        taskService.changeTaskStatus(nextTask.getId(), TaskStatus.COMPLETED, category);
                        LogService.logger.info(String.format("Task with id: %s and category: '%s' has been executed.",
                                nextTask.getId(), category));
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