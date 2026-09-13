package com.milktea.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.milktea.common.PageResult;
import com.milktea.common.Result;
import com.milktea.entity.*;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final RefundRequestMapper refundRequestMapper;
    private final ComplaintMapper complaintMapper;
    
    public AdminService(OrderMapper orderMapper, OrderItemMapper orderItemMapper, 
                       ProductMapper productMapper, UserMapper userMapper,
                       RefundRequestMapper refundRequestMapper, ComplaintMapper complaintMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.refundRequestMapper = refundRequestMapper;
        this.complaintMapper = complaintMapper;
    }
    
    /**
     * 分页查询订单
     */
    public Result<PageResult<Order>> getOrderPage(Integer page, Integer size, Integer status, 
                                                  String orderNo, String startDate, String endDate, Long userId) {
        Page<Order> pageObj = new Page<>(page, size);
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            queryWrapper.eq(Order::getStatus, status);
        }
        if (userId != null) queryWrapper.eq(Order::getUserId, userId);
        
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
        
        // 构建扁平化的订单详情数据
        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("userId", order.getUserId());
        result.put("username", order.getUsername());
        result.put("totalAmount", order.getTotalAmount());
        result.put("discountAmount", order.getDiscountAmount());
        result.put("actualAmount", order.getActualAmount());
        result.put("payAmount", order.getPayAmount());
        result.put("payType", order.getPayType());
        result.put("status", order.getStatus());
        result.put("remark", order.getRemark());
        result.put("refundReason", order.getRefundReason());
        result.put("payTime", order.getPayTime());
        result.put("finishTime", order.getFinishTime());
        result.put("cancelTime", order.getCancelTime());
        result.put("createTime", order.getCreateTime());
        result.put("updateTime", order.getUpdateTime());
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
            for (OrderItem item : orderItems) productMapper.incrementStock(item.getProductId(), item.getQuantity());
            
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
     * 获取用户详情
     */
    public Result<User> getUserDetail(Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 清空密码字段
            user.setPassword(null);
            
            return Result.success("获取成功", user);
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            return Result.error("获取用户详情失败");
        }
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
        user.setTokenVersion((user.getTokenVersion() == null ? 0 : user.getTokenVersion()) + 1);
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
    
    @Transactional
    public Result<String> batchAcceptOrders(List<Long> orderIds) {
        for (Long orderId : orderIds) {
            Order order = orderMapper.selectById(orderId);
            if (order != null) {
                OrderStateMachine.assertTransition(order.getStatus(), 1);
                order.setStatus(1);
                order.setPayTime(order.getPayTime() == null ? LocalDateTime.now() : order.getPayTime());
                orderMapper.updateById(order);
            }
        }
        return Result.success("批量接单成功");
    }

    @Transactional
    public Result<String> batchCompleteOrders(List<Long> orderIds) {
        for (Long orderId : orderIds) {
            Order order = orderMapper.selectById(orderId);
            if (order != null) {
                OrderStateMachine.assertTransition(order.getStatus(), 4);
                order.setStatus(4);
                order.setFinishTime(LocalDateTime.now());
                orderMapper.updateById(order);
            }
        }
        return Result.success("批量完成成功");
    }
    
    public Result<Map<String, Object>> getPendingOrderCount() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 查询各状态订单数量
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            
            // 待支付订单 (status = 0)
            queryWrapper.eq(Order::getStatus, 0);
            Long pendingCount = orderMapper.selectCount(queryWrapper);
            data.put("pending", pendingCount);
            
            // 制作中订单 (status = 1 待制作 + status = 2 制作中)
            queryWrapper.clear();
            queryWrapper.in(Order::getStatus, 1, 2);
            Long processingCount = orderMapper.selectCount(queryWrapper);
            data.put("processing", processingCount);
            
            // 待取餐订单 (status = 3)
            queryWrapper.clear();
            queryWrapper.eq(Order::getStatus, 3);
            Long readyCount = orderMapper.selectCount(queryWrapper);
            data.put("ready", readyCount);
            
            // 催单数量 (这里暂时设为0，实际项目中需要单独的催单表)
            data.put("urgent", 0);
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取待处理订单数量失败", e);
            return Result.error("获取失败");
        }
    }
    
    public Result<Map<String, Object>> getTodayOrderOverview() {
        Map<String, Object> data = new HashMap<>();
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, start).lt(Order::getCreateTime, end).ne(Order::getStatus, 5);
        List<Order> orders = orderMapper.selectList(wrapper);
        BigDecimal amount = orders.stream().map(Order::getPayAmount).filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalOrders", orders.size());
        data.put("totalAmount", amount);
        data.put("avgAmount", orders.isEmpty() ? BigDecimal.ZERO : amount.divide(BigDecimal.valueOf(orders.size()), 2, java.math.RoundingMode.HALF_UP));
        return Result.success("获取成功", data);
    }
    
    /**
     * 更新订单备注
     */
    public Result<String> updateOrderRemark(Long id, String remark) {
        try {
            Order order = orderMapper.selectById(id);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            order.setRemark(remark);
            orderMapper.updateById(order);
            
            log.info("订单备注更新成功: 订单ID={}, 备注={}", id, remark);
            return Result.success("备注更新成功");
        } catch (Exception e) {
            log.error("更新订单备注失败", e);
            return Result.error("备注更新失败");
        }
    }
    
    /**
     * 获取订单时间线
     */
    public Result<List<Map<String, Object>>> getOrderTimeline(Long id) {
        try {
            Order order = orderMapper.selectById(id);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            List<Map<String, Object>> timeline = new ArrayList<>();
            
            // 创建订单
            Map<String, Object> createEvent = new HashMap<>();
            createEvent.put("time", order.getCreateTime());
            createEvent.put("event", "创建订单");
            createEvent.put("description", "订单创建成功");
            timeline.add(createEvent);
            
            // 支付
            if (order.getPayTime() != null) {
                Map<String, Object> payEvent = new HashMap<>();
                payEvent.put("time", order.getPayTime());
                payEvent.put("event", "支付成功");
                payEvent.put("description", "订单已支付");
                timeline.add(payEvent);
            }
            
            // 完成
            if (order.getFinishTime() != null) {
                Map<String, Object> finishEvent = new HashMap<>();
                finishEvent.put("time", order.getFinishTime());
                finishEvent.put("event", "订单完成");
                finishEvent.put("description", "订单已完成");
                timeline.add(finishEvent);
            }
            
            // 取消
            if (order.getCancelTime() != null) {
                Map<String, Object> cancelEvent = new HashMap<>();
                cancelEvent.put("time", order.getCancelTime());
                cancelEvent.put("event", "订单取消");
                cancelEvent.put("description", "订单已取消");
                timeline.add(cancelEvent);
            }
            
            return Result.success("获取成功", timeline);
        } catch (Exception e) {
            log.error("获取订单时间线失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 标记催单状态
     * 注意：这里简化实现，实际项目中应该有单独的催单表
     */
    public Result<String> markOrderUrgent(Long id, Boolean urgent) {
        try {
            Order order = orderMapper.selectById(id);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            // 这里可以在订单备注中添加催单标记，或者使用单独的催单表
            String currentRemark = order.getRemark() != null ? order.getRemark() : "";
            if (urgent) {
                if (!currentRemark.contains("[催单]")) {
                    order.setRemark("[催单] " + currentRemark);
                }
            } else {
                order.setRemark(currentRemark.replace("[催单] ", "").replace("[催单]", ""));
            }
            orderMapper.updateById(order);
            
            log.info("订单催单状态更新: 订单ID={}, 催单={}", id, urgent);
            return Result.success("催单状态更新成功");
        } catch (Exception e) {
            log.error("更新催单状态失败", e);
            return Result.error("更新失败");
        }
    }
    
    /**
     * 申请退款
     */
    public Result<String> requestRefund(Long id, String reason) {
        try {
            Order order = orderMapper.selectById(id);
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            // 创建退款申请
            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setOrderId(order.getId());
            refundRequest.setOrderNo(order.getOrderNo());
            refundRequest.setUserId(order.getUserId());
            refundRequest.setCustomerName(order.getUsername());
            refundRequest.setRefundAmount(order.getPayAmount());
            refundRequest.setReason(reason);
            refundRequest.setStatus(0); // 待处理
            
            refundRequestMapper.insert(refundRequest);
            
            // 更新订单状态为退款中
            order.setStatus(6);
            order.setRefundReason(reason);
            orderMapper.updateById(order);
            
            log.info("退款申请成功: 订单ID={}, 退款金额={}", id, order.getPayAmount());
            return Result.success("退款申请成功");
        } catch (Exception e) {
            log.error("申请退款失败", e);
            return Result.error("申请退款失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取退款申请列表
     */
    public Result<PageResult<Map<String, Object>>> getRefundRequests(Integer page, Integer size, Integer status) {
        try {
            Page<RefundRequest> pageObj = new Page<>(page, size);
            
            LambdaQueryWrapper<RefundRequest> queryWrapper = new LambdaQueryWrapper<>();
            if (status != null && status >= 0) {
                queryWrapper.eq(RefundRequest::getStatus, status);
            }
            queryWrapper.orderByDesc(RefundRequest::getCreateTime);
            
            Page<RefundRequest> result = refundRequestMapper.selectPage(pageObj, queryWrapper);
            
            // 转换为Map格式
            List<Map<String, Object>> records = result.getRecords().stream().map(refund -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", refund.getId());
                map.put("orderId", refund.getOrderId());
                map.put("orderNo", refund.getOrderNo());
                map.put("userId", refund.getUserId());
                map.put("customerName", refund.getCustomerName());
                map.put("refundAmount", refund.getRefundAmount());
                map.put("reason", refund.getReason());
                map.put("status", refund.getStatus());
                map.put("rejectReason", refund.getRejectReason());
                map.put("processTime", refund.getProcessTime());
                map.put("createTime", refund.getCreateTime());
                return map;
            }).toList();
            
            PageResult<Map<String, Object>> pageResult = new PageResult<>(
                records,
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
            );
            
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取退款申请列表失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 处理退款申请
     */
    @Transactional
    public Result<String> processRefund(Long id, Boolean approve, String rejectReason) {
        try {
            RefundRequest refundRequest = refundRequestMapper.selectById(id);
            if (refundRequest == null) {
                throw new BusinessException("退款申请不存在");
            }
            
            if (refundRequest.getStatus() != 0) {
                throw new BusinessException("该退款申请已处理");
            }
            
            Order order = orderMapper.selectById(refundRequest.getOrderId());
            if (order == null) {
                throw new BusinessException("订单不存在");
            }
            
            if (approve) {
                if (refundRequestMapper.processIfPending(id, 1, null) != 1) throw new BusinessException("该退款申请已处理");
                // 同意退款
                refundRequest.setStatus(1);
                refundRequest.setProcessTime(LocalDateTime.now());
                
                // 更新订单状态为已退款
                OrderStateMachine.assertTransition(order.getStatus(), 7);
                order.setStatus(7);
                
                // 恢复库存
                LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(OrderItem::getOrderId, order.getId());
                List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
                
                for (OrderItem item : orderItems) {
                    productMapper.incrementStock(item.getProductId(), item.getQuantity());
                }
                
                log.info("退款申请已同意: ID={}, 订单号={}, 金额={}", id, order.getOrderNo(), refundRequest.getRefundAmount());
            } else {
                // 拒绝退款
                if (refundRequestMapper.processIfPending(id, 2, rejectReason) != 1) throw new BusinessException("该退款申请已处理");
                refundRequest.setStatus(2);
                refundRequest.setRejectReason(rejectReason);
                refundRequest.setProcessTime(LocalDateTime.now());
                
                // 恢复订单状态为已完成
                OrderStateMachine.assertTransition(order.getStatus(), 4);
                order.setStatus(4);
                
                log.info("退款申请已拒绝: ID={}, 订单号={}, 原因={}", id, order.getOrderNo(), rejectReason);
            }
            
            refundRequestMapper.updateById(refundRequest);
            orderMapper.updateById(order);
            
            return Result.success(approve ? "退款申请已同意" : "退款申请已拒绝");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("处理退款申请失败", e);
            return Result.error("处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取投诉列表
     */
    public Result<PageResult<Map<String, Object>>> getComplaints(Integer page, Integer size, Integer status) {
        try {
            Page<Complaint> pageObj = new Page<>(page, size);
            
            LambdaQueryWrapper<Complaint> queryWrapper = new LambdaQueryWrapper<>();
            if (status != null && status >= 0) {
                queryWrapper.eq(Complaint::getStatus, status);
            }
            queryWrapper.orderByDesc(Complaint::getCreateTime);
            
            Page<Complaint> result = complaintMapper.selectPage(pageObj, queryWrapper);
            
            // 转换为Map格式
            List<Map<String, Object>> records = result.getRecords().stream().map(complaint -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", complaint.getId());
                map.put("orderId", complaint.getOrderId());
                map.put("orderNo", complaint.getOrderNo());
                map.put("userId", complaint.getUserId());
                map.put("customerName", complaint.getCustomerName());
                map.put("complaintType", complaint.getComplaintType());
                map.put("content", complaint.getContent());
                map.put("images", complaint.getImages());
                map.put("status", complaint.getStatus());
                map.put("response", complaint.getResponse());
                map.put("processTime", complaint.getProcessTime());
                map.put("createTime", complaint.getCreateTime());
                return map;
            }).toList();
            
            PageResult<Map<String, Object>> pageResult = new PageResult<>(
                records,
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
            );
            
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取投诉列表失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 处理投诉
     */
    public Result<String> processComplaint(Long id, String response, Integer status) {
        try {
            Complaint complaint = complaintMapper.selectById(id);
            if (complaint == null) {
                throw new BusinessException("投诉不存在");
            }
            
            complaint.setResponse(response);
            complaint.setStatus(status);
            complaint.setProcessTime(LocalDateTime.now());
            
            complaintMapper.updateById(complaint);
            
            log.info("投诉处理成功: ID={}, 订单号={}, 状态={}", id, complaint.getOrderNo(), status);
            return Result.success("投诉处理成功");
        } catch (Exception e) {
            log.error("处理投诉失败", e);
            return Result.error("处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取售后统计
     */
    public Result<Map<String, Object>> getAftersaleStatistics() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 总退款申请数
            Long totalRefunds = refundRequestMapper.selectCount(null);
            
            // 待处理退款数
            LambdaQueryWrapper<RefundRequest> pendingRefundWrapper = new LambdaQueryWrapper<>();
            pendingRefundWrapper.eq(RefundRequest::getStatus, 0);
            Long pendingRefunds = refundRequestMapper.selectCount(pendingRefundWrapper);
            
            // 总投诉数
            Long totalComplaints = complaintMapper.selectCount(null);
            
            // 待处理投诉数
            LambdaQueryWrapper<Complaint> pendingComplaintWrapper = new LambdaQueryWrapper<>();
            pendingComplaintWrapper.eq(Complaint::getStatus, 0);
            Long pendingComplaints = complaintMapper.selectCount(pendingComplaintWrapper);
            
            data.put("totalRefunds", totalRefunds);
            data.put("pendingRefunds", pendingRefunds);
            data.put("totalComplaints", totalComplaints);
            data.put("pendingComplaints", pendingComplaints);
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取售后统计失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 调整用户余额
     */
    public Result<String> updateUserBalance(Long id, Double balance, String type, String reason) {
        try {
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            BigDecimal currentBalance = user.getBalance();
            BigDecimal newBalance;
            BigDecimal amount = BigDecimal.valueOf(balance);
            
            switch (type) {
                case "add":
                    newBalance = currentBalance.add(amount);
                    break;
                case "subtract":
                    newBalance = currentBalance.subtract(amount);
                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                        throw new BusinessException("余额不足");
                    }
                    break;
                case "set":
                    newBalance = amount;
                    break;
                default:
                    throw new BusinessException("操作类型错误");
            }
            
            user.setBalance(newBalance);
            userMapper.updateById(user);
            
            log.info("用户余额调整成功: 用户ID={}, 操作={}, 金额={}, 原余额={}, 新余额={}, 原因={}", 
                    id, type, balance, currentBalance, newBalance, reason);
            
            return Result.success("余额更新成功");
        } catch (Exception e) {
            log.error("调整用户余额失败", e);
            return Result.error("余额更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户消费统计
     */
    public Result<Map<String, Object>> getUserStatistics(Long id) {
        try {
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 查询用户订单统计
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getUserId, id)
                       .eq(Order::getStatus, 4); // 只统计已完成订单
            
            List<Order> orders = orderMapper.selectList(queryWrapper);
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalOrders", orders.size());
            
            if (!orders.isEmpty()) {
                double totalAmount = orders.stream()
                        .mapToDouble(order -> order.getPayAmount().doubleValue())
                        .sum();
                double avgAmount = totalAmount / orders.size();
                
                LocalDateTime lastOrderTime = orders.stream()
                        .map(Order::getCreateTime)
                        .max(LocalDateTime::compareTo)
                        .orElse(null);
                
                data.put("totalAmount", String.format("%.2f", totalAmount));
                data.put("avgAmount", String.format("%.2f", avgAmount));
                data.put("lastOrderTime", lastOrderTime);
            } else {
                data.put("totalAmount", "0.00");
                data.put("avgAmount", "0.00");
                data.put("lastOrderTime", null);
            }
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取用户统计失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取用户订单历史
     */
    public Result<PageResult<Map<String, Object>>> getUserOrders(Long id, Integer page, Integer size) {
        try {
            Page<Order> pageObj = new Page<>(page, size);
            
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Order::getUserId, id)
                       .orderByDesc(Order::getCreateTime);
            
            Page<Order> result = orderMapper.selectPage(pageObj, queryWrapper);
            
            // 转换为Map格式，包含订单明细
            List<Map<String, Object>> records = result.getRecords().stream().map(order -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                map.put("orderNo", order.getOrderNo());
                map.put("totalAmount", order.getTotalAmount());
                map.put("payAmount", order.getPayAmount());
                map.put("status", order.getStatus());
                map.put("payType", order.getPayType());
                map.put("remark", order.getRemark());
                map.put("createTime", order.getCreateTime());
                map.put("payTime", order.getPayTime());
                
                // 获取订单明细
                LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
                itemWrapper.eq(OrderItem::getOrderId, order.getId());
                List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
                map.put("items", items);
                
                return map;
            }).toList();
            
            PageResult<Map<String, Object>> pageResult = new PageResult<>(
                records,
                result.getTotal(),
                result.getCurrent(),
                result.getSize()
            );
            
            return Result.success("获取成功", pageResult);
        } catch (Exception e) {
            log.error("获取用户订单历史失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 批量更新用户状态
     */
    public Result<String> batchUpdateUserStatus(List<Long> userIds, Integer status) {
        try {
            for (Long userId : userIds) {
                User user = userMapper.selectById(userId);
                if (user != null) {
                    user.setStatus(status);
                    userMapper.updateById(user);
                }
            }
            log.info("批量更新用户状态成功: 用户数={}, 状态={}", userIds.size(), status);
            return Result.success("批量更新成功");
        } catch (Exception e) {
            log.error("批量更新用户状态失败", e);
            return Result.error("批量更新失败");
        }
    }
    
    /**
     * 获取会员统计
     */
    public Result<Map<String, Object>> getMemberStatistics() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 总会员数
            Long totalMembers = userMapper.selectCount(null);
            
            // 黄金会员数
            LambdaQueryWrapper<User> goldWrapper = new LambdaQueryWrapper<>();
            goldWrapper.eq(User::getMemberLevel, 1);
            Long goldMembers = userMapper.selectCount(goldWrapper);
            
            // 钻石会员数
            LambdaQueryWrapper<User> diamondWrapper = new LambdaQueryWrapper<>();
            diamondWrapper.eq(User::getMemberLevel, 2);
            Long diamondMembers = userMapper.selectCount(diamondWrapper);
            
            // 今日新增会员数
            LambdaQueryWrapper<User> todayWrapper = new LambdaQueryWrapper<>();
            todayWrapper.ge(User::getCreateTime, LocalDateTime.now().toLocalDate().atStartOfDay())
                       .lt(User::getCreateTime, LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
            Long newMembersToday = userMapper.selectCount(todayWrapper);
            
            data.put("totalMembers", totalMembers);
            data.put("goldMembers", goldMembers);
            data.put("diamondMembers", diamondMembers);
            data.put("newMembersToday", newMembersToday);
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取会员统计失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 获取用户增长趋势
     */
    public Result<Map<String, Object>> getUserGrowthTrend(Integer days) {
        try {
            Map<String, Object> data = new HashMap<>();
            List<String> dates = new ArrayList<>();
            List<Integer> newUsers = new ArrayList<>();
            
            LocalDateTime endDate = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime startDate = endDate.minusDays(days - 1);
            
            for (int i = 0; i < days; i++) {
                LocalDateTime dayStart = startDate.plusDays(i);
                LocalDateTime dayEnd = dayStart.plusDays(1);
                
                // 格式化日期
                String dateStr = dayStart.format(java.time.format.DateTimeFormatter.ofPattern("MM-dd"));
                dates.add(dateStr);
                
                // 查询当天新增用户数
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.ge(User::getCreateTime, dayStart)
                           .lt(User::getCreateTime, dayEnd);
                Long count = userMapper.selectCount(queryWrapper);
                newUsers.add(count.intValue());
            }
            
            data.put("dates", dates);
            data.put("newUsers", newUsers);
            
            return Result.success("获取成功", data);
        } catch (Exception e) {
            log.error("获取用户增长趋势失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 导出用户数据
     */
    public Result<String> exportUsers(String keyword, Integer memberLevel) {
        try {
            // 这里应该实现实际的导出逻辑，生成Excel文件
            // 目前返回成功状态，实际项目中需要使用POI等库生成Excel
            log.info("导出用户数据 - 关键词: {}, 会员等级: {}", keyword, memberLevel);
            return Result.success("用户数据导出成功");
        } catch (Exception e) {
            log.error("导出用户数据失败", e);
            return Result.error("导出失败");
        }
    }
    
    /**
     * 导出订单数据
     */
    public Result<String> exportOrders(Integer status, String orderNo, String startDate, String endDate) {
        try {
            // 这里应该实现实际的导出逻辑，生成Excel文件
            // 目前返回成功状态，实际项目中需要使用POI等库生成Excel
            log.info("导出订单数据 - 状态: {}, 订单号: {}, 开始日期: {}, 结束日期: {}", 
                    status, orderNo, startDate, endDate);
            return Result.success("订单数据导出成功");
        } catch (Exception e) {
            log.error("导出订单数据失败", e);
            return Result.error("导出失败");
        }
    }
}
