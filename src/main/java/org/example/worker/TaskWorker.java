package org.example.worker;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.service.TaskExecutor;
import org.example.core.service.TaskService;
import org.example.core.service.delay.DelayService;
import org.example.core.task.Schedulable;

import java.util.List;
import java.util.Map;

public class TaskWorker implements Runnable {

    private final TaskService taskService;

    private final TaskExecutor taskExecutor;


    public TaskWorker(TaskService taskService, DelayService delayService) {
        this.taskService = taskService;
        this.taskExecutor = new TaskExecutor(taskService, delayService);
    }


    public boolean executeTask(Schedulable task, Map<String, String> params) {
        return task.execute(params);
    }


    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(5000); // периодичность получения задач из БД
                taskService.startTransaction();
                List<ScheduledTask> scheduledTaskList = taskService.getAndLockReadyTasks();
                for (ScheduledTask task : scheduledTaskList) {
//                    taskService.changeTaskStatus(task.getId(), TASK_STATUS.PROCESSING);
                    Thread.sleep(2000); // имитация процесса выполнения
                    Schedulable taskClass = (Schedulable) Class.forName(task.getCanonicalName()).getDeclaredConstructor().newInstance();
                    //System.out.println("Worker " + this.hashCode() + ", task " + task.getId() + ": ");
                    if (executeTask(taskClass, task.getParams())) {
                        taskService.changeTaskStatus(task.getId(), TASK_STATUS.COMPLETED);
                    } else {
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