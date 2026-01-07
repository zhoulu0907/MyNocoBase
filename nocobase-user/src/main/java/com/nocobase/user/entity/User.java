package com.nocobase.user.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * User Domain Entity
 * Represents a user in system
 */
@Data
@Table("users")
public class User {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String username;

    private String email;

    private String password;

    private String nickname;

    private String status;

    private String createdAt;

    private String updatedAt;

    private String deletedAt;

    private Long createdById;

    private Long updatedById;

    // MyBatis-Flex doesn't need relationship mappings like JPA
    // These will be handled at service/mapper layer
}
