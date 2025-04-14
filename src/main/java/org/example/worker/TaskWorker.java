package org.example.worker;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.service.TaskService;
import org.example.test.Schedulable;

import java.util.List;
import java.util.Map;

public class TaskWorker implements Runnable {
    private final String category;
    private final int threadCount;
    private final TaskService taskService;


    public TaskWorker(String category, int threadCount, TaskService taskService) {
        this.category = category;
        this.threadCount = threadCount;
        this.taskService = taskService;
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
                List<ScheduledTask> scheduledTaskList = taskService.getReadyTasksByType(category);
                for (ScheduledTask task : scheduledTaskList) {
                    taskService.changeTaskStatus(task.getId(), TASK_STATUS.PROCESSING);
                    Thread.sleep(2000); // имитация процесса выполнения
                    Schedulable taskClass = (Schedulable) Class.forName(task.getCanonicalName()).getDeclaredConstructor().newInstance();
                    if (executeTask(taskClass, task.getParams())) {
                        taskService.changeTaskStatus(task.getId(), TASK_STATUS.COMPLETED);
                    } else {
                        taskService.changeTaskStatus(task.getId(), TASK_STATUS.FAILED);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

//    public void executeTask(Long id) throws Exception {
//        if(1+1==2) {
//            taskService.changeTaskStatus(id, TASK_STATUS.COMPLETED);
//        }else {
//            taskService.changeTaskStatus(id,TASK_STATUS.FAILED);
//            retryTask();
//        }
//    }
//
//    public void retryTask(){
//}
//    }


