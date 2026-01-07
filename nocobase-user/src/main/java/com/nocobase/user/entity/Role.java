package com.nocobase.user.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * Role Domain Entity
 * Represents a role in system with associated permissions
 */
@Data
@Table("roles")
public class Role {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String name;

    private String description;

    private Boolean status;
}
