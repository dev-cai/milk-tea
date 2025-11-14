package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.User;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.UserMapper;
import com.milktea.utils.JwtUtils;
import com.milktea.utils.MD5Utils;
import com.milktea.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class AuthService {
    
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    
    public AuthService(UserMapper userMapper, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
    }
    
    /**
     * 用户登录
     */
    public Result<Map<String, Object>> login(String username, String password) {
        // 输入验证
        if (!ValidationUtils.isSafeInput(username) || !ValidationUtils.isSafeInput(password)) {
            throw new BusinessException("输入包含非法字符");
        }
        
        if (!ValidationUtils.isValidUsername(username)) {
            throw new BusinessException("用户名格式不正确");
        }
        
        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }
        
        // 验证密码
        if (!MD5Utils.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getUserType());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("userType", user.getUserType());
        result.put("user", userInfo);
        
        return Result.success("登录成功", result);
    }
    
    /**
     * 用户注册
     */
    public Result<String> register(String username, String password, String nickname) {
        // 输入验证
        if (!ValidationUtils.isSafeInput(username) || !ValidationUtils.isSafeInput(password) || !ValidationUtils.isSafeInput(nickname)) {
            throw new BusinessException("输入包含非法字符");
        }
        
        if (!ValidationUtils.isValidUsername(username)) {
            throw new BusinessException("用户名格式不正确：4-20位字母数字下划线");
        }
        
        if (!ValidationUtils.isValidPassword(password)) {
            throw new BusinessException("密码格式不正确：6-20位，至少包含字母和数字");
        }
        
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User existUser = userMapper.selectOne(queryWrapper);
        
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(MD5Utils.encode(password));
        user.setNickname(nickname);
        user.setMemberLevel(0); // 普通会员
        user.setPoints(0);
        user.setBalance(BigDecimal.ZERO);
        user.setUserType(0); // 普通用户
        user.setStatus(1); // 启用状态
        
        userMapper.insert(user);
        
        return Result.success("注册成功");
    }
}
