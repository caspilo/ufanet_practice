package org.example.core.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.config.DataSourceConfig;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TaskStatus;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTaskRepository implements TaskRepository {

    private final DataSource dataSource;
    private final String tableName = "tasks_";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JdbcTaskRepository() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DataSourceConfig.jdbcUrl);
        config.setUsername(DataSourceConfig.username);
        config.setPassword(DataSourceConfig.password);
        dataSource = new HikariDataSource(config);
    }

    public JdbcTaskRepository(final DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public boolean existsById(Long id, String category) {

        return findById(id, category) != null;
    }

    @Override
    public Long save(ScheduledTask task, String category) {

        createTableIfNotExists(category);
        createUpdateEvent(category);

        String sql = "INSERT INTO " + tableName + category +
                " (category, canonical_name, params, status, execution_time) " +
                "VALUES (?,?,?,?,?)";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, task.getCategory());
            stmt.setString(2, task.getCanonicalName());
            stmt.setString(3, objectMapper.writeValueAsString(task.getParams()));
            stmt.setString(4, task.getStatus().name());
            stmt.setTimestamp(5, task.getExecutionTime());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to insert row into " + tableName + category);
            }

            try {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Saving task failed, no ID obtained.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to save task: ", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void createTableIfNotExists(String category) {

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + category + " (\n" +
                "    id BIGINT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    category VARCHAR(50) NOT NULL,\n" +
                "    canonical_name VARCHAR(255) NOT NULL,\n" +
                "    params JSON NOT NULL,\n" +
                "    status ENUM('PENDING','READY','PROCESSING','FAILED','COMPLETED','CANCELED','NONE') NOT NULL DEFAULT 'NONE',\n" +
                "    execution_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    retry_count INT DEFAULT 0\n);";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void createUpdateEvent(String category) {

        String sql1 = "CREATE EVENT IF NOT EXISTS auto_update_" + tableName + category +
                " ON SCHEDULE EVERY 1 MINUTE" +
                " DO" +
                " UPDATE " + tableName + category +
                " SET status = 'READY'" +
                " WHERE execution_time <= NOW()" +
                " AND status IN ('PENDING', 'NONE');";

        String sql2 = "SET GLOBAL event_scheduler = ON";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt1 = connection.prepareStatement(sql1);
            stmt1.executeUpdate();

            PreparedStatement stmt2 = connection.prepareStatement(sql2);
            stmt2.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void cancelTask(Long id, String category) {

        changeTaskStatus(id, TaskStatus.CANCELED, category);
    }


    @Override
    public void changeTaskStatus(Long id, TaskStatus status, String category) {

        String sql = "UPDATE " + tableName + category + " SET status = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, status.name());
            stmt.setLong(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to change task status with ID " + id + " to " + status.name());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void changeTaskStatus(Long id, TaskStatus status, String category, Connection connection) {

        String sql = "UPDATE " + tableName + category + " SET status = ? WHERE id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, status.name());
            stmt.setLong(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to change task status with ID " + id + " to " + status.name());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void increaseRetryCountForTask(Long id, String category) {

        String sql = "UPDATE " + tableName + category + " SET retry_count = retry_count + 1 WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to increase retry count for task with ID: " + id);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private ScheduledTask createTaskFromResult(ResultSet result) {

        ScheduledTask task = new ScheduledTask();

        try {
            task.setId(result.getLong(1));
            task.setCategory(result.getString(2));
            task.setCanonicalName(result.getString(3));
            task.setParams(objectMapper.readValue(result.getString(4), new TypeReference<>() {
            }));
            task.setStatus(TaskStatus.valueOf(result.getString(5)));
            task.setExecutionTime(result.getTimestamp(6));
            task.setRetryCount(result.getInt(7));

            return task;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<ScheduledTask> getAndLockReadyTasksByCategory(String category) {

        List<ScheduledTask> tasks = new ArrayList<>();
        ScheduledTask currentTask;

        String sql = "SELECT * FROM " + tableName + category + " WHERE status = 'READY' LIMIT 5 FOR UPDATE SKIP LOCKED";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            connection.setAutoCommit(false);

            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    currentTask = createTaskFromResult(result);
                    changeTaskStatus(currentTask.getId(), TaskStatus.PROCESSING, category, connection);
                    tasks.add(currentTask);
                }
                connection.commit();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }


    @Override
    public ScheduledTask getAndLockNextTaskByCategory(String category) {

        String sql = "SELECT * FROM " + tableName + category + " WHERE status = 'READY' ORDER BY id LIMIT 1 FOR UPDATE SKIP LOCKED";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    ScheduledTask task = createTaskFromResult(result);
                    changeTaskStatus(task.getId(), TaskStatus.PROCESSING, category, connection);
                    connection.commit();
                    return task;
                }
                else {
                    connection.rollback();
                    return null;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Task locking error. ", e);
        }
    }


    @Override
    public void rescheduleTask(Long id, long delay, String category) {

        String sql = "UPDATE " + tableName + category + " SET execution_time = TIMESTAMPADD(MICROSECOND, ?, execution_time) WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setLong(1, delay * 1000);
            preparedStatement.setLong(2, id);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ScheduledTask findById(Long id, String category) {

        String sql = "SELECT * FROM " + tableName + category + " WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setLong(1, id);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return createTaskFromResult(result);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
