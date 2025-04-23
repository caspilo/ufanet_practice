package org.example.integrationtest;

import org.example.core.service.task.scheduler.TaskScheduler;
import org.example.core.service.task.scheduler.TaskSchedulerService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class TaskThreads extends TestThreads {
    private final TaskSchedulerService taskScheduler = new TaskScheduler();
    private final Map<Integer, String> classes;
    private final Map<String, String> params;

    public TaskThreads(Map<Integer, String> classes, Map<String, String> params) {
        this.classes = classes;
        this.params = params;
    }

    @Override
    public Thread[] initThreads(int threadCount, int boundMillisToSleep) {
        Thread[] taskThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            taskThreads[i] = new Thread(() -> {
                while (true) {
                    initRandomTask();
                    sleep(boundMillisToSleep);
                }
            });
            taskThreads[i].setName("Task-" + i);
            taskThreads[i].start();
        }
        return taskThreads;
    }

    private void initRandomTask() {
        String randomClass = classes.get(RANDOM.nextInt(classes.size()));
        String executionTime = Timestamp.valueOf(LocalDateTime.now()).toString();
        taskScheduler.scheduleTask(randomClass, params, executionTime);
        printTaskInfo(randomClass, executionTime);
    }

    private static void printTaskInfo(String randomClass, String executionTime) {
        System.out.println("Создан новый Task:" +
                "\nКласс - " + randomClass +
                "\nВремя выполнения - " + executionTime +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата создания - " + LocalDateTime.now());
    }
}
