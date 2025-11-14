package com.milktea.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.milktea.entity.User;
import com.milktea.entity.Product;
import com.milktea.mapper.UserMapper;
import com.milktea.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库测试Controller - Spring Boot 3
 * @author MilkTea Team
 */
@RestController
@RequestMapping("/database")
public class DatabaseController {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/test")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查询用户总数
            Long userCount = userMapper.selectCount(new QueryWrapper<>());
            
            // 查询商品总数
            Long productCount = productMapper.selectCount(new QueryWrapper<>());
            
            result.put("code", 200);
            result.put("message", "Spring Boot 3 数据库连接成功");
            result.put("userCount", userCount);
            result.put("productCount", productCount);
            result.put("framework", "Spring Boot 3.2.0");
            result.put("database", "MySQL 8.0");
            result.put("orm", "MyBatis Plus 3.5.5");
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "数据库连接失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public Map<String, Object> getUsers() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 1);
            queryWrapper.last("LIMIT 10");
            
            List<User> users = userMapper.selectList(queryWrapper);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", users);
            result.put("count", users.size());
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取商品列表
     */
    @GetMapping("/products")
    public Map<String, Object> getProducts() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 1);
            queryWrapper.last("LIMIT 10");
            
            List<Product> products = productMapper.selectList(queryWrapper);
            
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", products);
            result.put("count", products.size());
            
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }
}
