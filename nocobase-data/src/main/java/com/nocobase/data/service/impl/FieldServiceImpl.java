package com.nocobase.data.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.nocobase.data.entity.Collection;
import com.nocobase.data.entity.Field;
import com.nocobase.data.manager.DynamicTableManager;
import com.nocobase.data.mapper.FieldMapper;
import com.nocobase.data.service.CollectionService;
import com.nocobase.data.service.FieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nocobase.data.entity.table.FieldTableDef.FIELD;

/**
 * Field Service Implementation
 * Manages field metadata and physical column creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FieldServiceImpl extends ServiceImpl<FieldMapper, Field> implements FieldService {

    private final DynamicTableManager dynamicTableManager;
    private final CollectionService collectionService;

    /**
     * Field type to database type mapping
     */
    private static final Map<String, String> TYPE_MAPPING = new HashMap<>();

    static {
        TYPE_MAPPING.put("string", "VARCHAR(255)");
        TYPE_MAPPING.put("text", "TEXT");
        TYPE_MAPPING.put("integer", "INTEGER");
        TYPE_MAPPING.put("bigInteger", "BIGINT");
        TYPE_MAPPING.put("float", "FLOAT");
        TYPE_MAPPING.put("double", "DOUBLE");
        TYPE_MAPPING.put("decimal", "DECIMAL(10,2)");
        TYPE_MAPPING.put("boolean", "BOOLEAN");
        TYPE_MAPPING.put("date", "DATE");
        TYPE_MAPPING.put("datetime", "TIMESTAMP");
        TYPE_MAPPING.put("time", "TIME");
        TYPE_MAPPING.put("json", "JSONB");
        TYPE_MAPPING.put("uuid", "UUID");
    }

    /**
     * Create a new field with transaction
     * Ensures atomicity between metadata saving and physical column creation
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Field createField(Field field) {
        log.info("Creating field: collectionId={}, name={}, type={}",
                field.getCollectionId(), field.getName(), field.getType());

        // Validate collection exists
        Collection collection = collectionService.getById(field.getCollectionId());
        if (collection == null) {
            throw new RuntimeException("Collection not found: id=" + field.getCollectionId());
        }

        // Validate field name uniqueness within collection
        Field existing = findByCollectionAndName(field.getCollectionId(), field.getName());
        if (existing != null) {
            throw new RuntimeException("Field already exists: " + field.getName());
        }

        // Set default values
        if (field.getSystem() == null) {
            field.setSystem(false);
        }
        if (field.getHidden() == null) {
            field.setHidden(false);
        }
        if (field.getRequired() == null) {
            field.setRequired(false);
        }
        if (field.getUnique() == null) {
            field.setUnique(false);
        }

        // Map field type to database type
        if (field.getDbType() == null) {
            String dbType = TYPE_MAPPING.getOrDefault(field.getType(), "VARCHAR(255)");
            field.setDbType(dbType);
        }

        field.setCreatedAt(LocalDateTime.now());
        field.setUpdatedAt(LocalDateTime.now());

        // Step 1: Save field metadata to database
        boolean saved = save(field);
        if (!saved) {
            throw new RuntimeException("Failed to save field metadata");
        }

        log.info("Field metadata saved: id={}, name={}", field.getId(), field.getName());

        // Step 2: Add column to physical table using DynamicTableManager
        try {
            dynamicTableManager.addColumn(collection.getTableName(), field.getName(), field.getDbType());
            log.info("Physical column added: tableName={}, columnName={}, columnType={}",
                    collection.getTableName(), field.getName(), field.getDbType());
        } catch (Exception e) {
            log.error("Failed to add physical column: tableName={}, columnName={}",
                    collection.getTableName(), field.getName(), e);
            // Transaction will be rolled back automatically
            throw new RuntimeException("Failed to add physical column: " + e.getMessage(), e);
        }

        return field;
    }

    /**
     * Find fields by collection ID using static TableDef
     */
    @Override
    public List<Field> findByCollectionId(Long collectionId) {
        QueryWrapper query = QueryWrapper.create()
                .where(FIELD.COLLECTION_ID.eq(collectionId))
                .and(FIELD.DELETED_AT.isNull())
                .orderBy(FIELD.SORT.asc(), FIELD.ID.asc());

        return list(query);
    }

    /**
     * Find field by collection ID and name
     */
    private Field findByCollectionAndName(Long collectionId, String name) {
        QueryWrapper query = QueryWrapper.create()
                .where(FIELD.COLLECTION_ID.eq(collectionId))
                .and(FIELD.NAME.eq(name))
                .and(FIELD.DELETED_AT.isNull());

        return getOne(query);
    }

    /**
     * Delete field (soft delete)
     * Also drops the physical column
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteField(Long id) {
        log.info("Deleting field: id={}", id);

        Field field = getById(id);
        if (field == null) {
            throw new RuntimeException("Field not found: id=" + id);
        }

        Collection collection = collectionService.getById(field.getCollectionId());
        if (collection == null) {
            throw new RuntimeException("Collection not found: id=" + field.getCollectionId());
        }

        // Step 1: Soft delete field metadata
        field.setDeletedAt(LocalDateTime.now());
        boolean updated = updateById(field);

        if (!updated) {
            throw new RuntimeException("Failed to delete field metadata");
        }

        // Step 2: Drop physical column
        try {
            // TODO: Implement dropColumn in DynamicTableManager
            log.info("Physical column would be dropped: tableName={}, columnName={}",
                    collection.getTableName(), field.getName());
        } catch (Exception e) {
            log.error("Failed to drop physical column: tableName={}, columnName={}",
                    collection.getTableName(), field.getName(), e);
            throw new RuntimeException("Failed to drop physical column: " + e.getMessage(), e);
        }

        return true;
    }
}
