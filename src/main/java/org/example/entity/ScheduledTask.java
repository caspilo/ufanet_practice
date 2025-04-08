package org.example.entity;


import lombok.Data;
import org.example.entity.enums.TASK_STATUS;
import org.example.entity.enums.TASK_TYPE;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Data
@Entity
@Table(name="scheduled_tasks")
public class ScheduledTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private TASK_TYPE type;

    @Column(name = "status", nullable = false)
    private TASK_STATUS status = TASK_STATUS.NONE;

    @Column(name = "execution_time", nullable = false)
    private Time executionTime;

    @Column(name="retry_count")
    private int retryCount = 0;

    public ScheduledTask(Long id, TASK_TYPE type, Time executionTime) {
        this.id = id;
        this.type = type;
        this.executionTime = executionTime;
    }

    public ScheduledTask() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TASK_TYPE getType() {
        return type;
    }

    public void setType(TASK_TYPE type) {
        this.type = type;
    }

    public TASK_STATUS getStatus() {
        return status;
    }

    public void setStatus(TASK_STATUS status) {
        this.status = status;
    }

    public Time getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Time executionTime) {
        this.executionTime = executionTime;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
