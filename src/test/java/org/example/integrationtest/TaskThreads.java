package org.example.integrationtest;

import org.example.core.service.task.TaskScheduler;
import org.example.core.service.task.TaskSchedulerService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

public class TaskThreads {
    private static final Random RANDOM = new Random();

    private TaskSchedulerService taskScheduler = new TaskScheduler();
    private Map<Integer, String> classes;
    private Map<String, String> params;

    public TaskThreads(Map<Integer, String> classes, Map<String, String> params) {
        this.classes = classes;
        this.params = params;
    }

    public void initTaskThreads(int taskThreadCount, int boundMillisToSleep) {
        Thread[] taskThreads = new Thread[taskThreadCount];
        for (int i = 0; i < taskThreadCount; i++) {
            taskThreads[i] = new Thread(() -> {
                while (true) {
                    initRandomTask();
                    sleep(boundMillisToSleep);
                }
            });
            taskThreads[i].setName("Task-" + i);
            taskThreads[i].start();
        }
    }

    // TODO: убрать дублирование кода
    private static void sleep(int boundMillisToSleep) {
        try {
            Thread.sleep(RANDOM.nextInt(boundMillisToSleep));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
