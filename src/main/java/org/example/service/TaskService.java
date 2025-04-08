package org.example.service;

import org.example.entity.ScheduledTask;
import org.example.entity.enums.TASK_STATUS;

public interface TaskService {

    ScheduledTask getTask(long id) throws Exception;

    void cancelTask(long id) throws Exception;

    ScheduledTask changeTaskStatus (ScheduledTask scheduledTask, TASK_STATUS taskStatus) throws Exception;

    ScheduledTask createTask (ScheduledTask scheduledTask) throws Exception;
}
