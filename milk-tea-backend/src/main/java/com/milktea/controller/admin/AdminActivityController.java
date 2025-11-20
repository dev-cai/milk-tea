package com.milktea.controller.admin;

import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.MarketingActivity;
import com.milktea.service.MarketingActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端营销活动控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/activity")
public class AdminActivityController {
    
    private final MarketingActivityService activityService;
    
    public AdminActivityController(MarketingActivityService activityService) {
        this.activityService = activityService;
    }
    
    /**
     * 分页查询活动
     */
    @GetMapping("/page")
    public Result<PageResult<MarketingActivity>> getActivityPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        return activityService.getActivityPage(page, size, keyword, type, status);
    }
    
    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public Result<MarketingActivity> getActivityById(@PathVariable Long id) {
        return activityService.getActivityById(id);
    }
    
    /**
     * 创建活动
     */
    @PostMapping
    public Result<String> createActivity(@RequestBody MarketingActivity activity) {
        return activityService.createActivity(activity);
    }
    
    /**
     * 更新活动
     */
    @PutMapping("/{id}")
    public Result<String> updateActivity(@PathVariable Long id, @RequestBody MarketingActivity activity) {
        return activityService.updateActivity(id, activity);
    }
    
    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteActivity(@PathVariable Long id) {
        return activityService.deleteActivity(id);
    }
    
    /**
     * 批量删除活动
     */
    @DeleteMapping("/batch")
    public Result<String> batchDeleteActivities(@RequestBody List<Long> ids) {
        return activityService.batchDeleteActivities(ids);
    }
    
    /**
     * 更新活动状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateActivityStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        return activityService.updateActivityStatus(id, status);
    }
    
    /**
     * 获取活动统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getActivityStatistics() {
        return activityService.getActivityStatistics();
    }
    
    /**
     * 获取活动分析数据
     */
    @GetMapping("/{id}/analysis")
    public Result<Map<String, Object>> getActivityAnalysis(@PathVariable Long id) {
        return activityService.getActivityAnalysis(id);
    }
}
