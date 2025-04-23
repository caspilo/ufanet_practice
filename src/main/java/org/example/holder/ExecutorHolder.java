package org.example.holder;

import org.example.core.service.task.TaskExecutor;

public class ExecutorHolder {

    private static class TaskExecutorHolderInstance {
        static final TaskExecutor INSTANCE = new TaskExecutor(ServiceHolder.getTaskService(), ServiceHolder.getDelayService());
    }

    public static TaskExecutor getTaskExecutor() {
        return TaskExecutorHolderInstance.INSTANCE;
    }
}