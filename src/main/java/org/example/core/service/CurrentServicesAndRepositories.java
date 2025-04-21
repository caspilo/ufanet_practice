package org.example.core.service;

import org.example.core.repository.DelayRepository;
import org.example.core.repository.TaskRepository;
import org.example.core.service.delay.DelayService;

public class CurrentServicesAndRepositories {

    private static TaskService taskService;

    private static DelayService delayService;

    private static TaskRepository taskRepository;

    private static DelayRepository delayRepository;

    public static TaskService getTaskService() {
        return taskService;
    }

    public static void setTaskService(TaskService taskService_) {
        if (taskService != null) {
            return;
        }
        taskService = taskService_;
    }

    public static DelayService getDelayService() {
        return delayService;
    }

    public static void setDelayService(DelayService delayService_) {
        if (delayService != null) {
            return;
        }
        delayService = delayService_;
    }

    public static TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public static void setTaskRepository(TaskRepository taskRepository_) {
        if (taskRepository != null) {
            return;
        }
        taskRepository = taskRepository_;
    }

    public static DelayRepository getDelayRepository() {
        return delayRepository;
    }

    public static void setDelayRepository(DelayRepository delayRepository_) {
        if (delayRepository != null) {
            return;
        }
        CurrentServicesAndRepositories.delayRepository = delayRepository_;
    }

    public static void init(TaskService taskService_, DelayService delayService_, TaskRepository taskRepository_, DelayRepository delayRepository_) {
        setTaskService(taskService_);
        setDelayService(delayService_);
        setTaskRepository(taskRepository_);
        setDelayRepository(delayRepository_);
    }
}
