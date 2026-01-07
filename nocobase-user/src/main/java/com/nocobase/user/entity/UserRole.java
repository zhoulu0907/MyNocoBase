package com.nocobase.user.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * User-Role Association Entity
 * Represents many-to-many relationship between users and roles
 */
@Data
@Table("users_roles")
public class UserRole {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long userId;

    private Long roleId;
}
