package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.repository.TaskRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class TaskServiceDataBase implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceDataBase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public ScheduledTask getTask(long id) throws Exception {
        return null;
    }

    @Override
    public void cancelTask(long id) throws Exception {

    }

    @Override
    public ScheduledTask changeTaskStatus(Long id, TASK_STATUS taskStatus) throws Exception {
        return null;
    }

    @Override
    public ScheduledTask createTask(Long id, String type, Timestamp executionTime) throws Exception {
        return null;
    }

    @Override
    public List<ScheduledTask> getPendingTasksByType(String type) {
        return List.of();
    }

//    @Override
//    public ScheduledTask getTask(long id) throws Exception {
//        return taskRepository.findById(id).orElse(new ScheduledTask());
//    }
//
//    @Override
//    public void cancelTask(long id) throws Exception {
//        Optional <ScheduledTask> scheduledTask = taskRepository.findById(id);
//        if(scheduledTask.isEmpty()){
//            throw new RuntimeException("");
//        }
//        scheduledTask.get().setStatus(TASK_STATUS.CANCELED);
//    }
//
//    @Override
//    public ScheduledTask changeTaskStatus(ScheduledTask scheduledTask, TASK_STATUS taskStatus) throws Exception {
//        scheduledTask.setStatus(taskStatus);
//        return taskRepository.saveAndFlush(scheduledTask);
//    }
//
//    @Override
//    public ScheduledTask createTask(ScheduledTask scheduledTask) throws Exception {
//        return changeTaskStatus(scheduledTask, TASK_STATUS.PENDING);
//    }
}
