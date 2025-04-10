package org.example.core.entity;

import lombok.Data;
import org.example.core.entity.enums.TASK_STATUS;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name="tasks")
public class ScheduledTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "canonical_name", nullable = false)
    private String canonicalName;

    @Column(name = "params", nullable = false)
    private String params;

    @Column(name = "status", nullable = false)
    private TASK_STATUS status = TASK_STATUS.NONE;

    @Column(name = "execution_time", nullable = false)
    private Timestamp executionTime;

    @Column(name="retry_count")
    private int retryCount = 0;

    public ScheduledTask(Long id, String type, Timestamp executionTime) {
        this.id = id;
        this.type = type;
        this.executionTime = executionTime;
    }

    public ScheduledTask() {
        this.id = 1000L + this.hashCode();
        this.type = "default";
        this.canonicalName = "default";
        this.params = "{\"param1\": \"default\", \"param2\" : \"default\"}";
        this.status = TASK_STATUS.NONE;
        this.executionTime = new Timestamp(System.currentTimeMillis());
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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
