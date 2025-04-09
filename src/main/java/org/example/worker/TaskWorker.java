package org.example.worker;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class TaskWorker implements Runnable {
    private final String type;
    private final Map<String, String> stringMap;

    public TaskWorker(String type, Map<String, String> stringMap) {
        this.type = type;
        this.stringMap = stringMap;
    }

    @Override
    public void run() {
        System.out.println(getClass().getCanonicalName());
        try {
            Object o = Class.forName(stringMap.get(type)).newInstance();
            o.getClass().getMethod("execute").invoke(o);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }


//        while (!Thread.currentThread().isInterrupted()){
////            List<ScheduledTask> scheduledTaskList = taskService.getPendingTasksByType(type);
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


