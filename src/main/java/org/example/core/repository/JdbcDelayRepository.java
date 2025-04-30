package org.example.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.core.entity.DelayParams;
import org.example.retry_policy.RetryPolicy;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;

public class JdbcDelayRepository implements DelayRepository {

    private final DataSource dataSource;

    private final String tableName;

    private final String taskTableName;

    private final ObjectMapper objectMapper = new ObjectMapper();


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
                    delayParams.setFixDelayValue(result.getLong("fix_delay_value"));
                    delayParams.setRetryPolicyClass((Class<? extends RetryPolicy>) Class.forName(result.getString("retry_policy_canonical_name")));
                    delayParams.setRetryParams(objectMapper.readValue(result.getString("retry_params"), Map.class));
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

        String sql = "INSERT INTO " + tableName + category + " (task_id, with_retry, retry_count, fix_delay_value, " +
                "retry_policy_canonical_name, retry_params) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, delayParams.getTaskId());
            stmt.setBoolean(2, delayParams.isWithRetry());
            stmt.setInt(3, delayParams.getRetryCount());
            stmt.setLong(4, delayParams.getFixDelayValue());
            stmt.setString(5, delayParams.getRetryPolicyClass().getName());
            stmt.setString(6, objectMapper.writeValueAsString(delayParams.getRetryParams()));

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
                "    fix_delay_value BIGINT,\n" +
                "    retry_policy_canonical_name VARCHAR(255) NOT NULL,\n" +
                "    retry_params JSON NOT NULL,\n" +
                "    FOREIGN KEY (task_id) REFERENCES " + taskTableName + category + " (id) ON DELETE CASCADE);";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}