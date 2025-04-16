package org.example.worker;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.service.TaskExecutor;
import org.example.core.service.TaskSchedulerService;
import org.example.core.service.TaskService;
import org.example.core.service.delay.DelayService;
import org.example.test.Schedulable;

import java.util.List;
import java.util.Map;

public class TaskWorker implements Runnable {
    private final String category;
    private final int threadCount;
    private final TaskService taskService;

    private final TaskSchedulerService taskSchedulerService;

    private final DelayService delayService;

    private final TaskExecutor taskExecutor;


    public TaskWorker(String category, int threadCount, TaskService taskService, TaskSchedulerService taskSchedulerService, DelayService delayService) {
        this.category = category;
        this.threadCount = threadCount;
        this.taskService = taskService;
        this.taskSchedulerService = taskSchedulerService;
        this.delayService = delayService;
        this.taskExecutor = new TaskExecutor(taskSchedulerService, taskService, delayService);
    }


    public boolean executeTask(Schedulable task, Map<String, String> params) {
        try {
            task.execute(params);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        System.out.println("Initializing worker with category " + category + ", with " + threadCount + " thread(s) " +
                Thread.currentThread());
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(5000); // периодичность получения задач из БД
                taskService.startTransaction();
                List<ScheduledTask> scheduledTaskList = taskService.getAndLockReadyTasksByType(category);
                for (ScheduledTask task : scheduledTaskList) {
                    //taskService.changeTaskStatus(task.getId(), TASK_STATUS.PROCESSING);
                    Thread.sleep(2000); // имитация процесса выполнения
                    Schedulable taskClass = (Schedulable) Class.forName(task.getCanonicalName()).getDeclaredConstructor().newInstance();
                    //System.out.println("Worker " + this.hashCode() + ", task " + task.getId() + ": ");
                    if (executeTask(taskClass, task.getParams())) {
                        taskService.changeTaskStatus(task.getId(), TASK_STATUS.COMPLETED);
                    } else {
                        taskService.changeTaskStatus(task.getId(), TASK_STATUS.FAILED);
                        taskExecutor.executeRetryPolicyForTask(task.getId());
                    }
                }
                taskService.commitTransaction();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}