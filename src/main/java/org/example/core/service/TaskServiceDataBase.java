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
        return taskRepository.findById(id).orElse(new ScheduledTask());
    }

    @Override
    public void cancelTask(long id) throws Exception {
        Optional <ScheduledTask> scheduledTask = taskRepository.findById(id);
        if(scheduledTask.isEmpty()){
            throw new RuntimeException("");
        }
        scheduledTask.get().setStatus(TASK_STATUS.CANCELED);
    }

    @Override
    public ScheduledTask changeTaskStatus(Long id, TASK_STATUS taskStatus) throws Exception {

        Optional <ScheduledTask> scheduledTask = taskRepository.findById(id);
        if(scheduledTask.isEmpty()){
            throw new RuntimeException("");
        }
        scheduledTask.get().setStatus(taskStatus);
        return taskRepository.saveAndFlush(scheduledTask.get());
    }

    @Override
    public ScheduledTask createTask(Long id, String type, Timestamp executionTime) throws Exception {
        taskRepository.saveAndFlush(new ScheduledTask(id,type,executionTime));
        return changeTaskStatus(id, TASK_STATUS.PENDING);
    }

    @Override
    public List <ScheduledTask> getPendingTasksByType(String type){
        Optional<List<ScheduledTask>> scheduledTasks = taskRepository.getPendingTasksForType(type);
        if(scheduledTasks.isEmpty()){
            throw new RuntimeException("");
        }
        return scheduledTasks.get();
    }
}
