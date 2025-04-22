package org.example.worker;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.service.task.TaskExecutor;
import org.example.core.service.task.TaskService;
import org.example.core.service.delay.DelayService;
import org.example.core.schedulable.Schedulable;
import org.example.holder.RepositoryHolder;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class TaskWorker implements Runnable {

    private final String category;

    private final TaskService taskService;

    private final TaskExecutor taskExecutor;


    public TaskWorker(TaskService taskService, DelayService delayService, String category) {
        this.taskService = taskService;
        this.taskExecutor = new TaskExecutor(taskService, delayService);
        this.category = category;
    }


    public boolean executeTask(Schedulable task, Map<String, String> params) {
        return task.execute(params);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(2000);
                ScheduledTask nextTask = taskService.getAndLockNextTaskByCategory(category);
                if (nextTask != null) {
                    Thread.sleep(2000);
                    Schedulable taskClass = (Schedulable) Class.forName(nextTask.getCanonicalName()).getDeclaredConstructor().newInstance();
                    if (executeTask(taskClass, nextTask.getParams())) {
                        taskService.changeTaskStatus(nextTask.getId(), TaskStatus.COMPLETED, category);
                    } else {
                        taskExecutor.executeRetryPolicyForTask(nextTask.getId(), category);
                    }
                }

                /*List<ScheduledTask> scheduledTaskList = taskService.getAndLockReadyTasksByCategory(category);
                for (ScheduledTask task : scheduledTaskList) {
                    Thread.sleep(2000);
                    Schedulable taskClass = (Schedulable) Class.forName(task.getCanonicalName()).getDeclaredConstructor().newInstance();
                    if (executeTask(taskClass, task.getParams())) {
                        taskService.changeTaskStatus(task.getId(), TaskStatus.COMPLETED, category);
                    } else {
                        taskExecutor.executeRetryPolicyForTask(task.getId(), category);
                    }
                }*/
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}