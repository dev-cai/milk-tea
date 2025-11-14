package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.User;
import com.milktea.entity.UserAddress;
import com.milktea.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam Long userId) {
        return userService.getUserInfo(userId);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<String> updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }
    
    /**
     * 获取用户地址列表
     */
    @GetMapping("/addresses")
    public Result<List<UserAddress>> getUserAddresses(@RequestParam Long userId) {
        return userService.getUserAddresses(userId);
    }
    
    /**
     * 添加用户地址
     */
    @PostMapping("/address")
    public Result<String> addUserAddress(@RequestBody UserAddress address) {
        return userService.addUserAddress(address);
    }
    
    /**
     * 更新用户地址
     */
    @PutMapping("/address")
    public Result<String> updateUserAddress(@RequestBody UserAddress address) {
        return userService.updateUserAddress(address);
    }
    
    /**
     * 删除用户地址
     */
    @DeleteMapping("/address/{id}")
    public Result<String> deleteUserAddress(@PathVariable Long id) {
        return userService.deleteUserAddress(id);
    }
}
