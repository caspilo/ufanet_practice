package org.example.core.entity;

import org.example.core.entity.enums.TaskStatus;
import org.example.core.schedulable.DoSomething;
import org.example.core.schedulable.Schedulable;

import java.sql.Timestamp;
import java.util.Map;


public class ScheduledTask {

    private Long id;
    private String category;
    private Class<? extends Schedulable> schedulableClass;
    private Map<String, String> params;
    private TaskStatus status = TaskStatus.NONE;
    private Timestamp executionTime;
    private int retryCount = 0;

    public ScheduledTask(String category, Timestamp executionTime) {
        this.category = category;
        this.executionTime = executionTime;
    }

    public ScheduledTask() {
        this.category = "DoSomething";
        this.schedulableClass = DoSomething.class;
        this.params = Map.of("ID", "123",
                "message", "Test message");
        this.executionTime = new Timestamp(System.currentTimeMillis());
    }


    public String toString() {
        return
                "Task " + this.id +
                        ": category: " + this.category +
                        ", status: " + this.status +
                        ", execution time: " + this.executionTime +
                        ", retry count: " + this.retryCount;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Timestamp getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Timestamp executionTime) {
        this.executionTime = executionTime;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public Class<? extends Schedulable> getSchedulableClass() {
        return schedulableClass;
    }

    public void setSchedulableClass(Class<? extends Schedulable> schedulableClass) {
        this.schedulableClass = schedulableClass;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setSchedulableClass(String schedulableClassName) {
        try {
            setSchedulableClass((Class<? extends Schedulable>) Class.forName(schedulableClassName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
