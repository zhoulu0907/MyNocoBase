package com.nocobase.data.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.nocobase.data.entity.Collection;
import com.nocobase.data.manager.DynamicTableManager;
import com.nocobase.data.mapper.CollectionMapper;
import com.nocobase.data.service.CollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.nocobase.data.entity.table.CollectionTableDef.COLLECTION;

/**
 * Collection Service Implementation
 * Manages collection metadata and physical table creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {

    private final DynamicTableManager dynamicTableManager;

    /**
     * Create a new collection with transaction
     * Ensures atomicity between metadata saving and physical table creation
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Collection createCollection(Collection collection) {
        log.info("Creating collection: name={}, title={}", collection.getName(), collection.getTitle());

        // Validate collection name uniqueness
        Collection existing = findByName(collection.getName());
        if (existing != null) {
            throw new RuntimeException("Collection already exists: " + collection.getName());
        }

        // Set default values
        if (collection.getTableName() == null) {
            collection.setTableName(collection.getName());
        }
        if (collection.getSystem() == null) {
            collection.setSystem(false);
        }
        if (collection.getHidden() == null) {
            collection.setHidden(false);
        }
        if (collection.getType() == null) {
            collection.setType("table");
        }
        collection.setCreatedAt(LocalDateTime.now());
        collection.setUpdatedAt(LocalDateTime.now());

        // Step 1: Save collection metadata to database
        boolean saved = save(collection);
        if (!saved) {
            throw new RuntimeException("Failed to save collection metadata");
        }

        log.info("Collection metadata saved: id={}, name={}", collection.getId(), collection.getName());

        // Step 2: Create physical table using DynamicTableManager
        try {
            dynamicTableManager.createTable(collection.getTableName(), collection.getTitle());
            log.info("Physical table created: tableName={}", collection.getTableName());
        } catch (Exception e) {
            log.error("Failed to create physical table: tableName={}", collection.getTableName(), e);
            // Transaction will be rolled back automatically
            throw new RuntimeException("Failed to create physical table: " + e.getMessage(), e);
        }

        return collection;
    }

    /**
     * Find collection by name using static TableDef
     */
    @Override
    public Collection findByName(String name) {
        QueryWrapper query = QueryWrapper.create()
                .where(COLLECTION.NAME.eq(name))
                .and(COLLECTION.DELETED_AT.isNull());

        return getOne(query);
    }

    /**
     * Delete collection (soft delete)
     * Also drops the physical table
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCollection(Long id) {
        log.info("Deleting collection: id={}", id);

        Collection collection = getById(id);
        if (collection == null) {
            throw new RuntimeException("Collection not found: id=" + id);
        }

        // Step 1: Soft delete collection metadata
        collection.setDeletedAt(LocalDateTime.now());
        boolean updated = updateById(collection);

        if (!updated) {
            throw new RuntimeException("Failed to delete collection metadata");
        }

        // Step 2: Drop physical table
        try {
            // TODO: Implement dropTable in DynamicTableManager
            log.info("Physical table would be dropped: tableName={}", collection.getTableName());
        } catch (Exception e) {
            log.error("Failed to drop physical table: tableName={}", collection.getTableName(), e);
            throw new RuntimeException("Failed to drop physical table: " + e.getMessage(), e);
        }

        return true;
    }
}
