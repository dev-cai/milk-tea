package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.milktea.common.Result;
import com.milktea.entity.Order;
import com.milktea.entity.OrderItem;
import com.milktea.entity.User;
import com.milktea.mapper.OrderItemMapper;
import com.milktea.mapper.OrderMapper;
import com.milktea.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据分析服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class AnalysisService {
    
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    
    public AnalysisService(UserMapper userMapper, OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }
    
    /**
     * 获取会员分析概览数据
     */
    public Result<Map<String, Object>> getMemberOverview() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 获取所有已完成订单
            LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(Order::getStatus, 4); // 已完成
            List<Order> completedOrders = orderMapper.selectList(orderWrapper);
            
            if (completedOrders.isEmpty()) {
                data.put("avgConsumption", 0);
                data.put("avgFrequency", 0);
                data.put("totalRevenue", 0);
                data.put("retentionRate", 0);
                return Result.success("获取成功", data);
            }
            
            // 计算平均消费金额
            double avgConsumption = completedOrders.stream()
                    .mapToDouble(order -> order.getPayAmount().doubleValue())
                    .average()
                    .orElse(0.0);
            
            // 计算总收入
            double totalRevenue = completedOrders.stream()
                    .mapToDouble(order -> order.getPayAmount().doubleValue())
                    .sum();
            
            // 计算平均消费频次（每个用户的平均订单数）
            Map<Long, Long> userOrderCount = completedOrders.stream()
                    .collect(Collectors.groupingBy(Order::getUserId, Collectors.counting()));
            double avgFrequency = userOrderCount.values().stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            
            // 计算留存率（简化版：有重复购买的用户比例）
            long repeatUsers = userOrderCount.values().stream()
                    .filter(count -> count > 1)
                    .count();
            double retentionRate = userOrderCount.isEmpty() ? 0 : 
                    (double) repeatUsers / userOrderCount.size() * 100;
            
            data.put("avgConsumption", Math.round(avgConsumption * 100.0) / 100.0);
            data.put("avgFrequency", Math.round(avgFrequency * 100.0) / 100.0);
            data.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);
            data.put("retentionRate", Math.round(retentionRate * 100.0) / 100.0);
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取会员分析概览失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取销售趋势数据
     */
    public Result<Map<String, Object>> getSalesTrend(Integer days) {
        try {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> trend = new ArrayList<>();
            
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(days);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (int i = 0; i < days; i++) {
                LocalDateTime date = startDate.plusDays(i);
                LocalDateTime nextDate = date.plusDays(1);
                
                LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.ge(Order::getCreateTime, date)
                           .lt(Order::getCreateTime, nextDate)
                           .eq(Order::getStatus, 4); // 已完成
                
                List<Order> orders = orderMapper.selectList(queryWrapper);
                
                double sales = orders.stream()
                        .mapToDouble(order -> order.getPayAmount().doubleValue())
                        .sum();
                
                Map<String, Object> item = new HashMap<>();
                item.put("date", date.format(formatter));
                item.put("sales", Math.round(sales * 100.0) / 100.0);
                item.put("orders", orders.size());
                trend.add(item);
            }
            
            data.put("trend", trend);
            data.put("days", days);
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取销售趋势失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取商品销售排行
     */
    public Result<List<Map<String, Object>>> getProductRanking(Integer limit) {
        try {
            // 获取所有已完成订单的订单项
            LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(Order::getStatus, 4);
            List<Order> completedOrders = orderMapper.selectList(orderWrapper);
            
            if (completedOrders.isEmpty()) {
                return Result.success("获取成功", new ArrayList<>());
            }
            
            List<Long> orderIds = completedOrders.stream()
                    .map(Order::getId)
                    .collect(Collectors.toList());
            
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.in(OrderItem::getOrderId, orderIds);
            List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
            
            // 按商品ID分组统计
            Map<Long, Map<String, Object>> productStats = new HashMap<>();
            for (OrderItem item : orderItems) {
                Long productId = item.getProductId();
                productStats.putIfAbsent(productId, new HashMap<>());
                Map<String, Object> stats = productStats.get(productId);
                
                stats.put("productId", productId);
                stats.put("productName", item.getProductName());
                
                int quantity = (int) stats.getOrDefault("quantity", 0) + item.getQuantity();
                stats.put("quantity", quantity);
                
                double revenue = ((Number) stats.getOrDefault("revenue", 0.0)).doubleValue() + 
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue();
                stats.put("revenue", Math.round(revenue * 100.0) / 100.0);
            }
            
            // 按销量排序
            List<Map<String, Object>> ranking = new ArrayList<>(productStats.values());
            ranking.sort((a, b) -> Integer.compare((int) b.get("quantity"), (int) a.get("quantity")));
            
            // 限制返回数量
            if (limit != null && limit > 0 && ranking.size() > limit) {
                ranking = ranking.subList(0, limit);
            }
            
            return Result.success("获取成功", ranking);
        } catch (Exception e) {
            log.error("获取商品销售排行失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取会员消费分析
     */
    public Result<Map<String, Object>> getMemberConsumption(Integer days) {
        try {
            Map<String, Object> data = new HashMap<>();
            
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);
            
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.ge(Order::getCreateTime, startDate)
                       .eq(Order::getStatus, 4);
            List<Order> orders = orderMapper.selectList(queryWrapper);
            
            // 按会员等级分组统计
            Map<Integer, List<Order>> levelOrders = orders.stream()
                    .collect(Collectors.groupingBy(Order::getUserId))
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                        e -> {
                            User user = userMapper.selectById(e.getKey());
                            return user != null ? user.getMemberLevel() : 0;
                        },
                        Map.Entry::getValue,
                        (a, b) -> {
                            List<Order> combined = new ArrayList<>(a);
                            combined.addAll(b);
                            return combined;
                        }
                    ));
            
            List<Map<String, Object>> levelStats = new ArrayList<>();
            for (Map.Entry<Integer, List<Order>> entry : levelOrders.entrySet()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("level", entry.getKey());
                stat.put("orderCount", entry.getValue().size());
                double totalAmount = entry.getValue().stream()
                        .mapToDouble(o -> o.getPayAmount().doubleValue())
                        .sum();
                stat.put("totalAmount", Math.round(totalAmount * 100.0) / 100.0);
                stat.put("avgAmount", Math.round(totalAmount / entry.getValue().size() * 100.0) / 100.0);
                levelStats.add(stat);
            }
            
            data.put("levelStats", levelStats);
            data.put("days", days);
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取会员消费分析失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取会员等级分布
     */
    public Result<List<Map<String, Object>>> getMemberLevelDistribution() {
        try {
            Long totalUsers = userMapper.selectCount(null);
            
            LambdaQueryWrapper<User> level0 = new LambdaQueryWrapper<>();
            level0.eq(User::getMemberLevel, 0);
            Long normalCount = userMapper.selectCount(level0);
            
            LambdaQueryWrapper<User> level1 = new LambdaQueryWrapper<>();
            level1.eq(User::getMemberLevel, 1);
            Long goldCount = userMapper.selectCount(level1);
            
            LambdaQueryWrapper<User> level2 = new LambdaQueryWrapper<>();
            level2.eq(User::getMemberLevel, 2);
            Long diamondCount = userMapper.selectCount(level2);
            
            List<Map<String, Object>> distribution = new ArrayList<>();
            distribution.add(Map.of("level", 0, "name", "普通会员", "count", normalCount, "total", totalUsers));
            distribution.add(Map.of("level", 1, "name", "黄金会员", "count", goldCount, "total", totalUsers));
            distribution.add(Map.of("level", 2, "name", "钻石会员", "count", diamondCount, "total", totalUsers));
            
            return Result.success("获取成功", distribution);
        } catch (Exception e) {
            log.error("获取会员等级分布失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取会员行为分析
     */
    public Result<Map<String, Object>> getMemberBehavior() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getStatus, 4);
            List<Order> orders = orderMapper.selectList(queryWrapper);
            
            // 统计复购率
            Map<Long, Long> userOrderCount = orders.stream()
                    .collect(Collectors.groupingBy(Order::getUserId, Collectors.counting()));
            long repeatUsers = userOrderCount.values().stream().filter(c -> c > 1).count();
            double repeatRate = userOrderCount.isEmpty() ? 0 : 
                    (double) repeatUsers / userOrderCount.size() * 100;
            
            // 统计平均订单金额
            double avgOrderAmount = orders.stream()
                    .mapToDouble(o -> o.getPayAmount().doubleValue())
                    .average()
                    .orElse(0.0);
            
            data.put("repeatRate", Math.round(repeatRate * 100.0) / 100.0);
            data.put("avgOrderAmount", Math.round(avgOrderAmount * 100.0) / 100.0);
            data.put("totalOrders", orders.size());
            data.put("totalUsers", userOrderCount.size());
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取会员行为分析失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取会员活跃度趋势
     */
    public Result<Map<String, Object>> getMemberActivityTrend(Integer days) {
        try {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> trend = new ArrayList<>();
            
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(days);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (int i = 0; i < days; i++) {
                LocalDateTime date = startDate.plusDays(i);
                LocalDateTime nextDate = date.plusDays(1);
                
                LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.ge(Order::getCreateTime, date)
                           .lt(Order::getCreateTime, nextDate);
                
                List<Order> orders = orderMapper.selectList(queryWrapper);
                long activeUsers = orders.stream()
                        .map(Order::getUserId)
                        .distinct()
                        .count();
                
                Map<String, Object> item = new HashMap<>();
                item.put("date", date.format(formatter));
                item.put("activeUsers", activeUsers);
                item.put("orders", orders.size());
                trend.add(item);
            }
            
            data.put("trend", trend);
            data.put("days", days);
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取会员活跃度趋势失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取会员分群
     */
    public Result<List<Map<String, Object>>> getMemberSegmentation() {
        try {
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getStatus, 4);
            List<Order> orders = orderMapper.selectList(queryWrapper);
            
            // 按用户分组统计
            Map<Long, List<Order>> userOrders = orders.stream()
                    .collect(Collectors.groupingBy(Order::getUserId));
            
            int highValue = 0;  // 高价值用户（消费>500）
            int mediumValue = 0; // 中价值用户（100-500）
            int lowValue = 0;    // 低价值用户（<100）
            
            for (List<Order> userOrderList : userOrders.values()) {
                double totalAmount = userOrderList.stream()
                        .mapToDouble(o -> o.getPayAmount().doubleValue())
                        .sum();
                
                if (totalAmount > 500) {
                    highValue++;
                } else if (totalAmount > 100) {
                    mediumValue++;
                } else {
                    lowValue++;
                }
            }
            
            int totalUsers = userOrders.size();
            List<Map<String, Object>> segments = new ArrayList<>();
            segments.add(Map.of("segment", "高价值用户", "count", highValue, "threshold", ">500", "totalUsers", totalUsers));
            segments.add(Map.of("segment", "中价值用户", "count", mediumValue, "threshold", "100-500", "totalUsers", totalUsers));
            segments.add(Map.of("segment", "低价值用户", "count", lowValue, "threshold", "<100", "totalUsers", totalUsers));
            
            return Result.success("获取成功", segments);
        } catch (Exception e) {
            log.error("获取会员分群失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取VIP客户列表
     */
    public Result<List<Map<String, Object>>> getVipCustomers(Integer limit) {
        try {
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getStatus, 4);
            List<Order> orders = orderMapper.selectList(queryWrapper);
            
            // 按用户统计消费
            Map<Long, Double> userConsumption = orders.stream()
                    .collect(Collectors.groupingBy(
                        Order::getUserId,
                        Collectors.summingDouble(o -> o.getPayAmount().doubleValue())
                    ));
            
            // 排序并获取前N名
            List<Map<String, Object>> vipList = userConsumption.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .limit(limit != null ? limit : 10)
                    .map(entry -> {
                        User user = userMapper.selectById(entry.getKey());
                        Map<String, Object> vip = new HashMap<>();
                        vip.put("userId", entry.getKey());
                        vip.put("username", user != null ? user.getUsername() : "未知");
                        vip.put("nickname", user != null ? user.getNickname() : "未知");
                        vip.put("memberLevel", user != null ? user.getMemberLevel() : 0);
                        vip.put("totalConsumption", Math.round(entry.getValue() * 100.0) / 100.0);
                        
                        long orderCount = orders.stream()
                                .filter(o -> o.getUserId().equals(entry.getKey()))
                                .count();
                        vip.put("orderCount", orderCount);
                        
                        return vip;
                    })
                    .collect(Collectors.toList());
            
            return Result.success("获取成功", vipList);
        } catch (Exception e) {
            log.error("获取VIP客户列表失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取客户详情
     */
    public Result<Map<String, Object>> getCustomerDetail(Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            data.put("memberLevel", user.getMemberLevel());
            data.put("points", user.getPoints());
            data.put("balance", user.getBalance());
            
            // 统计订单信息
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getUserId, userId)
                       .eq(Order::getStatus, 4);
            List<Order> orders = orderMapper.selectList(queryWrapper);
            
            double totalConsumption = orders.stream()
                    .mapToDouble(o -> o.getPayAmount().doubleValue())
                    .sum();
            
            data.put("totalOrders", orders.size());
            data.put("totalConsumption", Math.round(totalConsumption * 100.0) / 100.0);
            data.put("avgOrderAmount", orders.isEmpty() ? 0 : 
                    Math.round(totalConsumption / orders.size() * 100.0) / 100.0);
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取客户详情失败", e);
            return Result.error("获取失败");
        }
    }
}
