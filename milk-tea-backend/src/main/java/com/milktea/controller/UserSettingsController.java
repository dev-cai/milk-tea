package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.UserSettings;
import com.milktea.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户设置控制器
 */
@Slf4j
@RestController
@RequestMapping("/user/settings")
@RequiredArgsConstructor
public class UserSettingsController {
    
    private final UserSettingsService userSettingsService;
    
    /**
     * 获取用户设置
     */
    @GetMapping("/{userId}")
    public Result<UserSettings> getUserSettings(@PathVariable Long userId) {
        log.info("获取用户设置，userId: {}", userId);
        return userSettingsService.getUserSettings(userId);
    }
    
    /**
     * 更新用户设置
     */
    @PutMapping
    public Result<String> updateUserSettings(@RequestBody UserSettings settings) {
        log.info("更新用户设置，userId: {}, settings: {}", settings.getUserId(), settings);
        return userSettingsService.updateUserSettings(settings);
    }
}
