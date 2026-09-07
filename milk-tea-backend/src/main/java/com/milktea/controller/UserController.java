package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.User;
import com.milktea.entity.UserAddress;
import com.milktea.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public Result<User> getUserInfo(@RequestParam Long userId, HttpServletRequest request) {
        return userService.getUserInfo(verifyUser(userId, request));
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<String> updateUserInfo(@RequestBody User user, HttpServletRequest request) {
        user.setId(currentUser(request));
        return userService.updateUserInfo(user);
    }
    
    /**
     * 获取用户地址列表
     */
    @GetMapping("/addresses")
    public Result<List<UserAddress>> getUserAddresses(@RequestParam Long userId, HttpServletRequest request) {
        return userService.getUserAddresses(verifyUser(userId, request));
    }
    
    /**
     * 添加用户地址
     */
    @PostMapping("/address")
    public Result<String> addUserAddress(@RequestBody UserAddress address, HttpServletRequest request) {
        address.setUserId(currentUser(request));
        return userService.addUserAddress(address);
    }
    
    /**
     * 更新用户地址
     */
    @PutMapping("/address")
    public Result<String> updateUserAddress(@RequestBody UserAddress address, HttpServletRequest request) {
        address.setUserId(currentUser(request));
        return userService.updateUserAddress(address);
    }
    
    /**
     * 删除用户地址
     */
    @DeleteMapping("/address/{id}")
    public Result<String> deleteUserAddress(@PathVariable Long id, HttpServletRequest request) {
        return userService.deleteUserAddress(id, currentUser(request));
    }
    
    /**
     * 绑定微信
     */
    @PostMapping("/bind-wechat")
    public Result<String> bindWechat(@RequestBody User user, HttpServletRequest request) {
        user.setId(currentUser(request));
        return userService.bindWechat(user);
    }
    
    /**
     * 解绑微信
     */
    @PostMapping("/unbind-wechat")
    public Result<String> unbindWechat(@RequestBody User user, HttpServletRequest request) {
        user.setId(currentUser(request));
        return userService.unbindWechat(user);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestBody java.util.Map<String, Object> params, HttpServletRequest request) {
        params.put("userId", currentUser(request));
        return userService.changePassword(params);
    }
    
    /**
     * 获取登录记录
     */
    @GetMapping("/login-history")
    public Result<java.util.List<com.milktea.entity.LoginHistory>> getLoginHistory(@RequestParam Long userId, HttpServletRequest request) {
        return userService.getLoginHistory(verifyUser(userId, request));
    }

    private Long currentUser(HttpServletRequest request) {
        Object userId = request.getAttribute("authenticatedUserId");
        if (userId == null) {
            throw new org.springframework.security.access.AccessDeniedException("未登录");
        }
        return Long.valueOf(userId.toString());
    }

    private Long verifyUser(Long suppliedUserId, HttpServletRequest request) {
        Long userId = currentUser(request);
        if (suppliedUserId != null && !userId.equals(suppliedUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("无权操作其他用户数据");
        }
        return userId;
    }
}
