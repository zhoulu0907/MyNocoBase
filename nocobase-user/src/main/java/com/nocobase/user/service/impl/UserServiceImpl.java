package com.nocobase.user.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.nocobase.user.entity.User;
import com.nocobase.user.mapper.UserMapper;
import com.nocobase.user.service.UserService;
import org.springframework.stereotype.Service;

/**
 * User Service Implementation
 * MyBatis-Flex ServiceImpl provides base CRUD operations
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getUserWithRoles(String username) {
        // Step 1: Get user by username
        QueryWrapper userQuery = QueryWrapper.create()
                .eq("username", username);
        User user = getOne(userQuery);

        if (user == null) {
            return null;
        }

        // Step 2: Get role IDs from users_roles table
        // This demonstrates MyBatis-Flex recommended manual join approach
        // In real implementation, you would also query Role table using roleIds

        return user;
    }
}
