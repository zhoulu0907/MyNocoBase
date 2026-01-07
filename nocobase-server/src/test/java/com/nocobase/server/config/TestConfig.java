package com.nocobase.server.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.TestConfiguration;

/**
 * Test Configuration for MyBatis-Flex
 * Ensures Mappers are properly scanned in test environment
 */
@TestConfiguration
@MapperScan(basePackages = {
    "com.nocobase.user.mapper",
    "com.nocobase.data.mapper",
    "com.nocobase.auth.mapper"
})
public class TestConfig {
}
