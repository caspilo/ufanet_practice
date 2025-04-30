package org.example.integrationtest.task;

import org.example.core.monitoring.MetricRegisterer;
import org.example.core.schedulable.Schedulable;
import org.example.core.service.task.scheduler.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class TaskManager {
    private static final Random RANDOM = new Random();

    private final TaskSchedulerService taskScheduler;
    private final Map<Integer, Class<? extends Schedulable>> classes;
    private final Map<String, String> params;
    private final Map<String, List<Long>> tasksId = new ConcurrentHashMap<>();

    public TaskManager(Map<Integer, Class<? extends Schedulable>> classes,
                       Map<String, String> params,
                       MetricRegisterer metricRegisterer) {
        this.taskScheduler = new TaskScheduler(metricRegisterer);
        this.classes = classes;
        this.params = params;
    }

    public void initRandomTask() {
        Class<? extends Schedulable> randomClass = classes.get(RANDOM.nextInt(classes.size()));
        String executionTime = Timestamp.valueOf(LocalDateTime.now()).toString();
        Delay defaultDelayParams = new Delay.DelayBuilder().build();
        Long taskId = scheduleTask(randomClass, executionTime, defaultDelayParams);
        putInTasksId(randomClass, taskId);
        printScheduledTaskInfo(randomClass.getSimpleName(), executionTime);
    }

    private Long scheduleTask(Class<? extends Schedulable> randomClass, String executionTime, Delay defaultDelayParams) {
        return taskScheduler.scheduleTask(randomClass, params, executionTime, defaultDelayParams)
                .orElseThrow(() -> new RuntimeException("Could not schedule task with category: " +
                        randomClass.getSimpleName() + " and delay params: " +
                        defaultDelayParams.toString()));
    }

    private void putInTasksId(Class<? extends Schedulable> randomClass, Long taskId) {
        if (tasksId.containsKey(randomClass.getSimpleName())) {
            tasksId.get(randomClass.getSimpleName()).add(taskId);
        } else {
            List<Long> taskIds = new CopyOnWriteArrayList<>();
            taskIds.add(taskId);
            tasksId.put(randomClass.getSimpleName(), taskIds);
        }
    }

    private void printScheduledTaskInfo(String className, String executionTime) {
        System.out.println("Создан новый Task:" +
                "\nКласс - " + className +
                "\nВремя выполнения - " + executionTime +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата создания - " + LocalDateTime.now());
    }

    public void stopRandomTask() {
        String randomCategory = classes.get(RANDOM.nextInt(classes.size())).getSimpleName();
        List<Long> tasksIdByCategory = tasksId.get(randomCategory);
        if (tasksIdByCategory != null) {
            Long randomTaskId = tasksIdByCategory.get(RANDOM.nextInt(tasksIdByCategory.size()));
            taskScheduler.cancelTask(randomTaskId, randomCategory);
            printStoppedTaskInfo(randomCategory, randomTaskId);
        }
    }

    private void printStoppedTaskInfo(String className, Long taskId) {
        System.out.println("Отменён task:" +
                "\nTask id: " + taskId +
                "\nКласс - " + className +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата отмены - " + LocalDateTime.now());
    }
}
