package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.Order;
import com.milktea.entity.OrderItem;
import com.milktea.entity.Product;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.OrderItemMapper;
import com.milktea.mapper.OrderMapper;
import com.milktea.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端订单服务类
 * @author MilkTea Team
 */
@Slf4j
@Service
public class AdminOrderService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    
    public AdminOrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
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
        
        OrderStateMachine.assertTransition(order.getStatus(), status);
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
            
            OrderStateMachine.assertTransition(order.getStatus(), 7);
            if (orderMapper.updateStatusIfExpected(orderId, 6, 7) != 1) {
                throw new BusinessException("退款状态已变更，请刷新后重试");
            }
            for (OrderItem item : orderItems) productMapper.incrementStock(item.getProductId(), item.getQuantity());
        } else {
            // 拒绝退款，恢复为已完成状态
            if (orderMapper.updateStatusIfExpected(orderId, 6, 4) != 1) {
                throw new BusinessException("退款状态已变更，请刷新后重试");
            }
        }
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
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("todayOrders", todayOrders);
        statistics.put("todaySales", todaySales);
        statistics.put("pendingOrders", pendingOrders);
        statistics.put("totalUsers", 1256); // 示例数据
        
        return Result.success("获取成功", statistics);
    }
}
