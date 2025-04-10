package org.example.worker;


import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class TaskWorker implements Runnable {
    private final String category;
    private final int threadCount;

    public TaskWorker(String category, int threadCount) {
        this.category = category;
        this.threadCount = threadCount;
    }

    public void executeTask(String task, Map<String, String> params) {
        try {
            Object o = Class.forName(task).newInstance();
            o.getClass().getMethod("execute", Map.class).invoke(o, params);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
//
//    public void getCurrentTask(){
//
//    }

    @Override
    public void run() {
        System.out.println("Initializing worker with category " + category + ", with " + threadCount + " thread(s) " +
                Thread.currentThread());
        ;
//        while (!Thread.currentThread().isInterrupted()){
//            List<ScheduledTask> scheduledTaskList = taskService.getPendingTasksByType(category);
//            for(ScheduledTask scheduleTask: scheduledTaskList){
        //    if ()
//            }
//            }
//
//            System.out.println(Thread.currentThread().getClass());

    }

}

//    public void executeTask(Long id) throws Exception {
//        if(1+1==2) {
//            taskService.changeTaskStatus(id, TASK_STATUS.COMPLETED);
//        }else {
//            taskService.changeTaskStatus(id,TASK_STATUS.FAILED);
//            retryTask();
//        }
//    }
//
//    public void retryTask(){
//}
//    }


