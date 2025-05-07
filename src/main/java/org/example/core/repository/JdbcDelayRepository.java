package org.example.core.repository;

import org.example.core.entity.DelayParams;

import javax.sql.DataSource;
import java.sql.*;

public class JdbcDelayRepository implements DelayRepository {

    private final DataSource dataSource;

    private final String tableName;

    private final String taskTableName;

    public JdbcDelayRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.tableName = "delays_";
        this.taskTableName = "tasks_";
    }

    @Override
    public DelayParams getDelayParams(Long taskId, String category) {

        String sql = "SELECT * FROM " + tableName + category + " WHERE task_id = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setLong(1, taskId);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    DelayParams delayParams = new DelayParams(taskId);
                    delayParams.setWithRetry(result.getBoolean("with_retry"));
                    delayParams.setRetryCount(result.getInt("retry_count"));
                    delayParams.setValueIsFixed(result.getBoolean("value_is_fixed"));
                    delayParams.setFixDelayValue(result.getLong("fix_delay_value"));
                    delayParams.setDelayBase(result.getLong("delay_base"));
                    delayParams.setDelayLimit(result.getLong("delay_limit"));
                    return delayParams;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void save(DelayParams delayParams, String category) {

        createTableIfNotExists(category);

        String sql = "INSERT INTO " + tableName + category + " (task_id, with_retry, retry_count, value_is_fixed, fix_delay_value, delay_base, delay_limit)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, delayParams.getTaskId());
            stmt.setBoolean(2, delayParams.isWithRetry());
            stmt.setInt(3, delayParams.getRetryCount());
            stmt.setBoolean(4, delayParams.isValueIsFixed());
            stmt.setLong(5, delayParams.getFixDelayValue());
            stmt.setLong(6, delayParams.getDelayBase());
            stmt.setLong(7, delayParams.getDelayLimit());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to insert row into table " + tableName + category);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createTableIfNotExists(String category) {

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + category + " (\n" +
                "task_id BIGINT PRIMARY KEY,\n" +
                "    with_retry BOOL NOT NULL,\n" +
                "    retry_count INT,\n" +
                "    value_is_fixed BOOL,\n" +
                "    fix_delay_value BIGINT,\n" +
                "    delay_base BIGINT,\n" +
                "    delay_limit BIGINT,\n" +
                "    FOREIGN KEY (task_id) REFERENCES " + taskTableName + category + " (id) ON DELETE CASCADE);";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}