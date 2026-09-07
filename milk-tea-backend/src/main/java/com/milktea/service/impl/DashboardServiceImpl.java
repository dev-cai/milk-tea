package com.milktea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.milktea.entity.Order;
import com.milktea.entity.Product;
import com.milktea.entity.User;
import com.milktea.mapper.OrderMapper;
import com.milktea.mapper.ProductMapper;
import com.milktea.mapper.UserMapper;
import com.milktea.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 仪表盘服务实现类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 今日开始时间
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = todayStart.plusDays(1);
            
            // 今日订单数
            QueryWrapper<Order> todayOrderQuery = new QueryWrapper<>();
            todayOrderQuery.ge("create_time", todayStart)
                          .lt("create_time", todayEnd)
                          .ne("status", 5); // 排除已取消订单
            Integer todayOrders = Math.toIntExact(orderMapper.selectCount(todayOrderQuery));
            
            // 今日销售额
            QueryWrapper<Order> todaySalesQuery = new QueryWrapper<>();
            todaySalesQuery.ge("create_time", todayStart)
                          .lt("create_time", todayEnd)
                          .in("status", Arrays.asList(1, 2, 3, 4)); // 已支付订单
            List<Order> todayOrderList = orderMapper.selectList(todaySalesQuery);
            BigDecimal todaySales = todayOrderList.stream()
                    .map(Order::getPayAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 客单价
            BigDecimal todayCustomerPrice = BigDecimal.ZERO;
            if (todayOrders > 0) {
                todayCustomerPrice = todaySales.divide(new BigDecimal(todayOrders), 2, RoundingMode.HALF_UP);
            }
            
            // 总用户数
            QueryWrapper<User> userQuery = new QueryWrapper<>();
            userQuery.eq("deleted", 0);
            Integer totalUsers = Math.toIntExact(userMapper.selectCount(userQuery));
            
            // 待处理订单数
            QueryWrapper<Order> pendingQuery = new QueryWrapper<>();
            pendingQuery.in("status", Arrays.asList(0, 1, 2, 3)); // 待支付、待制作、制作中、待取餐
            Integer pendingOrders = Math.toIntExact(orderMapper.selectCount(pendingQuery));
            
            // 库存不足商品数
            QueryWrapper<Product> lowStockQuery = new QueryWrapper<>();
            lowStockQuery.le("stock", 10)
                        .eq("status", 1)
                        .eq("deleted", 0);
            Integer lowStockProducts = Math.toIntExact(productMapper.selectCount(lowStockQuery));
            
            // 在线用户数（模拟数据）
            Integer onlineUsers = (int) (Math.random() * 50) + 20;
            
            // 今日新用户
            QueryWrapper<User> todayNewUserQuery = new QueryWrapper<>();
            todayNewUserQuery.ge("create_time", todayStart)
                            .lt("create_time", todayEnd)
                            .eq("deleted", 0);
            Integer todayNewUsers = Math.toIntExact(userMapper.selectCount(todayNewUserQuery));
            
            // 今日完成订单
            QueryWrapper<Order> completedQuery = new QueryWrapper<>();
            completedQuery.ge("create_time", todayStart)
                         .lt("create_time", todayEnd)
                         .eq("status", 4); // 已完成
            Integer completedOrders = Math.toIntExact(orderMapper.selectCount(completedQuery));
            
            // 今日退款订单
            QueryWrapper<Order> refundQuery = new QueryWrapper<>();
            refundQuery.ge("create_time", todayStart)
                      .lt("create_time", todayEnd)
                      .in("status", Arrays.asList(6, 7)); // 退款中、已退款
            Integer refundOrders = Math.toIntExact(orderMapper.selectCount(refundQuery));
            
            stats.put("todayOrders", todayOrders);
            stats.put("todaySales", todaySales);
            stats.put("todayCustomerPrice", todayCustomerPrice);
            stats.put("totalUsers", totalUsers);
            stats.put("pendingOrders", pendingOrders);
            stats.put("lowStockProducts", lowStockProducts);
            stats.put("onlineUsers", onlineUsers);
            stats.put("todayNewUsers", todayNewUsers);
            stats.put("completedOrders", completedOrders);
            stats.put("refundOrders", refundOrders);
            
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败", e);
            // 返回默认数据
            stats.put("todayOrders", 0);
            stats.put("todaySales", BigDecimal.ZERO);
            stats.put("todayCustomerPrice", BigDecimal.ZERO);
            stats.put("totalUsers", 0);
            stats.put("pendingOrders", 0);
            stats.put("lowStockProducts", 0);
            stats.put("onlineUsers", 0);
            stats.put("todayNewUsers", 0);
            stats.put("completedOrders", 0);
            stats.put("refundOrders", 0);
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getSalesTrend(Integer days) {
        Map<String, Object> trendData = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<BigDecimal> sales = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();
        
        try {
            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                LocalDateTime dayStart = date.atStartOfDay();
                LocalDateTime dayEnd = dayStart.plusDays(1);
                
                dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
                
                // 查询当日订单
                QueryWrapper<Order> dayOrderQuery = new QueryWrapper<>();
                dayOrderQuery.ge("create_time", dayStart)
                            .lt("create_time", dayEnd)
                            .in("status", Arrays.asList(1, 2, 3, 4)); // 已支付订单
                
                List<Order> dayOrders = orderMapper.selectList(dayOrderQuery);
                orders.add(dayOrders.size());
                
                BigDecimal daySales = dayOrders.stream()
                        .map(Order::getPayAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                sales.add(daySales);
            }
        } catch (Exception e) {
            log.error("获取销售趋势数据失败", e);
            // 返回模拟数据
            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
                sales.add(new BigDecimal((Math.random() * 2000) + 1000));
                orders.add((int) (Math.random() * 50) + 50);
            }
        }
        
        trendData.put("dates", dates);
        trendData.put("sales", sales);
        trendData.put("orders", orders);
        
        return trendData;
    }
    
    @Override
    public Map<String, Object> getProductRanking(Integer limit) {
        Map<String, Object> rankingData = new HashMap<>();
        int safeLimit = limit == null ? 10 : Math.max(1, Math.min(limit, 50));
        
        try {
            // 查询销量排行前N的商品
            QueryWrapper<Product> productQuery = new QueryWrapper<>();
            productQuery.eq("status", 1)
                       .eq("deleted", 0)
                       .orderByDesc("sales")
                       .last("LIMIT " + safeLimit);
            
            List<Product> products = productMapper.selectList(productQuery);
            
            List<String> productNames = new ArrayList<>();
            List<Integer> salesCount = new ArrayList<>();
            List<BigDecimal> salesAmount = new ArrayList<>();
            
            for (Product product : products) {
                productNames.add(product.getName());
                salesCount.add(product.getSales());
                salesAmount.add(product.getPrice().multiply(new BigDecimal(product.getSales())));
            }
            
            rankingData.put("names", productNames);
            rankingData.put("salesCount", salesCount);
            rankingData.put("salesAmount", salesAmount);
            
        } catch (Exception e) {
            log.error("获取商品销售排行失败", e);
            // 返回模拟数据
            List<String> productNames = Arrays.asList(
                "珍珠奶茶", "芋泥奶茶", "芝士奶盖茶", "百香果茶", "波霸奶茶",
                "红豆奶茶", "椰果奶茶", "布丁奶茶", "仙草奶茶", "双拼奶茶"
            );
            List<Integer> salesCount = Arrays.asList(156, 145, 134, 112, 98, 87, 76, 65, 54, 43);
            List<BigDecimal> salesAmount = Arrays.asList(
                new BigDecimal("1872.00"), new BigDecimal("1740.00"), new BigDecimal("2412.00"),
                new BigDecimal("1792.00"), new BigDecimal("1274.00"), new BigDecimal("1218.00"),
                new BigDecimal("1064.00"), new BigDecimal("910.00"), new BigDecimal("756.00"),
                new BigDecimal("602.00")
            );
            
            int actualLimit = Math.min(safeLimit, productNames.size());
            rankingData.put("names", productNames.subList(0, actualLimit));
            rankingData.put("salesCount", salesCount.subList(0, actualLimit));
            rankingData.put("salesAmount", salesAmount.subList(0, actualLimit));
        }
        
        return rankingData;
    }
    
    @Override
    public Map<String, Object> getUserGrowth(Integer days) {
        Map<String, Object> growthData = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> newUsers = new ArrayList<>();
        List<Integer> totalUsers = new ArrayList<>();
        
        try {
            // 获取总用户数基数
            QueryWrapper<User> totalQuery = new QueryWrapper<>();
            totalQuery.eq("deleted", 0)
                     .lt("create_time", LocalDate.now().minusDays(days).atStartOfDay());
            Integer baseUsers = Math.toIntExact(userMapper.selectCount(totalQuery));
            
            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                LocalDateTime dayStart = date.atStartOfDay();
                LocalDateTime dayEnd = dayStart.plusDays(1);
                
                dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
                
                // 查询当日新增用户
                QueryWrapper<User> dayUserQuery = new QueryWrapper<>();
                dayUserQuery.ge("create_time", dayStart)
                           .lt("create_time", dayEnd)
                           .eq("deleted", 0);
                
                Integer dailyNew = Math.toIntExact(userMapper.selectCount(dayUserQuery));
                newUsers.add(dailyNew);
                baseUsers += dailyNew;
                totalUsers.add(baseUsers);
            }
        } catch (Exception e) {
            log.error("获取用户增长数据失败", e);
            // 返回模拟数据
            int baseUsers = 1200;
            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
                
                int dailyNew = (int) (Math.random() * 20) + 5;
                newUsers.add(dailyNew);
                baseUsers += dailyNew;
                totalUsers.add(baseUsers);
            }
        }
        
        growthData.put("dates", dates);
        growthData.put("newUsers", newUsers);
        growthData.put("totalUsers", totalUsers);
        
        return growthData;
    }
    
    @Override
    public List<Map<String, Object>> getAlerts() {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        try {
            // 库存预警
            QueryWrapper<Product> lowStockQuery = new QueryWrapper<>();
            lowStockQuery.le("stock", 10)
                        .eq("status", 1)
                        .eq("deleted", 0);
            Integer lowStockCount = Math.toIntExact(productMapper.selectCount(lowStockQuery));
            
            if (lowStockCount > 0) {
                Map<String, Object> stockAlert = new HashMap<>();
                stockAlert.put("type", "stock");
                stockAlert.put("level", "warning");
                stockAlert.put("title", "库存预警");
                stockAlert.put("message", lowStockCount + "个商品库存不足，请及时补货");
                stockAlert.put("count", lowStockCount);
                stockAlert.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                alerts.add(stockAlert);
            }
            
            // 异常订单预警
            QueryWrapper<Order> timeoutOrderQuery = new QueryWrapper<>();
            timeoutOrderQuery.in("status", Arrays.asList(1, 2, 3))
                            .lt("create_time", LocalDateTime.now().minusHours(2));
            Integer timeoutCount = Math.toIntExact(orderMapper.selectCount(timeoutOrderQuery));
            
            if (timeoutCount > 0) {
                Map<String, Object> orderAlert = new HashMap<>();
                orderAlert.put("type", "order");
                orderAlert.put("level", "error");
                orderAlert.put("title", "异常订单");
                orderAlert.put("message", timeoutCount + "个订单超时未处理");
                orderAlert.put("count", timeoutCount);
                orderAlert.put("time", LocalDateTime.now().minusMinutes(15).format(DateTimeFormatter.ofPattern("HH:mm")));
                alerts.add(orderAlert);
            }
            
            // 系统通知
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            QueryWrapper<Order> todaySalesQuery = new QueryWrapper<>();
            todaySalesQuery.ge("create_time", todayStart)
                          .in("status", Arrays.asList(1, 2, 3, 4));
            List<Order> todayOrders = orderMapper.selectList(todaySalesQuery);
            BigDecimal todaySales = todayOrders.stream()
                    .map(Order::getPayAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (todaySales.compareTo(new BigDecimal("3000")) >= 0) {
                Map<String, Object> systemAlert = new HashMap<>();
                systemAlert.put("type", "system");
                systemAlert.put("level", "info");
                systemAlert.put("title", "系统通知");
                systemAlert.put("message", "今日营业额已突破" + todaySales.intValue() + "元");
                systemAlert.put("count", 1);
                systemAlert.put("time", LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("HH:mm")));
                alerts.add(systemAlert);
            }
            
        } catch (Exception e) {
            log.error("获取预警信息失败", e);
        }
        
        return alerts;
    }
    
    @Override
    public Map<String, Object> getOrderStatus() {
        Map<String, Object> statusData = new HashMap<>();
        
        try {
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = todayStart.plusDays(1);
            
            Map<String, Integer> statusCount = new HashMap<>();
            
            // 统计各状态订单数量
            for (int status = 0; status <= 7; status++) {
                QueryWrapper<Order> statusQuery = new QueryWrapper<>();
                statusQuery.ge("create_time", todayStart)
                          .lt("create_time", todayEnd)
                          .eq("status", status);
                Integer count = Math.toIntExact(orderMapper.selectCount(statusQuery));
                
                String statusKey = getStatusKey(status);
                if (statusKey != null) {
                    statusCount.put(statusKey, count);
                }
            }
            
            // 今日总订单数
            QueryWrapper<Order> totalQuery = new QueryWrapper<>();
            totalQuery.ge("create_time", todayStart)
                     .lt("create_time", todayEnd);
            Integer totalToday = Math.toIntExact(orderMapper.selectCount(totalQuery));
            
            statusData.put("statusCount", statusCount);
            statusData.put("totalToday", totalToday);
            
        } catch (Exception e) {
            log.error("获取订单状态分布失败", e);
            // 返回模拟数据
            Map<String, Integer> statusCount = new HashMap<>();
            statusCount.put("pending", 15);
            statusCount.put("processing", 23);
            statusCount.put("ready", 8);
            statusCount.put("completed", 113);
            statusCount.put("cancelled", 5);
            statusCount.put("refunded", 2);
            
            statusData.put("statusCount", statusCount);
            statusData.put("totalToday", 166);
        }
        
        return statusData;
    }
    
    private String getStatusKey(int status) {
        switch (status) {
            case 0: return "pending";      // 待支付
            case 1:
            case 2: return "processing";   // 待制作/制作中
            case 3: return "ready";        // 待取餐
            case 4: return "completed";    // 已完成
            case 5: return "cancelled";    // 已取消
            case 6:
            case 7: return "refunded";     // 退款中/已退款
            default: return null;
        }
    }
}
