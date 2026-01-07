package com.nocobase.data.service;

import com.mybatisflex.core.service.IService;
import com.nocobase.data.entity.Collection;

/**
 * Collection Service Interface
 * Provides business logic for collection management
 */
public interface CollectionService extends IService<Collection> {

    /**
     * Create a new collection
     * This will:
     * 1. Save collection metadata to nocobase_collections table
     * 2. Create physical table in database using DynamicTableManager
     *
     * @param collection Collection entity
     * @return Created collection with ID
     */
    Collection createCollection(Collection collection);

    /**
     * Find collection by name
     *
     * @param name Collection name
     * @return Collection entity or null
     */
    Collection findByName(String name);

    /**
     * Delete collection by ID
     * This will:
     * 1. Soft delete collection metadata
     * 2. Drop physical table from database
     *
     * @param id Collection ID
     * @return true if successful
     */
    boolean deleteCollection(Long id);
}
