package org.example.worker;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;
import org.example.core.service.TaskExecutor;
import org.example.core.service.TaskService;
import org.example.core.service.delay.DelayService;
import org.example.core.task.Schedulable;

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
                Thread.sleep(5000);
                taskService.startTransaction();
                List<ScheduledTask> scheduledTaskList = taskService.getAndLockReadyTasksByCategory(category);
                for (ScheduledTask task : scheduledTaskList) {
                    Thread.sleep(2000);
                    Schedulable taskClass = (Schedulable) Class.forName(task.getCanonicalName()).getDeclaredConstructor().newInstance();
                    if (executeTask(taskClass, task.getParams())) {
                        taskService.changeTaskStatus(task.getId(), TaskStatus.COMPLETED, category);
                    } else {
                        taskExecutor.executeRetryPolicyForTask(task.getId(), category);
                    }
                }
                taskService.commitTransaction();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}