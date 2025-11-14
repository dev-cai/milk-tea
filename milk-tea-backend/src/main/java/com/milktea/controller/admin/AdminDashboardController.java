package com.milktea.controller.admin;

import com.milktea.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 管理端仪表盘控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {
    
    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 今日关键指标
            stats.put("todayOrders", 128);
            stats.put("todaySales", new BigDecimal("3280.50"));
            stats.put("todayCustomerPrice", new BigDecimal("25.63"));
            stats.put("totalUsers", 1256);
            stats.put("pendingOrders", 15);
            stats.put("lowStockProducts", 8);
            
            // 实时数据
            stats.put("onlineUsers", 45);
            stats.put("todayNewUsers", 12);
            stats.put("completedOrders", 113);
            stats.put("refundOrders", 2);
            
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
            Map<String, Object> trendData = new HashMap<>();
            
            List<String> dates = new ArrayList<>();
            List<BigDecimal> sales = new ArrayList<>();
            List<Integer> orders = new ArrayList<>();
            
            // 模拟最近7天的数据
            String[] dateLabels = {"11-07", "11-08", "11-09", "11-10", "11-11", "11-12", "11-13"};
            BigDecimal[] salesData = {
                new BigDecimal("2850.00"), new BigDecimal("3120.50"), new BigDecimal("2980.00"),
                new BigDecimal("3450.80"), new BigDecimal("4200.00"), new BigDecimal("3680.30"),
                new BigDecimal("3280.50")
            };
            Integer[] ordersData = {95, 108, 102, 125, 156, 134, 128};
            
            Collections.addAll(dates, dateLabels);
            Collections.addAll(sales, salesData);
            Collections.addAll(orders, ordersData);
            
            trendData.put("dates", dates);
            trendData.put("sales", sales);
            trendData.put("orders", orders);
            
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
            Map<String, Object> rankingData = new HashMap<>();
            
            List<String> productNames = Arrays.asList(
                "珍珠奶茶", "芋泥奶茶", "芝士奶盖茶", "百香果茶", "波霸奶茶",
                "红豆奶茶", "椰果奶茶", "布丁奶茶", "仙草奶茶", "双拼奶茶"
            );
            
            List<Integer> salesCount = Arrays.asList(
                156, 145, 134, 112, 98, 87, 76, 65, 54, 43
            );
            
            List<BigDecimal> salesAmount = Arrays.asList(
                new BigDecimal("1872.00"), new BigDecimal("1740.00"), new BigDecimal("2412.00"),
                new BigDecimal("1792.00"), new BigDecimal("1274.00"), new BigDecimal("1218.00"),
                new BigDecimal("1064.00"), new BigDecimal("910.00"), new BigDecimal("756.00"),
                new BigDecimal("602.00")
            );
            
            // 限制返回数量
            int actualLimit = Math.min(limit, productNames.size());
            
            rankingData.put("names", productNames.subList(0, actualLimit));
            rankingData.put("salesCount", salesCount.subList(0, actualLimit));
            rankingData.put("salesAmount", salesAmount.subList(0, actualLimit));
            
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
            Map<String, Object> growthData = new HashMap<>();
            
            List<String> dates = new ArrayList<>();
            List<Integer> newUsers = new ArrayList<>();
            List<Integer> totalUsers = new ArrayList<>();
            
            // 模拟最近30天的用户增长数据
            int baseUsers = 1200;
            for (int i = 29; i >= 0; i--) {
                LocalDateTime date = LocalDateTime.now().minusDays(i);
                dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
                
                int dailyNew = (int) (Math.random() * 20) + 5; // 5-25个新用户
                newUsers.add(dailyNew);
                baseUsers += dailyNew;
                totalUsers.add(baseUsers);
            }
            
            growthData.put("dates", dates);
            growthData.put("newUsers", newUsers);
            growthData.put("totalUsers", totalUsers);
            
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
            List<Map<String, Object>> alerts = new ArrayList<>();
            
            // 库存预警
            Map<String, Object> stockAlert = new HashMap<>();
            stockAlert.put("type", "stock");
            stockAlert.put("level", "warning");
            stockAlert.put("title", "库存预警");
            stockAlert.put("message", "8个商品库存不足，请及时补货");
            stockAlert.put("count", 8);
            stockAlert.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            alerts.add(stockAlert);
            
            // 异常订单预警
            Map<String, Object> orderAlert = new HashMap<>();
            orderAlert.put("type", "order");
            orderAlert.put("level", "error");
            orderAlert.put("title", "异常订单");
            orderAlert.put("message", "2个订单超时未处理");
            orderAlert.put("count", 2);
            orderAlert.put("time", LocalDateTime.now().minusMinutes(15).format(DateTimeFormatter.ofPattern("HH:mm")));
            alerts.add(orderAlert);
            
            // 系统通知
            Map<String, Object> systemAlert = new HashMap<>();
            systemAlert.put("type", "system");
            systemAlert.put("level", "info");
            systemAlert.put("title", "系统通知");
            systemAlert.put("message", "今日营业额已突破3000元");
            systemAlert.put("count", 1);
            systemAlert.put("time", LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("HH:mm")));
            alerts.add(systemAlert);
            
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
            Map<String, Object> statusData = new HashMap<>();
            
            Map<String, Integer> statusCount = new HashMap<>();
            statusCount.put("pending", 15);      // 待支付
            statusCount.put("processing", 23);   // 制作中
            statusCount.put("ready", 8);         // 待取餐
            statusCount.put("completed", 113);   // 已完成
            statusCount.put("cancelled", 5);     // 已取消
            statusCount.put("refunded", 2);      // 已退款
            
            statusData.put("statusCount", statusCount);
            statusData.put("totalToday", 166);
            
            return Result.success(statusData);
        } catch (Exception e) {
            log.error("获取订单状态分布失败", e);
            return Result.error("获取订单状态失败");
        }
    }
}
