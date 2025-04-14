package org.example.core.entity;

import org.example.core.entity.enums.TASK_STATUS;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


public class ScheduledTask {

    private Long id;
    private String type;
    private String canonicalName;
    private Map<String, String> params;
    private TASK_STATUS status = TASK_STATUS.NONE;
    private Timestamp executionTime;
    private int retryCount = 0;

    public ScheduledTask(String type, Timestamp executionTime) {
        this.type = type;
        this.executionTime = executionTime;
    }

    public ScheduledTask() {
        this.type = "DoSomething";
        this.canonicalName = "org.example.test.DoSomething";
        this.params = new HashMap<>(Map.of("ID", "123",
                                            "message", "Test message"));
        this.executionTime = new Timestamp(System.currentTimeMillis());
    }


    public String toString(){
        return
                "Task " + this.id +
                        ": type: " + this.type +
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TASK_STATUS getStatus() {
        return status;
    }

    public void setStatus(TASK_STATUS status) {
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

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
