package org.example.core.repository;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;

import javax.sql.DataSource;
import java.util.List;

public class JdbcTaskRepository implements TaskRepository {

    private final DataSource dataSource;
    private final String tableName;

    public JdbcTaskRepository(final DataSource dataSource) {
        this.dataSource = dataSource;
        this.tableName = "tasks";
    }

    public JdbcTaskRepository(final DataSource dataSource, final String category) {
        this.dataSource = dataSource;
        this.tableName = "tasks_" + category;
    }

    // сохранить задачу в БД
    @Override
    public String save(ScheduledTask task) {
        return "";
    }

    // отменить задачу
    @Override
    public void cancelTask(Long id) {

    }

    // заблокировать задачу
    @Override
    public void lockTask(Long id) {

    }

    // поменять статус задачи
    @Override
    public void changeTaskStatus(Long id, TASK_STATUS status) {

    }

    // найти задачи, готовые к выполнению
    @Override
    public List<ScheduledTask> getReadyTasks() {
        return List.of();
    }

    // найти задачи, готовые к выполнению, конкретной категории
    @Override
    public List<ScheduledTask> getReadyTasksByCategory(String category) {
        return List.of();
    }

    // перенести время задачи
    @Override
    public void rescheduleTask(Long id, int delay) {

    }

    // проверить существование задачи
    @Override
    public ScheduledTask findById(Long id) {
        return null;
    }
}
