package org.example.integrationtest.task;

import org.example.integrationtest.TestThreads;

public class TaskThreads extends TestThreads {
    private final TaskManager taskManager;

    public TaskThreads(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public Thread[] initThreads(int threadCount, int boundMillisToSleep) {
        Thread[] taskThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            taskThreads[i] = new Thread(() -> {
                while (true) {
                    taskManager.initRandomTask();
                    sleep(boundMillisToSleep);
                }
            });
            taskThreads[i].setName("Task initializer-" + i);
            taskThreads[i].start();
        }
        return taskThreads;
    }

    @Override
    public Thread[] stoppingThreads(int threadCount, int boundMillisToSleep) {
        Thread[] taskThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            taskThreads[i] = new Thread(() -> {
                while (true) {
                    taskManager.stopRandomTask();
                    sleep(boundMillisToSleep);
                }
            });
            taskThreads[i].setName("Task-" + i);
            taskThreads[i].start();
        }
        return taskThreads;

    }
}
