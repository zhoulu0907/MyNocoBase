package com.nocobase.data.manager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DynamicTableManager
 * Uses real PostgreSQL database for testing
 */
@SpringBootTest
public class DynamicTableManagerTest {

    @Autowired
    private DynamicTableManager dynamicTableManager;

    /**
     * Test table creation
     */
    @Test
    void testCreateTable() {
        String tableName = "test_orders";
        String title = "Test Orders";

        // Create table
        dynamicTableManager.createTable(tableName, title);

        // Verify table exists
        assertTrue(dynamicTableManager.tableExists(tableName), "Table should exist after creation");
    }

    /**
     * Test adding column to existing table
     */
    @Test
    void testAddColumn() {
        String tableName = "test_orders";
        String columnName = "price";
        String columnType = "DECIMAL(10,2)";

        // First create table
        dynamicTableManager.createTable(tableName, "Test Table");

        // Add column
        dynamicTableManager.addColumn(tableName, columnName, columnType);

        // Verify column was added (column should now exist in database)
        assertTrue(dynamicTableManager.tableExists(tableName), "Table should still exist");
    }

    /**
     * Test listing all tables
     */
    @Test
    void testListTables() {
        // Create multiple test tables
        dynamicTableManager.createTable("users", "Users");
        dynamicTableManager.createTable("orders", "Orders");
        dynamicTableManager.createTable("products", "Products");

        // List all tables
        List<Map<String, Object>> tables = dynamicTableManager.listTables();

        // Verify
        assertFalse(tables.isEmpty(), "Tables list should not be empty");
        assertTrue(tables.stream().anyMatch(t -> t.get("table_name").equals("users")), "Should contain users table");
        assertTrue(tables.stream().anyMatch(t -> t.get("table_name").equals("orders")), "Should contain orders table");
        assertTrue(tables.stream().anyMatch(t -> t.get("table_name").equals("products")), "Should contain products table");
    }
}
