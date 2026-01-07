package com.nocobase.data.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Collection Entity
 * Stores metadata for database tables
 */
@Data
@Table("nocobase_collections")
public class Collection {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * Collection name (unique identifier)
     */
    private String name;

    /**
     * Collection display title
     */
    private String title;

    /**
     * Physical table name in database
     */
    private String tableName;

    /**
     * Collection description
     */
    private String description;

    /**
     * Collection type (table, view, etc.)
     */
    private String type;

    /**
     * Is system collection
     */
    private Boolean system;

    /**
     * Is hidden from UI
     */
    private Boolean hidden;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Update timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Soft delete timestamp
     */
    private LocalDateTime deletedAt;

    /**
     * Created by user ID
     */
    private Long createdBy;

    /**
     * Updated by user ID
     */
    private Long updatedBy;
}
