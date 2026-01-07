package com.nocobase.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NocoBase Monolithic Application
 * Entry point for the entire NocoBase system
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.nocobase.common",
        "com.nocobase.auth",
        "com.nocobase.user",
        "com.nocobase.data",
        "com.nocobase.permission",
        "com.nocobase.workflow",
        "com.nocobase.ai",
        "com.nocobase.file"
    }
)
@MapperScan(basePackages = {
    "com.nocobase.user.mapper",
    "com.nocobase.data.mapper",
    "com.nocobase.auth.mapper",
    "com.nocobase.permission.mapper",
    "com.nocobase.workflow.mapper",
    "com.nocobase.ai.mapper",
    "com.nocobase.file.mapper"
})
public class NocoBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(NocoBaseApplication.class, args);
    }
}
