package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.repository.TaskRepository;
import org.example.test.Schedulable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class TaskScheduler implements TaskService {

    private final TaskRepository taskRepository;

    public TaskScheduler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    @Override
    public Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime, double delayBase) {
        return scheduleTask(schedulableClass.getClass(), params, executionTime, delayBase);
    }


    @Override
    public Long scheduleTask(Class objectClass, Map<String, String> params, String executionTime, double delayBase) {
        return scheduleTask(objectClass.getName(), params, executionTime, delayBase);
    }


    @Override
    public Long scheduleTask(String className, Map<String, String> params, String executionTime, double delayBase) {
        ScheduledTask task = new ScheduledTask();
        task.setCanonicalName(className);
        task.setParams(params);
        task.setExecutionTime(Timestamp.valueOf(executionTime));
        return taskRepository.save(task);
    }


    @Override
    public void cancelTask(long id) {

        ScheduledTask task = taskRepository.findById(id);

        if (task != null) {
            if (task.getStatus() == TASK_STATUS.PENDING) {
                taskRepository.changeTaskStatus(id, TASK_STATUS.CANCELED);
            }
            else {
                throw new RuntimeException("Cannot cancel task with id " + id + ": task status is " + task.getStatus().name());
            }
        } else {
            throw new RuntimeException("Cannot cancel task with id " + id + ": task not found");
        }
    }
}
