package org.example.worker;

import org.example.test.Schedualable;

import java.util.Map;

public class TaskWorker implements Runnable {
    private final String category;
    private final int threadCount;

    public TaskWorker(String category, int threadCount) {
        this.category = category;
        this.threadCount = threadCount;
    }

    public void executeTask(String taskName, Map<String, String> params) {
        try {
            Schedualable task = (Schedualable) Class.forName(taskName).getDeclaredConstructor().newInstance();
            task.execute(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//
//    public void getCurrentTask(){
//
//    }

    @Override
    public void run() {
//        System.out.println("Initializing worker with category " + category + ", with " + threadCount + " thread(s) " +
//                Thread.currentThread());
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


