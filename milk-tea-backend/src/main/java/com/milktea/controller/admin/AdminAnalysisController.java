package com.milktea.controller.admin;

import com.milktea.common.Result;
import com.milktea.service.AnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端数据分析控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/analysis")
public class AdminAnalysisController {
    
    private final AnalysisService analysisService;
    
    public AdminAnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }
    
    /**
     * 获取会员分析概览数据
     */
    @GetMapping("/member/overview")
    public Result<Map<String, Object>> getMemberOverview() {
        return analysisService.getMemberOverview();
    }
    
    /**
     * 获取会员消费分布
     */
    @GetMapping("/member/consumption")
    public Result<Map<String, Object>> getMemberConsumption(
            @RequestParam(defaultValue = "30") Integer days) {
        return analysisService.getMemberConsumption(days);
    }
    
    /**
     * 获取会员等级分布
     */
    @GetMapping("/member/level-distribution")
    public Result<List<Map<String, Object>>> getMemberLevelDistribution() {
        return analysisService.getMemberLevelDistribution();
    }
    
    /**
     * 获取会员消费行为分析
     */
    @GetMapping("/member/behavior")
    public Result<Map<String, Object>> getMemberBehavior() {
        return analysisService.getMemberBehavior();
    }
    
    /**
     * 获取会员活跃度趋势
     */
    @GetMapping("/member/activity-trend")
    public Result<Map<String, Object>> getMemberActivityTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        return analysisService.getMemberActivityTrend(days);
    }
    
    /**
     * 获取会员价值分层
     */
    @GetMapping("/member/segmentation")
    public Result<List<Map<String, Object>>> getMemberSegmentation() {
        return analysisService.getMemberSegmentation();
    }
    
    /**
     * 获取高价值客户列表
     */
    @GetMapping("/member/vip-customers")
    public Result<List<Map<String, Object>>> getVipCustomers(
            @RequestParam(defaultValue = "20") Integer limit) {
        return analysisService.getVipCustomers(limit);
    }
    
    /**
     * 获取客户详细信息（包含消费偏好）
     */
    @GetMapping("/member/customer-detail/{userId}")
    public Result<Map<String, Object>> getCustomerDetail(@PathVariable Long userId) {
        return analysisService.getCustomerDetail(userId);
    }
}
