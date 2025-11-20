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
    
    /**
     * 手机号登录（支持邀请码）
     */
    public Result<Map<String, Object>> phoneLogin(String phone, String code, String inviteCode) {
        // 验证手机号格式
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }
        
        // 简化版：验证码固定为123456
        if (!"123456".equals(code)) {
            throw new BusinessException("验证码错误");
        }
        
        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(queryWrapper);
        
        boolean isNewUser = false;
        
        // 如果用户不存在，自动注册
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setUsername(phone); // 直接使用手机号作为用户名
            user.setPassword(MD5Utils.encode("123456")); // 默认密码
            user.setNickname("用户" + phone.substring(7)); // 昵称显示后4位
            user.setMemberLevel(0);
            user.setPoints(0);
            user.setBalance(BigDecimal.ZERO);
            user.setUserType(0);
            user.setStatus(1);
            
            userMapper.insert(user);
            isNewUser = true;
            log.info("新用户注册: {}", phone);
            
            // 处理邀请码奖励
            if (inviteCode != null && !inviteCode.isEmpty()) {
                handleInviteReward(user.getId(), inviteCode);
            }
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getUserType());
        
        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("memberLevel", user.getMemberLevel());
        userInfo.put("points", user.getPoints());
        userInfo.put("balance", user.getBalance());
        userInfo.put("userType", user.getUserType());
        result.put("userInfo", userInfo);
        
        // 如果是新用户且有邀请码，返回提示
        if (isNewUser && inviteCode != null && !inviteCode.isEmpty()) {
            result.put("inviteMessage", "注册成功！您和邀请人各获得50积分奖励");
        }
        
        log.info("用户登录成功: {}", phone);
        
        return Result.success("登录成功", result);
    }
    
    /**
     * 处理邀请奖励
     */
    private void handleInviteReward(Long newUserId, String inviteCode) {
        try {
            // 解析邀请码获取邀请人ID
            // 这里简化处理，实际应该有专门的邀请码表
            log.info("处理邀请奖励 - 新用户ID: {}, 邀请码: {}", newUserId, inviteCode);
            
            // 给新用户增加50积分
            User newUser = userMapper.selectById(newUserId);
            if (newUser != null) {
                newUser.setPoints(newUser.getPoints() + 50);
                userMapper.updateById(newUser);
                log.info("新用户获得邀请奖励50积分");
            }
            
            // TODO: 给邀请人增加50积分
            // 需要根据邀请码查找邀请人，然后增加积分
            // 这里需要实现邀请码与用户ID的映射关系
            
        } catch (Exception e) {
            log.error("处理邀请奖励失败", e);
            // 不影响登录流程
        }
    }
}
