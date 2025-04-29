package org.example.integrationtest;

import org.example.core.monitoring.MetricRegisterer;
import org.example.core.schedulable.Schedulable;
import org.example.core.service.task.scheduler.Delay;
import org.example.core.service.task.scheduler.TaskScheduler;
import org.example.core.service.task.scheduler.TaskSchedulerService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

public class TaskThreads extends TestThreads {
    private final TaskSchedulerService taskScheduler;
    private final Map<Integer, Class<? extends Schedulable>> classes;
    private final Map<String, String> params;

    public TaskThreads(Map<Integer, Class<? extends Schedulable>> classes,
                       Map<String, String> params, MetricRegisterer metricRegisterer) {
        this.classes = classes;
        this.params = params;
        this.taskScheduler = new TaskScheduler(metricRegisterer);
    }

    @Override
    public Thread[] initThreads(int threadCount, int boundMillisToSleep) {
        Thread[] taskThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            taskThreads[i] = new Thread(() -> {
                while (true) {
                    initRandomTask();
                    sleep(boundMillisToSleep);
                }
            });
            taskThreads[i].setName("Task-" + i);
            taskThreads[i].start();
        }
        return taskThreads;
    }

    private void initRandomTask() {
        Class<? extends Schedulable> randomClass = classes.get(RANDOM.nextInt(classes.size()));
        String executionTime = Timestamp.valueOf(LocalDateTime.now()).toString();
        Delay defaultDelayParams = new Delay.DelayBuilder().build();
        taskScheduler.scheduleTask(randomClass, params, executionTime, defaultDelayParams);
        printTaskInfo(randomClass, executionTime);
    }

    private static void printTaskInfo(Class<? extends Schedulable> randomClass,
                                      String executionTime) {
        String className = randomClass.getName();
        System.out.println("Создан новый Task:" +
                "\nКласс - " + className +
                "\nВремя выполнения - " + executionTime +
                "\nИмя потока - " + Thread.currentThread().getName() +
                "\nДата создания - " + LocalDateTime.now());
    }
}
