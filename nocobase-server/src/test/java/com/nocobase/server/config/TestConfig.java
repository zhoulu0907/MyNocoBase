package com.nocobase.server.config;

import com.mybatis.flex.core.FlexGlobalConfig;
import com.mybatis.flex.spring.FlexSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Test Configuration for MyBatis-Flex
 * Manually configures SqlSessionFactory for MyBatis-Flex in test environment
 */
@Configuration
@MapperScan(basePackages = {
    "com.nocobase.user.mapper",
    "com.nocobase.data.mapper"
})
public class TestConfig {

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Autowired DataSource dataSource) throws Exception {
        FlexSqlSessionFactoryBean factory = new FlexSqlSessionFactoryBean();
        factory.setDataSource(dataSource);

        // Configure MyBatis-Flex global settings
        FlexGlobalConfig globalConfig = FlexGlobalConfig.getDefaultConfig();
        factory.setGlobalConfig(globalConfig);

        return factory.getObject();
    }
}
