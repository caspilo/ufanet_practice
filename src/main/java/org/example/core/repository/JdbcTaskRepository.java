package org.example.core.repository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdbcTaskRepository implements TaskRepository {

    private final DataSource dataSource;
    private final String tableName;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public JdbcTaskRepository(final DataSource dataSource) {
        this.dataSource = dataSource;
        this.tableName = "tasks";
    }

    public JdbcTaskRepository(final DataSource dataSource, final String category) {
        this.dataSource = dataSource;
        this.tableName = "tasks_" + category;
    }


    @Override
    public boolean existsById(Long id) {
        return false;
    }

    // запись задачи в БД
    @Override
    public Long save(ScheduledTask task) {

        String sql = "INSERT INTO " + tableName +
                "(type, canonical_name, params, status, execution_time) " +
                "VALUES (?,?,?,?,?)";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, task.getType());
            stmt.setString(2, task.getCanonicalName());
            stmt.setString(3, objectMapper.writeValueAsString(task.getParams()));
            stmt.setString(4, task.getStatus().name());
            stmt.setTimestamp(5, task.getExecutionTime());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to insert row into " + tableName);
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


    @Override
    public void cancelTask(Long id)  {

        changeTaskStatus(id, TASK_STATUS.CANCELED);
    }

    // заблокировать задачу
    @Override
    public void lockTask(Long id) {

        String sql = "";

    }


    @Override
    public void changeTaskStatus(Long id, TASK_STATUS status) {

        String sql = "UPDATE " + tableName + " SET status = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, status.name());
            stmt.setLong(2,id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to change task status with ID " + id + " to " + status.name());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<ScheduledTask> getReadyTasks() {

        List<ScheduledTask> tasks = new ArrayList<>();

        String sql = "SELECT * FROM " + tableName + " WHERE status = 'READY'";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    tasks.add(createTaskFromResult(result));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tasks;
    }

    private ScheduledTask createTaskFromResult(ResultSet result) throws SQLException, IOException {

        ScheduledTask task = new ScheduledTask();

        task.setId(result.getLong(1));
        task.setType(result.getString(2));
        task.setCanonicalName(result.getString(3));
        task.setParams(objectMapper.readValue(result.getString(4), new TypeReference<>() {}));
        task.setStatus(TASK_STATUS.valueOf(result.getString(5)));
        task.setExecutionTime(result.getTimestamp(6));
        task.setRetryCount(result.getInt(7));

        return task;
    }


    @Override
    public List<ScheduledTask> getReadyTasksByCategory(String category) {
        List<ScheduledTask> tasks = new ArrayList<>();

        String sql = "SELECT * FROM " + tableName + " WHERE status = 'READY' AND type = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, category);

            try (ResultSet result = stmt.executeQuery()) {
                while (result.next()) {
                    tasks.add(createTaskFromResult(result));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tasks;
    }


    @Override
    public void rescheduleTask(Long id, int delay) {

        String sql = "UPDATE tasks SET execution_time = TIMESTAMPADD(MICROSECOND, ?, execution_time) WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, delay * 1000); // миллисекунда - это 1000 микросекунд х_х
            preparedStatement.setLong(2, id);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ScheduledTask findById(Long id) {



        return null;
    }
}
