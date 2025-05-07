package org.example.integrationtest.task;

import org.example.integrationtest.TestThreads;

public class TaskThreads extends TestThreads {
    private final TaskManager taskManager;

    public TaskThreads(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected Runnable createInitThread(int boundMillisToSleep) {
        return () -> {
            setupThreadName(Thread.currentThread(), "Task initializer");
            while (true) {
                taskManager.initRandomTask();
                sleep(boundMillisToSleep);
            }
        };
    }

    @Override
    protected Runnable createStoppingThread(int boundMillisToSleep) {
        return () -> {
            setupThreadName(Thread.currentThread(), "Task stopper");
            while (true) {
                taskManager.stopRandomTask();
                sleep(boundMillisToSleep);
            }
        };
    }
}
