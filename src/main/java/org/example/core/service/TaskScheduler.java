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

    public Long scheduleTask(Schedulable schedulableClass, Map<String, String> params, String executionTime, double delayBase) {
        return scheduleTask(schedulableClass.getClass(), params, executionTime, delayBase);
    }

    public Long scheduleTask(Class objectClass, Map<String, String> params, String executionTime, double delayBase) {
        return scheduleTask(objectClass.getName(), params, executionTime, delayBase);
    }

    public Long scheduleTask(String className, Map<String, String> params, String executionTime, double delayBase) {
        ScheduledTask task = new ScheduledTask();
        task.setCanonicalName(className);
        task.setParams(params);
        task.setExecutionTime(Timestamp.valueOf(executionTime));
        return taskRepository.save(task);
    }

    public ScheduledTask getTask(long id) {
        return taskRepository.findById(id);
    }

    public void cancelTask(long id) {
        if (!getTask(id).getStatus().equals(TASK_STATUS.COMPLETED)) {
            changeTaskStatus(id, TASK_STATUS.CANCELED);
        } else {
            throw new RuntimeException("ERROR. Can`t cancel this task. Status for task with id: " + id + "is COMPLETED.");
        }
    }

    public void changeTaskStatus(Long id, TASK_STATUS taskStatus) {
        getTask(id).setStatus(taskStatus);
    }


    public List<ScheduledTask> getReadyTasksByCategory(String category) {
        return taskRepository.getReadyTasksByCategory(category);
    }
}
