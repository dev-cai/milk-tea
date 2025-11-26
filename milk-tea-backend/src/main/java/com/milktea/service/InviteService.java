package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.InviteRecord;
import com.milktea.entity.User;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.InviteRecordMapper;
import com.milktea.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 邀请服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class InviteService {
    
    private final UserMapper userMapper;
    private final InviteRecordMapper inviteRecordMapper;
    
    private static final String INVITE_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int INVITE_CODE_LENGTH = 6;
    private static final int INVITE_REWARD_POINTS = 50; // 邀请奖励积分
    
    public InviteService(UserMapper userMapper, InviteRecordMapper inviteRecordMapper) {
        this.userMapper = userMapper;
        this.inviteRecordMapper = inviteRecordMapper;
    }
    
    /**
     * 获取用户邀请码（如果没有则生成）
     */
    public Result<String> getInviteCode(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 如果已有邀请码，直接返回
        if (user.getInviteCode() != null && !user.getInviteCode().isEmpty()) {
            return Result.success("获取成功", user.getInviteCode());
        }
        
        // 生成新的邀请码
        String inviteCode = generateUniqueInviteCode();
        user.setInviteCode(inviteCode);
        userMapper.updateById(user);
        
        return Result.success("获取成功", inviteCode);
    }
    
    /**
     * 绑定邀请码
     */
    @Transactional
    public Result<String> bindInviteCode(Long userId, String inviteCode) {
        log.info("用户 {} 绑定邀请码: {}", userId, inviteCode);
        
        // 1. 检查用户是否存在
        User invitee = userMapper.selectById(userId);
        if (invitee == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 2. 检查是否已经绑定过邀请码
        if (invitee.getInviterId() != null) {
            throw new BusinessException("您已经使用过邀请码，不能重复使用");
        }
        
        // 3. 查找邀请人
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getInviteCode, inviteCode);
        User inviter = userMapper.selectOne(queryWrapper);
        
        if (inviter == null) {
            throw new BusinessException("邀请码不存在");
        }
        
        // 4. 不能使用自己的邀请码
        if (inviter.getId().equals(userId)) {
            throw new BusinessException("不能使用自己的邀请码");
        }
        
        // 5. 绑定邀请关系
        invitee.setInviterId(inviter.getId());
        invitee.setInviteTime(LocalDateTime.now());
        userMapper.updateById(invitee);
        
        // 6. 创建邀请记录
        InviteRecord record = new InviteRecord();
        record.setInviterId(inviter.getId());
        record.setInviteeId(invitee.getId());
        record.setPoints(INVITE_REWARD_POINTS);
        record.setStatus(2); // 2-已奖励
        record.setRewardTime(LocalDateTime.now());
        inviteRecordMapper.insert(record);
        
        // 7. 奖励邀请人积分
        inviter.setPoints(inviter.getPoints() + INVITE_REWARD_POINTS);
        userMapper.updateById(inviter);
        
        log.info("邀请绑定成功 - 邀请人: {}, 被邀请人: {}, 奖励积分: {}", 
                inviter.getNickname(), invitee.getNickname(), INVITE_REWARD_POINTS);
        
        return Result.success("绑定成功，邀请人获得" + INVITE_REWARD_POINTS + "积分");
    }
    
    /**
     * 获取我邀请的好友列表
     */
    public Result<List<InviteRecord>> getMyInvites(Long userId) {
        LambdaQueryWrapper<InviteRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InviteRecord::getInviterId, userId)
                   .orderByDesc(InviteRecord::getCreateTime);
        
        List<InviteRecord> records = inviteRecordMapper.selectList(queryWrapper);
        return Result.success("获取成功", records);
    }
    
    /**
     * 生成唯一的邀请码
     */
    private String generateUniqueInviteCode() {
        Random random = new Random();
        String inviteCode;
        int attempts = 0;
        int maxAttempts = 10;
        
        do {
            StringBuilder sb = new StringBuilder(INVITE_CODE_LENGTH);
            for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
                int index = random.nextInt(INVITE_CODE_CHARS.length());
                sb.append(INVITE_CODE_CHARS.charAt(index));
            }
            inviteCode = sb.toString();
            attempts++;
            
            // 检查是否已存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getInviteCode, inviteCode);
            Long count = userMapper.selectCount(queryWrapper);
            
            if (count == 0) {
                return inviteCode;
            }
            
        } while (attempts < maxAttempts);
        
        throw new BusinessException("生成邀请码失败，请重试");
    }
}
