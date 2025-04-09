package org.example.core;

import org.example.core.service.TaskScheduler;
import org.example.worker.TaskWorkerPool;

public class ScheduledTaskSystem implements Runnable{

    private final TaskScheduler taskScheduler;
    private final TaskWorkerPool taskWorkerPool;

    public ScheduledTaskSystem(TaskScheduler taskScheduler, TaskWorkerPool taskWorkerPool) {
        this.taskScheduler = taskScheduler;
        this.taskWorkerPool = taskWorkerPool;
    }

    @Override
    public void run() {

    }
}
