package com.nocobase.data.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Field Entity
 * Stores metadata for table fields/columns
 */
@Data
@Table("nocobase_fields")
public class Field {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * Associated collection ID
     */
    private Long collectionId;

    /**
     * Field name (column name)
     */
    private String name;

    /**
     * Field display title
     */
    private String title;

    /**
     * Field type (string, integer, boolean, date, etc.)
     */
    private String type;

    /**
     * Database column type (VARCHAR, INTEGER, BOOLEAN, TIMESTAMP, etc.)
     */
    private String dbType;

    /**
     * Field description
     */
    private String description;

    /**
     * Is required field
     */
    private Boolean required;

    /**
     * Is unique field
     */
    private Boolean unique;

    /**
     * Default value
     */
    private String defaultValue;

    /**
     * Field UI schema (JSON)
     */
    private String uiSchema;

    /**
     * Sort order
     */
    private Integer sort;

    /**
     * Is system field
     */
    private Boolean system;

    /**
     * Is hidden from UI
     */
    private Boolean hidden;

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
