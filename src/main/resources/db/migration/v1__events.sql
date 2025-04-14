#DROP EVENT auto_update_task_status;

CREATE EVENT auto_update_task_status
    ON SCHEDULE EVERY 1 MINUTE
    DO
    UPDATE tasks
    SET status = 'READY'
    WHERE execution_time <= NOW()
      AND status = 'PENDING';

SET GLOBAL event_scheduler = ON;