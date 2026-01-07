package com.nocobase.user.mapper;

import com.mybatisflex.core.BaseMapper;
import com.nocobase.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * User Mapper Interface
 * MyBatis-Flex BaseMapper provides CRUD operations
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
