package org.example.core.holder;

import org.example.core.repository.DelayRepository;
import org.example.core.repository.JdbcDelayRepository;
import org.example.core.repository.JdbcTaskRepository;
import org.example.core.repository.TaskRepository;

import javax.sql.DataSource;

public class RepositoryHolder {
    private static volatile DataSource dataSource;

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void init(DataSource ds) {
        if (dataSource != null) {
            throw new IllegalStateException("DataSource has already been initialized");
        }

        dataSource = ds;
    }

    private static class TaskRepositoryHolderInstance {
        static final TaskRepository INSTANCE = new JdbcTaskRepository(dataSource);
    }

    public static TaskRepository getTaskInstance() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource has not been initialized");
        }
        return TaskRepositoryHolderInstance.INSTANCE;
    }

    private static class DelayRepositoryHolderInstance {
        static final DelayRepository INSTANCE = new JdbcDelayRepository(dataSource);
    }

    public static DelayRepository getDelayInstance() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource has not been initialized");
        }
        return DelayRepositoryHolderInstance.INSTANCE;
    }
}
