package org.example.core.repository;

import org.example.core.entity.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<ScheduledTask, Long> {
    @Query(value = "select * from scheduled_tasks where id = :id", nativeQuery = true)
    Optional<ScheduledTask> findById(Long id);
}
