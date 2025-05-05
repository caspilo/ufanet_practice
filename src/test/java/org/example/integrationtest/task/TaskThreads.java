package org.example.integrationtest.task;

import org.example.integrationtest.TestThreads;

public class TaskThreads extends TestThreads {
    private final TaskManager taskManager;

    public TaskThreads(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected Thread createInitThreads(int boundMillisToSleep) {
        Thread thread = new Thread(() -> {
            while (true) {
                taskManager.initRandomTask();
                sleep(boundMillisToSleep);
            }
        });
        setupThreadName(thread, "Task initializer");
        return thread;
    }

    @Override
    protected Thread createStoppingThread(int boundMillisToSleep) {
        Thread thread = new Thread(() -> {
            while (true) {
                taskManager.stopRandomTask();
                sleep(boundMillisToSleep);
            }
        });
        setupThreadName(thread, "Task stopper");
        return thread;
    }
}
