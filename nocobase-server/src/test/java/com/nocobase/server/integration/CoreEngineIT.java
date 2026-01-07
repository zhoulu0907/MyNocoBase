package com.nocobase.server.integration;

import com.nocobase.data.entity.Collection;
import com.nocobase.data.mapper.CollectionMapper;
import com.nocobase.data.manager.DynamicTableManager;
import com.nocobase.data.service.DataRecordService;
import com.nocobase.server.NocoBaseApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Core Engine Integration Test
 * Tests the complete workflow of dynamic table creation, field addition, and CRUD operations
 */
@SpringBootTest(
    classes = NocoBaseApplication.class
)
@Testcontainers
@DisplayName("Core Engine Integration Test")
class CoreEngineIT {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("nocobase_test")
            .withUsername("nocobase")
            .withPassword("nocobase");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private DynamicTableManager dynamicTableManager;

    @Autowired
    private DataRecordService dataRecordService;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TEST_TABLE_NAME = "t_product";
    private static final String TEST_COLLECTION_NAME = "products";
    private static final String TEST_TITLE = "Test Products";

    @BeforeEach
    void setUp() {
        // Create collection metadata
        Collection collection = new Collection();
        collection.setName(TEST_COLLECTION_NAME);
        collection.setTitle(TEST_TITLE);
        collection.setTableName(TEST_TABLE_NAME);
        collection.setType("table");
        collection.setSystem(false);
        collection.setHidden(false);

        collectionMapper.insert(collection);

        // Create dynamic table
        dynamicTableManager.createTable(TEST_TABLE_NAME, TEST_TITLE);

        // Add fields
        dynamicTableManager.addColumn(TEST_TABLE_NAME, "name", "VARCHAR(255)");
        dynamicTableManager.addColumn(TEST_TABLE_NAME, "price", "INTEGER");

        // Verify table and columns exist
        assertTrue(dynamicTableManager.tableExists(TEST_TABLE_NAME),
            "Table " + TEST_TABLE_NAME + " should exist");
    }

    @AfterEach
    void tearDown() {
        // Clean up: drop the test table
        if (dynamicTableManager.tableExists(TEST_TABLE_NAME)) {
            jdbcTemplate.execute("DROP TABLE IF EXISTS " + TEST_TABLE_NAME);
        }

        // Clean up: delete collection metadata
        jdbcTemplate.execute("DELETE FROM nocobase_collections WHERE name = '" + TEST_COLLECTION_NAME + "'");
    }

    @Test
    @DisplayName("Should create table and add fields successfully")
    void testTableAndFieldCreation() {
        // Verify table exists
        assertTrue(dynamicTableManager.tableExists(TEST_TABLE_NAME),
            "Table should exist after creation");

        // Verify columns exist
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
            "SELECT column_name, data_type FROM information_schema.columns " +
            "WHERE table_name = '" + TEST_TABLE_NAME + "' " +
            "AND column_name IN ('name', 'price') " +
            "ORDER BY column_name"
        );

        assertFalse(columns.isEmpty(), "Columns should exist");
        assertEquals(2, columns.size(), "Should have 2 custom columns (name, price)");

        // Verify column types
        Map<String, Object> nameColumn = columns.get(0);
        assertEquals("name", nameColumn.get("column_name"));

        Map<String, Object> priceColumn = columns.get(1);
        assertEquals("price", priceColumn.get("column_name"));
    }

    @Test
    @DisplayName("Should insert and query data using DataRecordService")
    void testDataInsertAndQuery() {
        // Insert test data
        Map<String, Object> product1 = Map.of(
            "name", "Laptop",
            "price", 999
        );
        Map<String, Object> product2 = Map.of(
            "name", "Mouse",
            "price", 15
        );
        Map<String, Object> product3 = Map.of(
            "name", "Keyboard",
            "price", 75
        );

        Long id1 = dataRecordService.createRecord(TEST_COLLECTION_NAME, product1);
        Long id2 = dataRecordService.createRecord(TEST_COLLECTION_NAME, product2);
        Long id3 = dataRecordService.createRecord(TEST_COLLECTION_NAME, product3);

        assertNotNull(id1, "First insert should return ID");
        assertNotNull(id2, "Second insert should return ID");
        assertNotNull(id3, "Third insert should return ID");

        // Query all records
        List<Map<String, Object>> allRecords = dataRecordService.queryAllRecords(TEST_COLLECTION_NAME);
        assertEquals(3, allRecords.size(), "Should have 3 records");

        // Query records with price > 10 using JdbcTemplate directly
        List<Map<String, Object>> expensiveProducts = jdbcTemplate.queryForList(
            "SELECT * FROM " + TEST_TABLE_NAME + " WHERE price > ? ORDER BY price",
            10
        );

        assertEquals(2, expensiveProducts.size(),
            "Should find 2 products with price > 10 (Laptop: 999, Keyboard: 75)");

        // Verify the results
        Map<String, Object> keyboard = expensiveProducts.get(0);
        assertEquals("Keyboard", keyboard.get("name"));
        assertEquals(75, ((Number) keyboard.get("price")).intValue());

        Map<String, Object> laptop = expensiveProducts.get(1);
        assertEquals("Laptop", laptop.get("name"));
        assertEquals(999, ((Number) laptop.get("price")).intValue());
    }

    @Test
    @DisplayName("Should query individual records by ID")
    void testQueryById() {
        // Insert test data
        Long productId = dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of(
            "name", "Monitor",
            "price", 299
        ));

        assertNotNull(productId, "Insert should return ID");

        // Query by ID
        Map<String, Object> product = dataRecordService.queryById(TEST_COLLECTION_NAME, productId);

        assertNotNull(product, "Product should be found");
        assertEquals("Monitor", product.get("name"));
        assertEquals(299, ((Number) product.get("price")).intValue());
    }

    @Test
    @DisplayName("Should update existing records")
    void testUpdateRecord() {
        // Insert test data
        Long productId = dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of(
            "name", "Headphones",
            "price", 50
        ));

        assertNotNull(productId, "Insert should return ID");

        // Update the record
        int updated = dataRecordService.updateRecord(TEST_COLLECTION_NAME, productId, Map.of(
            "price", 45
        ));

        assertEquals(1, updated, "Should update 1 record");

        // Verify update
        Map<String, Object> product = dataRecordService.queryById(TEST_COLLECTION_NAME, productId);
        assertEquals(45, ((Number) product.get("price")).intValue(),
            "Price should be updated to 45");
    }

    @Test
    @DisplayName("Should delete records")
    void testDeleteRecord() {
        // Insert test data
        Long productId = dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of(
            "name", "Webcam",
            "price", 89
        ));

        assertNotNull(productId, "Insert should return ID");

        // Delete the record
        int deleted = dataRecordService.deleteRecord(TEST_COLLECTION_NAME, productId);

        assertEquals(1, deleted, "Should delete 1 record");

        // Verify deletion
        Map<String, Object> product = dataRecordService.queryById(TEST_COLLECTION_NAME, productId);
        assertNull(product, "Product should be deleted");
    }

    @Test
    @DisplayName("Should count records correctly")
    void testCountRecords() {
        // Initially should be 0
        long initialCount = dataRecordService.countRecords(TEST_COLLECTION_NAME);
        assertEquals(0, initialCount, "Initial count should be 0");

        // Insert 3 records
        dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of("name", "A", "price", 10));
        dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of("name", "B", "price", 20));
        dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of("name", "C", "price", 30));

        long finalCount = dataRecordService.countRecords(TEST_COLLECTION_NAME);
        assertEquals(3, finalCount, "Count should be 3 after inserts");
    }

    @Test
    @DisplayName("Should check record existence")
    void testExists() {
        // Insert test data
        Long productId = dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of(
            "name", "Speaker",
            "price", 120
        ));

        // Check existence
        assertTrue(dataRecordService.exists(TEST_COLLECTION_NAME, productId),
            "Record should exist");

        // Check non-existent record
        assertFalse(dataRecordService.exists(TEST_COLLECTION_NAME, 99999L),
            "Non-existent record should return false");
    }

    @Test
    @DisplayName("Should query with conditions")
    void testQueryWithConditions() {
        // Insert test data
        dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of("name", "Apple", "price", 5));
        dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of("name", "Banana", "price", 3));
        dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of("name", "Orange", "price", 4));

        // Query with price condition
        List<Map<String, Object>> cheapFruits = dataRecordService.queryRecords(
            TEST_COLLECTION_NAME,
            Map.of("price", 3)
        );

        assertEquals(1, cheapFruits.size(), "Should find 1 fruit with price = 3");
        assertEquals("Banana", cheapFruits.get(0).get("name"));
    }

    @Test
    @DisplayName("Should handle complete CRUD lifecycle")
    void testCompleteCrudLifecycle() {
        // Create
        Long id = dataRecordService.createRecord(TEST_COLLECTION_NAME, Map.of(
            "name", "Tablet",
            "price", 399
        ));
        assertNotNull(id, "Should create record with ID");

        // Read
        Map<String, Object> record = dataRecordService.queryById(TEST_COLLECTION_NAME, id);
        assertNotNull(record, "Should read created record");
        assertEquals("Tablet", record.get("name"));

        // Update
        int updated = dataRecordService.updateRecord(TEST_COLLECTION_NAME, id, Map.of(
            "name", "iPad",
            "price", 499
        ));
        assertEquals(1, updated, "Should update 1 record");

        Map<String, Object> updatedRecord = dataRecordService.queryById(TEST_COLLECTION_NAME, id);
        assertEquals("iPad", updatedRecord.get("name"));
        assertEquals(499, ((Number) updatedRecord.get("price")).intValue());

        // Delete
        int deleted = dataRecordService.deleteRecord(TEST_COLLECTION_NAME, id);
        assertEquals(1, deleted, "Should delete 1 record");

        Map<String, Object> deletedRecord = dataRecordService.queryById(TEST_COLLECTION_NAME, id);
        assertNull(deletedRecord, "Record should be deleted");
    }
}
