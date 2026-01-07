package com.nocobase.data.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Dynamic Table Manager
 * Handles dynamic table creation and field management
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicTableManager {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Create a new collection table
     */
    public void createTable(String tableName, String title) {
        String sql = String.format(
            "CREATE TABLE %s (" +
            "id BIGSERIAL PRIMARY KEY, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "created_by_id BIGINT, " +
            "updated_by_id BIGINT" +
            ")",
            tableName
        );
        jdbcTemplate.execute(sql);
        log.info("Created table: {}", tableName);
    }

    /**
     * Add a column to existing table
     */
    public void addColumn(String tableName, String columnName, String columnType) {
        String sql = String.format(
            "ALTER TABLE %s ADD COLUMN %s %s",
            tableName, columnName, columnType
        );
        jdbcTemplate.execute(sql);
        log.info("Added column {} to table {}", columnName, tableName);
    }

    /**
     * Check if table exists
     */
    public boolean tableExists(String tableName) {
        String sql = "SELECT EXISTS (" +
                     "SELECT table_name " +
                     "FROM information_schema.tables " +
                     "WHERE table_schema = 'public' " +
                     "AND table_name = ?)";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
        return result != null && result > 0;
    }

    /**
     * List all tables
     */
    public java.util.List<java.util.Map<String, Object>> listTables() {
        String sql = "SELECT table_name, table_type FROM information_schema.tables WHERE table_schema = 'public'";
        return jdbcTemplate.queryForList(sql);
    }
}
