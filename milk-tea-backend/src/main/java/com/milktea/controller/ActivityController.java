package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.Activity;
import com.milktea.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 营销活动控制器（用户端）
 */
@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {
    
    private final ActivityService activityService;
    
    /**
     * 获取进行中的活动列表
     */
    @GetMapping("/list")
    public Result<List<Activity>> list() {
        List<Activity> list = activityService.getActiveList();
        return Result.success(list);
    }
    
    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public Result<Activity> getById(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        return Result.success(activity);
    }
}
