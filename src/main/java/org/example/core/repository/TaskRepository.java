package org.example.core.repository;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<ScheduledTask, Long> {
    @Query(value = "select * from scheduled_tasks where id = :id", nativeQuery = true)
    Optional<ScheduledTask> findById(Long id);

    @Query(value = "select * ", nativeQuery = true)
    Optional<ScheduledTask> changeTaskStatus(Long id,TASK_STATUS status);

    @Query("select * from pending_tasks where type = :type")
    Optional<List<ScheduledTask>> getPendingTasksForType(String type);
}
