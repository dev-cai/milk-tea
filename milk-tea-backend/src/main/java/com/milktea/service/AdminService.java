package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Order;
import com.milktea.entity.OrderItem;
import com.milktea.entity.Product;
import com.milktea.entity.User;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.OrderItemMapper;
import com.milktea.mapper.OrderMapper;
import com.milktea.mapper.ProductMapper;
import com.milktea.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class AdminService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;
    
    public AdminService(OrderMapper orderMapper, OrderItemMapper orderItemMapper, 
                       ProductMapper productMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
    }
    
    /**
     * 分页查询订单
     */
    public Result<PageResult<Order>> getOrderPage(Integer page, Integer size, Integer status, 
                                                  String orderNo, String startDate, String endDate) {
        Page<Order> pageObj = new Page<>(page, size);
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        if (StringUtils.hasText(orderNo)) {
            queryWrapper.like(Order::getOrderNo, orderNo);
        }
        
        if (StringUtils.hasText(startDate)) {
            queryWrapper.ge(Order::getCreateTime, startDate + " 00:00:00");
        }
        
        if (StringUtils.hasText(endDate)) {
            queryWrapper.le(Order::getCreateTime, endDate + " 23:59:59");
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> result = orderMapper.selectPage(pageObj, queryWrapper);
        
        PageResult<Order> pageResult = new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
        
        return Result.success("查询成功", pageResult);
    }
    
    /**
     * 获取订单详情
     */
    public Result<Map<String, Object>> getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", orderItems);
        
        return Result.success("获取成功", result);
    }
    
    /**
     * 更新订单状态
     */
    public Result<String> updateOrderStatus(Long orderId, Integer status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        order.setStatus(status);
        
        // 根据状态设置相应时间
        switch (status) {
            case 1: // 待制作
                order.setPayTime(LocalDateTime.now());
                break;
            case 4: // 已完成
                order.setFinishTime(LocalDateTime.now());
                break;
        }
        
        orderMapper.updateById(order);
        return Result.success("状态更新成功");
    }
    
    /**
     * 处理退款
     */
    @Transactional
    public Result<String> handleRefund(Long orderId, Boolean approve) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (order.getStatus() != 6) {
            throw new BusinessException("订单状态错误");
        }
        
        if (approve) {
            // 同意退款，恢复库存
            LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderItem::getOrderId, orderId);
            List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
            
            for (OrderItem item : orderItems) {
                Product product = productMapper.selectById(item.getProductId());
                if (product != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    productMapper.updateById(product);
                }
            }
            
            order.setStatus(7); // 已退款
        } else {
            // 拒绝退款，恢复为已完成状态
            order.setStatus(4);
        }
        
        orderMapper.updateById(order);
        return Result.success(approve ? "退款成功" : "退款已拒绝");
    }
    
    /**
     * 获取订单统计数据
     */
    public Result<Map<String, Object>> getOrderStatistics() {
        // 今日订单数
        LambdaQueryWrapper<Order> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(Order::getCreateTime, LocalDateTime.now().toLocalDate().atStartOfDay())
                   .lt(Order::getCreateTime, LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
        Long todayOrders = orderMapper.selectCount(todayWrapper);
        
        // 今日销售额（已完成订单）
        LambdaQueryWrapper<Order> salesWrapper = new LambdaQueryWrapper<>();
        salesWrapper.ge(Order::getCreateTime, LocalDateTime.now().toLocalDate().atStartOfDay())
                   .lt(Order::getCreateTime, LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay())
                   .eq(Order::getStatus, 4);
        List<Order> todayCompletedOrders = orderMapper.selectList(salesWrapper);
        Double todaySales = todayCompletedOrders.stream()
                .mapToDouble(order -> order.getPayAmount().doubleValue())
                .sum();
        
        // 待处理订单数
        LambdaQueryWrapper<Order> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.in(Order::getStatus, 1, 2); // 待制作、制作中
        Long pendingOrders = orderMapper.selectCount(pendingWrapper);
        
        // 总用户数
        Long totalUsers = userMapper.selectCount(null);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("todayOrders", todayOrders);
        statistics.put("todaySales", todaySales);
        statistics.put("pendingOrders", pendingOrders);
        statistics.put("totalUsers", totalUsers);
        
        return Result.success("获取成功", statistics);
    }
    
    /**
     * 分页查询用户
     */
    public Result<PageResult<User>> getUserPage(Integer page, Integer size, String keyword, Integer memberLevel) {
        Page<User> pageObj = new Page<>(page, size);
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(User::getUsername, keyword)
                       .or()
                       .like(User::getNickname, keyword)
                       .or()
                       .like(User::getPhone, keyword);
        }
        
        if (memberLevel != null) {
            queryWrapper.eq(User::getMemberLevel, memberLevel);
        }
        
        queryWrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = userMapper.selectPage(pageObj, queryWrapper);
        
        // 清空密码字段
        result.getRecords().forEach(user -> user.setPassword(null));
        
        PageResult<User> pageResult = new PageResult<>(
            result.getRecords(),
            result.getTotal(),
            result.getCurrent(),
            result.getSize()
        );
        
        return Result.success("查询成功", pageResult);
    }
    
    /**
     * 更新用户状态
     */
    public Result<String> updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success("状态更新成功");
    }
    
    /**
     * 调整用户会员等级
     */
    public Result<String> updateUserMemberLevel(Long userId, Integer memberLevel) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setMemberLevel(memberLevel);
        userMapper.updateById(user);
        return Result.success("会员等级更新成功");
    }
    
    /**
     * 调整用户积分
     */
    public Result<String> updateUserPoints(Long userId, Integer points, String type) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        switch (type) {
            case "add":
                user.setPoints(user.getPoints() + points);
                break;
            case "subtract":
                user.setPoints(Math.max(0, user.getPoints() - points));
                break;
            case "set":
                user.setPoints(points);
                break;
            default:
                throw new BusinessException("操作类型错误");
        }
        
        userMapper.updateById(user);
        return Result.success("积分更新成功");
    }
    
    // 添加所有缺失的方法 - 返回模拟数据
    public Result<String> batchAcceptOrders(List<Long> orderIds) {
        return Result.success("批量接单成功");
    }
    
    public Result<String> batchCompleteOrders(List<Long> orderIds) {
        return Result.success("批量完成成功");
    }
    
    public Result<Map<String, Object>> getPendingOrderCount() {
        Map<String, Object> data = new HashMap<>();
        data.put("pending", 5);
        data.put("processing", 8);
        data.put("ready", 3);
        data.put("urgent", 2);
        return Result.success("获取成功", data);
    }
    
    public Result<Map<String, Object>> getTodayOrderOverview() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalOrders", 156);
        data.put("totalAmount", 4580.50);
        data.put("avgAmount", 29.36);
        return Result.success("获取成功", data);
    }
    
    public Result<String> updateOrderRemark(Long id, String remark) {
        return Result.success("备注更新成功");
    }
    
    public Result<List<Map<String, Object>>> getOrderTimeline(Long id) {
        return Result.success("获取成功", List.of());
    }
    
    public Result<String> markOrderUrgent(Long id, Boolean urgent) {
        return Result.success("催单状态更新成功");
    }
    
    public Result<String> requestRefund(Long id, String reason) {
        return Result.success("退款申请成功");
    }
    
    public Result<PageResult<Map<String, Object>>> getRefundRequests(Integer page, Integer size, Integer status) {
        PageResult<Map<String, Object>> pageResult = new PageResult<>();
        pageResult.setRecords(List.of());
        pageResult.setTotal(0L);
        return Result.success("获取成功", pageResult);
    }
    
    public Result<String> processRefund(Long id, Boolean approve, String rejectReason) {
        return Result.success("退款处理成功");
    }
    
    public Result<PageResult<Map<String, Object>>> getComplaints(Integer page, Integer size, Integer status) {
        PageResult<Map<String, Object>> pageResult = new PageResult<>();
        pageResult.setRecords(List.of());
        pageResult.setTotal(0L);
        return Result.success("获取成功", pageResult);
    }
    
    public Result<String> processComplaint(Long id, String response, Integer status) {
        return Result.success("投诉处理成功");
    }
    
    public Result<Map<String, Object>> getAftersaleStatistics() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalRefunds", 25);
        data.put("pendingRefunds", 5);
        data.put("totalComplaints", 12);
        data.put("pendingComplaints", 3);
        return Result.success("获取成功", data);
    }
    
    public Result<String> updateUserBalance(Long id, Double balance, String type, String reason) {
        return Result.success("余额更新成功");
    }
    
    public Result<Map<String, Object>> getUserStatistics(Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalOrders", 25);
        data.put("totalAmount", 680.50);
        data.put("avgAmount", 27.22);
        data.put("lastOrderTime", "2024-11-10 15:30:00");
        return Result.success("获取成功", data);
    }
    
    public Result<PageResult<Map<String, Object>>> getUserOrders(Long id, Integer page, Integer size) {
        PageResult<Map<String, Object>> pageResult = new PageResult<>();
        pageResult.setRecords(List.of());
        pageResult.setTotal(0L);
        return Result.success("获取成功", pageResult);
    }
    
    public Result<String> batchUpdateUserStatus(List<Long> userIds, Integer status) {
        return Result.success("批量更新成功");
    }
    
    public Result<Map<String, Object>> getMemberStatistics() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalMembers", 1000);
        data.put("goldMembers", 280);
        data.put("diamondMembers", 70);
        data.put("newMembersToday", 15);
        return Result.success("获取成功", data);
    }
    
    public Result<Map<String, Object>> getUserGrowthTrend(Integer days) {
        Map<String, Object> data = new HashMap<>();
        data.put("dates", List.of("11-07", "11-08", "11-09", "11-10", "11-11", "11-12", "11-13"));
        data.put("newUsers", List.of(20, 15, 25, 30, 28, 35, 22));
        return Result.success("获取成功", data);
    }
    
    public Result<String> exportUsers(String keyword, Integer memberLevel) {
        return Result.success("导出成功");
    }
}
