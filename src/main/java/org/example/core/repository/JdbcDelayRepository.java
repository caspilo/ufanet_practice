package org.example.core.repository;

import org.example.core.entity.DelayParams;

import javax.sql.DataSource;
import java.sql.*;

public class JdbcDelayRepository implements DelayRepository {

    private final DataSource dataSource;

    private String tableName = "delays";

    public JdbcDelayRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        createTableIfNotExists();
    }

    public JdbcDelayRepository(DataSource dataSource, String category) {
        this.dataSource = dataSource;
        this.tableName = "delays_" + category;
        createTableIfNotExists();
    }

    @Override
    public DelayParams getDelayParams(Long taskId) {

        String sql = "SELECT * FROM delays WHERE task_id = ?";

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
    public void save(DelayParams delayParams) {

        String sql = "INSERT INTO " + tableName + " (task_id, with_retry, retry_count, is_fixed, fix_delay_value, delay_base, delay_limit)" +
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
                throw new SQLException("Failed to insert row into table " + tableName);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createTableIfNotExists() {

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n" +
                "task_id BIGINT PRIMARY KEY,\n" +
                "    with_retry BOOL NOT NULL,\n" +
                "    retry_count INT,\n" +
                "    is_fixed BOOL,\n" +
                "    delay_value BIGINT,\n" +
                "    delay_base BIGINT,\n" +
                "    delay_limit BIGINT,\n" +
                "    FOREIGN KEY (task_id) REFERENCES tasks (id));";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
