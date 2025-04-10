package org.example.core.repository;

import org.example.core.entity.ScheduledTask;
import org.example.core.entity.enums.TASK_STATUS;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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


    @Override
    public void save(ScheduledTask task) {

        String sql = "INSERT INTO " + tableName + " VALUES (?,?,?,?,?,?,?)";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setLong(1, task.getId());
            stmt.setString(2, task.getType());
            stmt.setString(3, task.getCanonicalName());
            stmt.setString(4, task.getParams());
            stmt.setString(5, task.getStatus().name());
            stmt.setTimestamp(6, task.getExecutionTime());
            stmt.setInt(7, task.getRetryCount());

            stmt.executeUpdate();
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
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setLong(1,id);
            preparedStatement.setString(2, status.name());

            preparedStatement.executeUpdate();
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
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                tasks.add(createTaskFromResult(result));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tasks;
    }

    private ScheduledTask createTaskFromResult(ResultSet result) throws SQLException {

        ScheduledTask task = new ScheduledTask();
        task.setId(result.getLong(1));
        task.setType(result.getString(2));
        task.setCanonicalName(result.getString(3));
        task.setParams(result.getString(4));
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
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                tasks.add(createTaskFromResult(result));
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

            preparedStatement.setInt(1,delay);
            preparedStatement.setLong(2, id);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // проверить существование задачи
    @Override
    public ScheduledTask findById(Long id) {



        return null;
    }
}
