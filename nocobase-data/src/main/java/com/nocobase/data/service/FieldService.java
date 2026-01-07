package com.nocobase.data.service;

import com.mybatisflex.core.service.IService;
import com.nocobase.data.entity.Field;

import java.util.List;

/**
 * Field Service Interface
 * Provides business logic for field management
 */
public interface FieldService extends IService<Field> {

    /**
     * Create a new field
     * This will:
     * 1. Save field metadata to nocobase_fields table
     * 2. Add column to physical table using DynamicTableManager
     *
     * @param field Field entity
     * @return Created field with ID
     */
    Field createField(Field field);

    /**
     * Find fields by collection ID
     *
     * @param collectionId Collection ID
     * @return List of fields
     */
    List<Field> findByCollectionId(Long collectionId);

    /**
     * Delete field by ID
     * This will:
     * 1. Soft delete field metadata
     * 2. Drop column from physical table
     *
     * @param id Field ID
     * @return true if successful
     */
    boolean deleteField(Long id);
}
