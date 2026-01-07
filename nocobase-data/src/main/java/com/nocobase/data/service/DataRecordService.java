package com.nocobase.data.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.nocobase.data.entity.Collection;
import com.nocobase.data.mapper.CollectionMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.nocobase.data.entity.table.CollectionTableDef.COLLECTION;

/**
 * Dynamic Data Record Service
 * Provides CRUD operations for dynamic table data without entity dependencies
 */
@Service
public class DataRecordService {

    private final CollectionMapper collectionMapper;
    private final JdbcTemplate jdbcTemplate;

    public DataRecordService(CollectionMapper collectionMapper, JdbcTemplate jdbcTemplate) {
        this.collectionMapper = collectionMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Get physical table name by collection name
     */
    private String getTableName(String collectionName) {
        Collection collection = collectionMapper.selectOneByCondition(
            COLLECTION.NAME.eq(collectionName)
        );

        if (collection == null) {
            throw new IllegalArgumentException("Collection not found: " + collectionName);
        }

        return collection.getTableName();
    }

    /**
     * Create a new record in specified collection
     *
     * @param collectionName Collection name
     * @param data Data to insert (key-value pairs)
     * @return Created record ID
     */
    public Long createRecord(String collectionName, Map<String, Object> data) {
        String tableName = getTableName(collectionName);

        // Build INSERT SQL dynamically
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        Object[] values = new Object[data.size()];

        int index = 0;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (index > 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(entry.getKey());
            placeholders.append("?");
            values[index] = entry.getValue();
            index++;
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s) RETURNING id",
            tableName, columns, placeholders);

        // Execute and return generated ID
        return jdbcTemplate.queryForObject(sql, Long.class, values);
    }

    /**
     * Query records from specified collection
     *
     * @param collectionName Collection name
     * @param condition Query conditions (optional)
     * @return List of records
     */
    public List<Map<String, Object>> queryRecords(String collectionName, Map<String, Object> condition) {
        String tableName = getTableName(collectionName);

        if (condition == null || condition.isEmpty()) {
            String sql = "SELECT * FROM " + tableName;
            return jdbcTemplate.queryForList(sql);
        }

        // Build WHERE clause from condition map
        StringBuilder whereClause = new StringBuilder();
        Object[] values = new Object[condition.size()];
        int index = 0;

        for (Map.Entry<String, Object> entry : condition.entrySet()) {
            if (index > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(entry.getKey()).append(" = ?");
            values[index] = entry.getValue();
            index++;
        }

        String sql = "SELECT * FROM " + tableName + " WHERE " + whereClause;
        return jdbcTemplate.queryForList(sql, values);
    }

    /**
     * Query all records from specified collection
     *
     * @param collectionName Collection name
     * @return List of all records
     */
    public List<Map<String, Object>> queryAllRecords(String collectionName) {
        String tableName = getTableName(collectionName);
        return jdbcTemplate.queryForList("SELECT * FROM " + tableName);
    }

    /**
     * Query a single record by ID
     *
     * @param collectionName Collection name
     * @param id Record ID
     * @return Record data
     */
    public Map<String, Object> queryById(String collectionName, Long id) {
        String tableName = getTableName(collectionName);
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, id);

        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Update a record by ID
     *
     * @param collectionName Collection name
     * @param id Record ID
     * @param data Data to update
     * @return Number of affected rows
     */
    public int updateRecord(String collectionName, Long id, Map<String, Object> data) {
        String tableName = getTableName(collectionName);

        // Build SET clause
        StringBuilder setClause = new StringBuilder();
        Object[] values = new Object[data.size() + 1];
        int index = 0;

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (index > 0) {
                setClause.append(", ");
            }
            setClause.append(entry.getKey()).append(" = ?");
            values[index] = entry.getValue();
            index++;
        }
        values[index] = id; // ID for WHERE clause

        String sql = String.format("UPDATE %s SET %s WHERE id = ?", tableName, setClause);
        return jdbcTemplate.update(sql, values);
    }

    /**
     * Delete a record by ID
     *
     * @param collectionName Collection name
     * @param id Record ID
     * @return Number of affected rows
     */
    public int deleteRecord(String collectionName, Long id) {
        String tableName = getTableName(collectionName);
        return jdbcTemplate.update("DELETE FROM " + tableName + " WHERE id = ?", id);
    }

    /**
     * Count records in specified collection
     *
     * @param collectionName Collection name
     * @return Number of records
     */
    public long countRecords(String collectionName) {
        String tableName = getTableName(collectionName);
        String sql = "SELECT COUNT(*) FROM " + tableName;
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    /**
     * Check if a record exists by ID
     *
     * @param collectionName Collection name
     * @param id Record ID
     * @return true if exists, false otherwise
     */
    public boolean exists(String collectionName, Long id) {
        String tableName = getTableName(collectionName);
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, id);
        return count != null && count > 0;
    }
}
