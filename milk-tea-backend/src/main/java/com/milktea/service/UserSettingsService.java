package com.milktea.service;

import com.milktea.common.Result;
import com.milktea.entity.UserSettings;

/**
 * 用户设置服务接口
 */
public interface UserSettingsService {
    
    /**
     * 获取用户设置
     */
    Result<UserSettings> getUserSettings(Long userId);
    
    /**
     * 更新用户设置
     */
    Result<String> updateUserSettings(UserSettings settings);
}
