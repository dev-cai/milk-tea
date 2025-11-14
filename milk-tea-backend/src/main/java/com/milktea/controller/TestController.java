package com.milktea.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试Controller - Spring Boot 3
 * @author MilkTea Team
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "🎉 Spring Boot 3 奶茶小程序后端启动成功！");
        result.put("data", "Hello, Spring Boot 3 MilkTea!");
        result.put("version", "Spring Boot 3.2.0");
        result.put("java", "Java 17");
        result.put("timestamp", LocalDateTime.now());
        return result;
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("framework", "Spring Boot 3.2.0");
        result.put("database", "MySQL 8.0");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
    
    @GetMapping("/features")
    public Map<String, Object> features() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Spring Boot 3 新特性");
        
        Map<String, Object> features = new HashMap<>();
        features.put("jakarta", "Jakarta EE 支持");
        features.put("jwt", "JWT 0.12.3 兼容");
        features.put("validation", "Jakarta Validation");
        features.put("servlet", "Jakarta Servlet");
        features.put("mybatis", "MyBatis Plus 3.5.5");
        features.put("druid", "Druid Spring Boot 3 Starter");
        
        result.put("data", features);
        return result;
    }
}
