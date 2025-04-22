package org.example.holder;

import org.example.core.service.delay.DelayPolicy;
import org.example.core.service.delay.DelayService;
import org.example.core.service.task.DatabaseTaskActions;
import org.example.core.service.task.TaskService;

public class ServiceHolder {

    private static class TaskServiceHolderInstance {
        static final TaskService INSTANCE = new DatabaseTaskActions(RepositoryHolder.getTaskInstance());
    }

    public static TaskService getTaskService() {
        return TaskServiceHolderInstance.INSTANCE;
    }

    private static class DelayServiceHolderInstance {
        static final DelayService INSTANCE = new DelayPolicy(RepositoryHolder.getDelayInstance());
    }

    public static DelayService getDelayService() {
        return DelayServiceHolderInstance.INSTANCE;
    }
}
