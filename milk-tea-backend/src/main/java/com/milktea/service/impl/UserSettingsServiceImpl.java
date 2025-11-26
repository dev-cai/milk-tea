package com.milktea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.UserSettings;
import com.milktea.mapper.UserSettingsMapper;
import com.milktea.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户设置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSettingsServiceImpl implements UserSettingsService {
    
    private final UserSettingsMapper userSettingsMapper;
    
    @Override
    public Result<UserSettings> getUserSettings(Long userId) {
        try {
            LambdaQueryWrapper<UserSettings> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserSettings::getUserId, userId);
            UserSettings settings = userSettingsMapper.selectOne(wrapper);
            
            // 如果用户设置不存在，创建默认设置
            if (settings == null) {
                settings = new UserSettings();
                settings.setUserId(userId);
                settings.setOrderNotification(1);
                settings.setActivityPush(1);
                settings.setCouponReminder(1);
                settings.setPersonalizedRecommend(1);
                userSettingsMapper.insert(settings);
            }
            
            return Result.success(settings);
        } catch (Exception e) {
            log.error("获取用户设置失败", e);
            return Result.error("获取用户设置失败");
        }
    }
    
    @Override
    public Result<String> updateUserSettings(UserSettings settings) {
        try {
            LambdaQueryWrapper<UserSettings> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserSettings::getUserId, settings.getUserId());
            UserSettings existingSettings = userSettingsMapper.selectOne(wrapper);
            
            if (existingSettings == null) {
                // 如果不存在，创建新记录
                userSettingsMapper.insert(settings);
            } else {
                // 如果存在，更新记录
                settings.setId(existingSettings.getId());
                userSettingsMapper.updateById(settings);
            }
            
            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新用户设置失败", e);
            return Result.error("更新用户设置失败");
        }
    }
}
