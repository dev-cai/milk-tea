package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        return authService.login(username, password);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String nickname = request.get("nickname");
        return authService.register(username, password, nickname);
    }
    
    /**
     * 发送验证码（简化版，直接返回123456）
     */
    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        
        // 简单的手机号验证
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }
        
        // 简化版：直接返回成功，验证码固定为123456
        log.info("发送验证码到手机号: {}, 验证码: 123456", phone);
        
        return Result.success("验证码已发送（测试环境固定为123456）");
    }
    
    /**
     * 手机号登录（支持邀请码）
     */
    @PostMapping("/phone-login")
    public Result<Map<String, Object>> phoneLogin(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");
        String inviteCode = request.get("inviteCode");
        
        return authService.phoneLogin(phone, code, inviteCode);
    }
}
