package com.milktea.controller.admin;

import com.milktea.common.Result;
import com.milktea.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端仪表盘控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = dashboardService.getDashboardStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败", e);
            return Result.error("获取统计数据失败");
        }
    }
    
    /**
     * 获取销售趋势数据
     */
    @GetMapping("/sales-trend")
    public Result<Map<String, Object>> getSalesTrend(@RequestParam(defaultValue = "7") Integer days) {
        try {
            Map<String, Object> trendData = dashboardService.getSalesTrend(days);
            return Result.success(trendData);
        } catch (Exception e) {
            log.error("获取销售趋势数据失败", e);
            return Result.error("获取销售趋势失败");
        }
    }
    
    /**
     * 获取商品销售排行
     */
    @GetMapping("/product-ranking")
    public Result<Map<String, Object>> getProductRanking(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            Map<String, Object> rankingData = dashboardService.getProductRanking(limit);
            return Result.success(rankingData);
        } catch (Exception e) {
            log.error("获取商品销售排行失败", e);
            return Result.error("获取商品排行失败");
        }
    }
    
    /**
     * 获取用户增长数据
     */
    @GetMapping("/user-growth")
    public Result<Map<String, Object>> getUserGrowth(@RequestParam(defaultValue = "30") Integer days) {
        try {
            Map<String, Object> growthData = dashboardService.getUserGrowth(days);
            return Result.success(growthData);
        } catch (Exception e) {
            log.error("获取用户增长数据失败", e);
            return Result.error("获取用户增长失败");
        }
    }
    
    /**
     * 获取预警信息
     */
    @GetMapping("/alerts")
    public Result<List<Map<String, Object>>> getAlerts() {
        try {
            List<Map<String, Object>> alerts = dashboardService.getAlerts();
            return Result.success(alerts);
        } catch (Exception e) {
            log.error("获取预警信息失败", e);
            return Result.error("获取预警信息失败");
        }
    }
    
    /**
     * 获取实时订单状态分布
     */
    @GetMapping("/order-status")
    public Result<Map<String, Object>> getOrderStatus() {
        try {
            Map<String, Object> statusData = dashboardService.getOrderStatus();
            return Result.success(statusData);
        } catch (Exception e) {
            log.error("获取订单状态分布失败", e);
            return Result.error("获取订单状态失败");
        }
    }
}
