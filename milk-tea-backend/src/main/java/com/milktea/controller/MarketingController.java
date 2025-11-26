package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.entity.Banner;
import com.milktea.entity.MarketingActivity;
import com.milktea.service.BannerService;
import com.milktea.service.MarketingActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 营销活动控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/marketing")
@RequiredArgsConstructor
public class MarketingController {
    
    private final BannerService bannerService;
    private final MarketingActivityService marketingActivityService;
    
    /**
     * 获取轮播图列表
     */
    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        List<Banner> banners = bannerService.getActiveList();
        return Result.success(banners);
    }
    
    /**
     * 获取营销活动列表
     */
    @GetMapping("/activities")
    public Result<List<MarketingActivity>> getActivities() {
        List<MarketingActivity> activities = marketingActivityService.getActiveList();
        return Result.success(activities);
    }
    
    /**
     * 获取活动详情
     */
    @GetMapping("/activity/{id}")
    public Result<MarketingActivity> getActivityDetail(@PathVariable Long id) {
        MarketingActivity activity = marketingActivityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        return Result.success(activity);
    }
    
    /**
     * 参与活动
     */
    @PostMapping("/activity/{id}/join")
    public Result<String> joinActivity(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        
        try {
            marketingActivityService.joinActivity(id, userId);
            return Result.success("参与成功");
        } catch (Exception e) {
            log.error("参与活动失败", e);
            return Result.error(e.getMessage());
        }
    }
}
