package com.nocobase.user.service;

import com.mybatisflex.core.service.IService;
import com.nocobase.user.entity.User;

/**
 * User Service Interface
 * Extends MyBatis-Flex IService for common CRUD operations
 */
public interface UserService extends IService<User> {

    /**
     * Get user with associated roles
     * Manual join query recommended by MyBatis-Flex
     *
     * @param username the username
     * @return User with roles populated
     */
    User getUserWithRoles(String username);
}
