package com.nocobase.user.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * Permission Domain Entity
 * Represents a permission in system
 */
@Data
@Table("permissions")
public class Permission {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String name;

    private String description;

    private String resource;

    private String action;

    private Boolean status;
}
