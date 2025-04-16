package org.example.core.service;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.example.core.repository.TaskRepository;

import java.sql.Timestamp;
import java.util.List;

public class TaskServiceDataBase implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceDataBase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public ScheduledTask getTask(long id) throws Exception {
        return taskRepository.findById(id);
    }

    @Override
    public void cancelTask(long id) throws Exception {

    }

    @Override
    public void changeTaskStatus(Long id, TASK_STATUS taskStatus) throws Exception {
        taskRepository.changeTaskStatus(id, taskStatus);
    }

    @Override
    public ScheduledTask createTask(Long id, String type, Timestamp executionTime) throws Exception {
        return null;
    }

    @Override
    public List<ScheduledTask> getReadyTasksByType(String type) {
        return taskRepository.getReadyTasksByCategory(type);
    }


    @Override
    public List<ScheduledTask> getAndLockReadyTasksByType(String type) {
        return taskRepository.getAndLockReadyTasksByCategory(type);
    }


    @Override
    public void startTransaction() {
        taskRepository.startTransaction();
    }


    @Override
    public void commitTransaction() {
        taskRepository.commitTransaction();
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
