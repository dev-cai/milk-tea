package com.milktea.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus配置 - Spring Boot 3 简化版
 * @author MilkTea Team
 */
@Configuration
@MapperScan("com.milktea.mapper")
public class MybatisPlusConfig {
    
}
