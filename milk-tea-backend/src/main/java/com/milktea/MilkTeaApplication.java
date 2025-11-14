package com.milktea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 奶茶小程序后端启动类 - Spring Boot 3
 * @author MilkTea Team
 */
@SpringBootApplication
public class MilkTeaApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MilkTeaApplication.class, args);
        System.out.println("========================================");
        System.out.println("🎉 Spring Boot 3 奶茶小程序后端启动成功！");
        System.out.println("📖 API文档：http://localhost:8080/api/doc");
        System.out.println("🧪 测试接口：http://localhost:8080/api/test/hello");
        System.out.println("========================================");
    }
}
